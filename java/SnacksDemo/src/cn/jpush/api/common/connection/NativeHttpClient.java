package cn.jpush.api.common.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.common.resp.ResponseWrapper;

/**
 * The implementation has no connection pool mechanism, used origin java connection.
 * 
 * 本实现没有连接池机制，基于 Java 原始的 HTTP 连接实现。
 * 
 * 遇到连接超时，会自动重连指定的次数（默认为 3）；如果是读取超时，则不会自动重连。
 * 
 * 可选支持 HTTP 代理，同时支持 2 种方式：1) HTTP 头上加上 Proxy-Authorization 信息；2）全局配置 Authenticator.setDefault；
 */
public class NativeHttpClient implements IHttpClient {
    private static final Logger LOG = LoggerFactory.getLogger(NativeHttpClient.class);
    private static final String KEYWORDS_CONNECT_TIMED_OUT = "connect timed out";
    private static final String KEYWORDS_READ_TIMED_OUT = "Read timed out";
    
    private int _maxRetryTimes = 0;
    private String _authCode;
    private HttpProxy _proxy;
    
    /**
     * 默认的重连次数是 3
     */
    public NativeHttpClient(String authCode) {
        this(authCode, DEFAULT_MAX_RETRY_TIMES, null);
    }
    
    public NativeHttpClient(String authCode, int maxRetryTimes, HttpProxy proxy) {
        this._maxRetryTimes = maxRetryTimes;
        LOG.info("Created instance with _maxRetryTimes = " + _maxRetryTimes);
        
        this._authCode = authCode;
        this._proxy = proxy;
        
        if ( null != _proxy && _proxy.isAuthenticationNeeded()) {
        	Authenticator.setDefault(new SimpleProxyAuthenticator(
                _proxy.getUsername(), _proxy.getPassword()));
        }
        
        initSSL();
    }
    
    public ResponseWrapper sendGet(String url) 
            throws APIConnectionException, APIRequestException {
		return doRequest(url, null, RequestMethod.GET);
	}
    
    public ResponseWrapper sendDelete(String url) 
            throws APIConnectionException, APIRequestException {
        return doRequest(url, null, RequestMethod.DELETE);
    }
    
    public ResponseWrapper sendPost(String url, String content) 
            throws APIConnectionException, APIRequestException {
		return doRequest(url, content, RequestMethod.POST);
	}

	public ResponseWrapper sendPut(String url, String content)
			throws APIConnectionException, APIRequestException {
		return doRequest(url, content, RequestMethod.PUT);
	}
        
    public ResponseWrapper doRequest(String url, String content, 
            RequestMethod method) throws APIConnectionException, APIRequestException {
        ResponseWrapper response = null;
        for (int retryTimes = 0; ; retryTimes++) {
            try {
                response = _doRequest(url, content, method);
                break;
            } catch (SocketTimeoutException e) {
                if (KEYWORDS_READ_TIMED_OUT.equals(e.getMessage())) {
                    // Read timed out.  For push, maybe should not re-send.
                    throw new APIConnectionException(READ_TIMED_OUT_MESSAGE, e, true);
                } else {    // connect timed out
                    if (retryTimes >= _maxRetryTimes) {
                        throw new APIConnectionException(CONNECT_TIMED_OUT_MESSAGE, e, retryTimes);
                    } else {
                        LOG.debug("connect timed out - retry again - " + (retryTimes + 1));
                    }
                }
            }
        }
        return response;
    }
    
