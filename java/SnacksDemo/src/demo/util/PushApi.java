package demo.util;

import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import noear.snacks.ONode;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PushApi {

    public static boolean isApnsProduction=true;

    private static String masterSecret = "";
    private static String appKey = "";

    private static JPushClient jpushClient = null;

    public static boolean deleteAlias(long userID) throws Exception
    {
        //1.创建推送客户端
        if (jpushClient == null)
            jpushClient = new JPushClient(masterSecret, appKey, 3);//3为最大重试次数

        return jpushClient.deleteAlias(userID+"","android,ios,winphone").isResultOK();
    }

    /*推送[提醒]*/
    public static boolean pushAlert(long userID, String alert) {
        try {
            return pushMessage(userID, alert, null);
        } catch (Exception ex) {
            return false;
        }
    }

    /*推送[透传数据]*/
    public static boolean pushData(long userID, ONode data) throws Exception {
        return pushMessage(userID, null, data);
    }

    /*尝试推送完整消息（[提醒]＋[透传数据]）；如果不存在只推[推醒]*/
    public static boolean pushAutoMessage(long userID,String alert,ONode data) throws SQLException
    {
        if (userID == 0)
            return false;

//        if(!UserDb.isOnline(userID))
            return pushMessage(userID,alert,data);
//        else
//            return pushMessage(userID, null, data);
    }

    /*推送完整消息（[提醒]＋[透传数据]）*/
    public static boolean pushMessage(long userID, String alert, ONode data)  {

        try {
            return doPushMessage(userID, alert, data);
        }catch (Exception ex)
        {
//            Loger.LogPush("出错 - ", ex);
            return false;
        }
    }


    /*推送完整消息（[提醒]＋[透传数据]）*/
    private static boolean doPushMessage(long userID, String alert, ONode data) {

        if (userID == 0)
            return false;

        if (alert == null && data == null)
            return false;

        if(alert!=null){
            int hour = 12;//DateUtil.getHour(new Date());
            if(hour>22 || hour<7) //半夜不发alert
                return false;
        }


        //1.创建推送客户端
        if (jpushClient == null)
            jpushClient = new JPushClient(masterSecret, appKey, 3);//3为最大重试次数

//        if(isApnsProduction==false) { //说明是测试
//            if (alert == null)
//                Loger.LogPush("::alert - null");
//            else
//                Loger.LogPush("::alert - " + alert);
//
//            if (data == null)
//                Loger.LogPush("::data - null");
//            else
//                Loger.LogPush("::data - " + data.toJson());
//        }

        //2.构建推送对象
        PushPayload payload = buildPushPayload(userID, alert, data);

        if (payload == null)
            return false;
        else {
            //3.发送
            try {
                PushResult result = jpushClient.sendPush(payload);

//                Loger.LogPush("IN - " + payload);
//                Loger.LogPush("OUT - " + result + "\r\n");

                return result.isResultOK();
            } catch (Exception e) {
//                Loger.LogPush("出错 - ", e);
            }

            return false;
        }
    }

    private static PushPayload buildPushPayload(long userID, String alert, ONode data) {

        //1.预处理
        String data1 = null;
        if (data != null && data.count() > 0)
            data1 = data.toJson();

        String alert1 = null;
        if (alert != null && alert.length() > 0)
            alert1 = alert;

        //2.建构消息体
        if (alert1 != null && data1 != null) {
            //2.1.同时推送提醒和数据

            int cmd = data.get("CMD").getInt();

            data.remove("CONTENT");
            data1 = data.toJson();

            PushPayload.Builder builder = PushPayload.newBuilder()
                    .setPlatform(Platform.android_ios())            //构建推送对象为ios和android
                    .setAudience(Audience.alias(Long.toString(userID)))   //给别名为alias1的用户推送
                    .setNotification(Notification.newBuilder()
                            .setAlert(alert1)
                            .addPlatformNotification(AndroidNotification.newBuilder()
                                    .addExtra("content", data1)
                                    .build())
                            .addPlatformNotification(IosNotification.newBuilder()
                                    .incrBadge(1)
                                    .addExtra("content", data1)
                                    .setSound("default")
                                    .build())
                            .build())
                    .setOptions(Options.newBuilder().setApnsProduction((userID > 10020 ? true : isApnsProduction)).build());


            return builder.build();

        } else {
            if (data1 != null) {
                //2.2.只推送透传数据
                return PushPayload.newBuilder()
                        .setPlatform(Platform.android_ios())            //构建推送对象为ios和android
                        .setAudience(Audience.alias(Long.toString(userID)))            //给别名为alias1的用户推送
                        .setMessage(Message.content(data1))           //data1
                        .setOptions(Options.newBuilder().setApnsProduction((userID > 10020 ? true : isApnsProduction)).build())
                        .build();
            }

            if (alert1 != null) { //此哪data2 == null
                //2.3.只推送提醒
                return PushPayload.newBuilder()
                        .setPlatform(Platform.android_ios())            //构建推送对象为ios和android
                        .setAudience(Audience.alias(Long.toString(userID)))            //给别名为alias1的用户推送
                        .setNotification(Notification.newBuilder()
                                .setAlert(alert1)
                                .addPlatformNotification(AndroidNotification.newBuilder()
                                        .build())
                                .addPlatformNotification(IosNotification.newBuilder()
                                        .incrBadge(1)
                                        .setSound("default")
                                        .build())
                                .build())
                        .setOptions(Options.newBuilder().setApnsProduction((userID > 10020 ? true : isApnsProduction)).build())
                        .build();
            }

            return null;
        }
    }

    //
    //---------------------------------------
    //

    /*推送完整消息（[提醒]＋[透传数据]）*/
    public static boolean pushMessage2(String alert, ONode data,List<Long> userIDs) {

        if (userIDs.size() == 0)
            return false;

        if (alert == null && data == null)
            return false;

        if(alert!=null){
            int hour = 12;//DateUtil.getHour(new Date());
            if(hour>22 || hour<7) //半夜不发alert
                return false;
        }


        //1.创建推送客户端
        if (jpushClient == null)
            jpushClient = new JPushClient(masterSecret, appKey, 3);//3为最大重试次数

        //2.构建推送对象
        PushPayload payload = buildPushPayload2(alert, data, userIDs);

        if (payload == null)
            return false;
        else {
            //3.发送
            try {
                PushResult result = jpushClient.sendPush(payload);

//                Loger.LogPush("IN - " + payload);
//                Loger.LogPush("OUT - " + result + "\r\n");

                return result.isResultOK();
            } catch (Exception e) {
                e.printStackTrace();
//                Loger.LogPush("出错 - ", e);
            }

            return false;
        }
    }

    //批量推送时用
    private static PushPayload buildPushPayload2(String alert, ONode data,List<Long> userIDs) {

        //1.预处理
        String data1 = null;
        if (data != null && data.count() > 0)
            data1 = data.toJson();

        String alert1 = null;
        if (alert != null && alert.length() > 0)
            alert1 = alert;

        //2.建构消息体
        if (alert1 != null && data1 != null) {
            //2.1.同时推送提醒和数据

            int cmd = data.get("CMD").getInt();

            data.remove("CONTENT");
            data1 = data.toJson();

            PushPayload.Builder builder = PushPayload.newBuilder()
                    .setPlatform(Platform.android_ios())            //构建推送对象为ios和android
                    .setAudience(alias2(userIDs))   //给别名为alias1的用户推送
                    .setNotification(Notification.newBuilder()
                            .setAlert(alert1)
                            .addPlatformNotification(AndroidNotification.newBuilder()
                                    .addExtra("content", data1)
                                    .build())
                            .addPlatformNotification(IosNotification.newBuilder()
                                    .incrBadge(1)
                                    .addExtra("content", data1)
                                    .setSound("default")
                                    .build())
                            .build())
                    .setOptions(Options.newBuilder().setApnsProduction(true).build());

//            if(cmd!=12)
//                builder.setMessage(Message.content(data1));           //data1

            return builder.build();

        } else {
            if (data1 != null) {
                //2.2.只推送透传数据
                return PushPayload.newBuilder()
                        .setPlatform(Platform.android_ios())            //构建推送对象为ios和android
                        .setAudience(alias2(userIDs))            //给别名为alias1的用户推送
                        .setMessage(Message.content(data1))           //data1
                        .setOptions(Options.newBuilder().setApnsProduction(true).build())
                        .build();
            }

            if (alert1 != null) { //此哪data2 == null
                //2.3.只推送提醒
                return PushPayload.newBuilder()
                        .setPlatform(Platform.android_ios())            //构建推送对象为ios和android
                        .setAudience(alias2(userIDs))            //给别名为alias1的用户推送
                        .setNotification(Notification.newBuilder()
                                .setAlert(alert1)
                                .addPlatformNotification(AndroidNotification.newBuilder()
                                        .build())
                                .addPlatformNotification(IosNotification.newBuilder()
                                        .incrBadge(1)
                                        .setSound("default")
                                        .build())
                                .build())
                        .setOptions(Options.newBuilder().setApnsProduction(true).build())
                        .build();
            }

            return null;
        }
    }

    private static Audience alias2(List<Long> users)
    {
        List<String>  list = new ArrayList<String>();
        for(long u : users) {
            list.add(Long.toString(u));
        }

        return Audience.alias(list);
    }
}