    private ResponseWrapper _doRequest(String url, String content, 
            RequestMethod method) throws APIConnectionException, APIRequestException, 
            SocketTimeoutException {
    	
        LOG.debug("Send request - " + method.toString() + " "+ url);
        if (null != content) {
            LOG.debug("Request Content - " + content);
        }
        
		HttpURLConnection conn = null;
		OutputStream out = null;
		StringBuffer sb = new StringBuffer();
		ResponseWrapper wrapper = new ResponseWrapper();
		
		try {
			URL aUrl = new URL(url);
			
			if (null != _proxy) {
			    conn = (HttpURLConnection) aUrl.openConnection(_proxy.getNetProxy());
			    if (_proxy.isAuthenticationNeeded()) {
			        conn.setRequestProperty("Proxy-Authorization", _proxy.getProxyAuthorization());
			    }
			} else {
			    conn = (HttpURLConnection) aUrl.openConnection();
			}
			
			conn.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
			conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
			conn.setUseCaches(false);
			conn.setRequestMethod(method.name());
			conn.setRequestProperty("User-Agent", JPUSH_USER_AGENT);
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Accept-Charset", CHARSET);
			conn.setRequestProperty("Charset", CHARSET);
			conn.setRequestProperty("Authorization", _authCode);
			conn.setRequestProperty("Content-Type", CONTENT_TYPE_JSON);

			if (RequestMethod.GET == method) {
			    conn.setDoOutput(false);
			} else if (RequestMethod.DELETE == method) {
			    conn.setDoOutput(false);
			} else if (RequestMethod.POST == method || RequestMethod.PUT == method) {
                conn.setDoOutput(true);
                byte[] data = content.getBytes(CHARSET);
				conn.setRequestProperty("Content-Length", String.valueOf(data.length));
	            out = conn.getOutputStream();
				out.write(data);
	            out.flush();
			}
            
            int status = conn.getResponseCode();
            InputStream in = null;
            if (status / 100 == 2) {
                in = conn.getInputStream();
            } else {
                in = conn.getErrorStream();
            }
            
            if (null != in) {
	            InputStreamReader reader = new InputStreamReader(in, CHARSET);
	            char[] buff = new char[1024];
	            int len;
	            while ((len = reader.read(buff)) > 0) {
	                sb.append(buff, 0, len);
	            }
            }
            
            String responseContent = sb.toString();
            wrapper.responseCode = status;
            wrapper.responseContent = responseContent;
            
            String quota = conn.getHeaderField(RATE_LIMIT_QUOTA);
            String remaining = conn.getHeaderField(RATE_LIMIT_Remaining);
            String reset = conn.getHeaderField(RATE_LIMIT_Reset);
            wrapper.setRateLimit(quota, remaining, reset);
            
            if (status >= 200 && status < 300) {
				LOG.debug("Succeed to get response OK - responseCode:" + status);
				LOG.debug("Response Content - " + responseContent);
				
            } else if (status >= 300 && status < 400) {
                LOG.warn("Normal response but unexpected - responseCode:" + status + ", responseContent:" + responseContent);
                
			} else {
			    LOG.warn("Got error response - responseCode:" + status + ", responseContent:" + responseContent);
			    
			    switch (status) {
			    case 400:
			        LOG.error("Your request params is invalid. Please check them according to error message.");
	                wrapper.setErrorObject();
			        break;
                case 401:
                    LOG.error("Authentication failed! Please check authentication params according to docs.");
                    wrapper.setErrorObject();
                    break;
			    case 403:
			        LOG.error("Request is forbidden! Maybe your appkey is listed in blacklist or your params is invalid.");
	                wrapper.setErrorObject();
			        break;
				case 404:
					LOG.error("Request page is not found! Maybe your params is invalid.");
					wrapper.setErrorObject();
					break;
			    case 410:
                    LOG.error("Request resource is no longer in service. Please according to notice on official website.");
			        wrapper.setErrorObject();
			    case 429:
			        LOG.error("Too many requests! Please review your appkey's request quota.");
	                wrapper.setErrorObject();
			        break;
			    case 500:
			    case 502:
			    case 503:
			    case 504:
			        LOG.error("Seems encountered server error. Maybe JPush is in maintenance? Please retry later.");
			        break;
			    default:
                    LOG.error("Unexpected response.");
			    }
			    
			    throw new APIRequestException(wrapper);
			}
            
		} catch (SocketTimeoutException e) {
		    if (e.getMessage().contains(KEYWORDS_CONNECT_TIMED_OUT)) {
	            throw e;
		    } else if (e.getMessage().contains(KEYWORDS_READ_TIMED_OUT)) {
		        throw new SocketTimeoutException(KEYWORDS_READ_TIMED_OUT);
		    }
            LOG.debug(IO_ERROR_MESSAGE, e);
		    throw new APIConnectionException(IO_ERROR_MESSAGE, e);
            
        } catch (IOException e) {
            LOG.debug(IO_ERROR_MESSAGE, e);
            throw new APIConnectionException(IO_ERROR_MESSAGE, e);
            
		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					LOG.error("Failed to close stream.", e);
				}
			}
			if (null != conn) {
				conn.disconnect();
			}
		}
		
		return wrapper;
	}

	protected void initSSL() {
        TrustManager[] tmCerts = new TrustManager[1];
        tmCerts[0] = new SimpleTrustManager();
		try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, tmCerts, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
			
			HostnameVerifier hostnameVerifier = new SimpleHostnameVerifier();
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
		} catch (Exception e) {
			LOG.error("Init SSL error", e);
		}
	}


	private static class SimpleHostnameVerifier implements HostnameVerifier {

	    @Override
	    public boolean verify(String hostname, SSLSession session) {
	        return true;
	    }

	}

	private static class SimpleTrustManager implements TrustManager, X509TrustManager {

	    @Override
	    public void checkClientTrusted(X509Certificate[] chain, String authType)
	            throws CertificateException {
	        return;
	    }

	    @Override
	    public void checkServerTrusted(X509Certificate[] chain, String authType)
	            throws CertificateException {
	        return;
	    }

	    @Override
	    public X509Certificate[] getAcceptedIssuers() {
	        return null;
	    }
	}
	
    private static class SimpleProxyAuthenticator extends Authenticator {
        private String username;
        private String password;

        public SimpleProxyAuthenticator(String username, String password) {
            this.username = username;
            this.password = password;
        }
        
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(
                    this.username,
                    this.password.toCharArray());
        }
    }
}
