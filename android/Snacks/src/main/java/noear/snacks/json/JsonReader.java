package noear.snacks.json;

import noear.snacks.exts.StringReader;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by noear on 14-6-13.
 */
public class JsonReader {
    StringReader _reader;

    public JsonReader(String json_text)
    {
        _reader =new StringReader(json_text);
    }


    public Object Value;
    public JsonToken Token;


    /// <summary>
    /// try read obj
    /// </summary>
    private boolean ObjTryRead(StringBuilder buffer_sb, int buffer_char,JsonToken token)
    {
        if (Token != JsonToken.ValueStart)
        {
            Token = token;
            return true;
        }
        else
        {
            buffer_sb.append((char) buffer_char);
            return false;
        }
    }

    public boolean read() throws IOException
    {
        Token = JsonToken.None;
        Value = null;
        int buffer_char = 0;
        ValueType value_type = ValueType.None;
        StringBuilder buffer_sb = new StringBuilder();

        while (true)
        {
            buffer_char = _reader.read();

            if (buffer_char == -1)
            {
                Token = JsonToken.End;
                return true;
            }

            char temp = (char)buffer_char;

            switch (temp)
            {
                //#region object,array
                case '{':
                {
                    if (ObjTryRead(buffer_sb, buffer_char, JsonToken.ObjectStart))
                        return true;
                } break;
                case '}':
                {
                    if (ObjTryRead(buffer_sb, buffer_char, JsonToken.ObjectEnd))
                        return true;
                } break;
                case '[':
                {
                    if (ObjTryRead(buffer_sb, buffer_char, JsonToken.ArrayStart))
                        return true;
                } break;
                case ']':
                {
                    if (ObjTryRead(buffer_sb, buffer_char, JsonToken.ArrayEnd))
                        return true;
                } break;
                //#endregion

                case '\\':
                {
                    if (Token == JsonToken.ValueStart) //转义符处理
                    {
                        int c = _reader.read();
                        switch (c)
                        {
                            case '\'':
                                buffer_sb.append('\'');
                                break;

                            case '"':
                                buffer_sb.append('"');
                                break;

                            case '/':
                                buffer_sb.append('/');
                                break;

                            case 'n':
                                buffer_sb.append('\n');
                                break;

                            case 'r':
                                buffer_sb.append('\r');
                                break;

                            case 't':
                                buffer_sb.append('\t');
                                break;

                            case 'f':
                                buffer_sb.append('\f');
                                break;

                            case 'b':
                                buffer_sb.append('\b');
                                break;

                            default:
                                buffer_sb.append((char) buffer_char);
                                buffer_sb.append((char) c);
                                break;

                        }
                    }
                }
                break;

                //#region string
                case '\"':
                {
                    if (Token != JsonToken.ValueStart)
                    {
                        Token = JsonToken.ValueStart;
                        value_type = ValueType.String2;
                    }
                    else
                    {
                        if (value_type == ValueType.String2)
                        {
                            Token = JsonToken.String;
                            Value = buffer_sb.toString();
                            return true;
                        }

                        buffer_sb.append((char) buffer_char);
                    }
                }
                break;

                case '\'':
                {
                    if (Token != JsonToken.ValueStart)
                    {
                        Token = JsonToken.ValueStart;
                        value_type = ValueType.String1;
                    }
                    else
                    {
                        if (value_type == ValueType.String1)
                        {
                            Token = JsonToken.String;
                            Value = buffer_sb.toString();
                            return true;
                        }

                        buffer_sb.append((char) buffer_char);
                    }
                }
                break;
                //#endregion

                //#region bool
                case 't':
                {
                    if (Token != JsonToken.ValueStart)
                    {
                        Token = JsonToken.Boolean;
                        _reader.read();//true
                        _reader.read();
                        buffer_char = _reader.read();
                        Value = true;
                        return true;
                    }

                    buffer_sb.append((char) buffer_char);
                }
                break;

                case 'f':
                {
                    if (Token != JsonToken.ValueStart)
                    {
                        Token = JsonToken.Boolean;
                        _reader.read();//false
                        _reader.read();
                        _reader.read();
                        buffer_char = _reader.read();
                        Value = false;
                        return true;
                    }

                    buffer_sb.append((char) buffer_char);
                } break;
                //#endregion

                //#region datetime
                case '/':
                {
                    if (Token != JsonToken.ValueStart)
                    {
                        Token = JsonToken.DateTime;

                        while (true)
                        {
                            buffer_char = _reader.read();
                            if (buffer_char == '/') //结束
                            {
                                outputDateTime(buffer_sb.toString());
                                return true;
                            }
                            else
                                buffer_sb.append((char) buffer_char);

                            if (buffer_char < 0) //出错
                                return true;
                        }
                    }

                    buffer_sb.append((char) buffer_char); //不需要先打入/
                } break;

                //#endregion

                //#region datetime 或 null
                case 'n':
                {
                    buffer_sb.append((char) buffer_char); //要先打入字符

                    if (Token != JsonToken.ValueStart)
                    {
                        buffer_sb.append((char) _reader.read());
                        buffer_sb.append((char) _reader.read());
                        buffer_sb.append((char) (buffer_char = _reader.read()));

                        if (buffer_char == 'l') //为null
                        {
                            Value = null;
                            Token = JsonToken.Null;
                            return true;
                        }
                        else //为时间
                        {
                            while (true)
                            {
                                buffer_sb.append((char) (buffer_char = _reader.read()));

                                if (buffer_char == ')') //结束
                                {
                                    outputDateTime(buffer_sb.toString());
                                    return true;
                                }

                                if (buffer_char < 0) //出错
                                    return false;
                            }
                        }
                    }
                } break;
                //#endregion

                //#region number
                default: //对字符串处理的补充
                {
                    // 值由::/,字母,",',数字 开始
                    //字符串::以",'结束
                    //否则以 ,: ]}结束

                    if (Token != JsonToken.ValueStart) //如果还没有开始值
                    {
                        //48 === 0
                        //45 === -（负号）20110809
                        if ((buffer_char >= 48 && buffer_char <= 57) || buffer_char == 45) //0-9的值
                        {
                            buffer_sb.append((char) buffer_char);

                            Token = JsonToken.ValueStart;
                            value_type = ValueType.Number;

                            while (true)
                            {
                                buffer_char = _reader.peek();

                                if (buffer_char == 46) //小数点.
                                {
                                    value_type = ValueType.Number_Double;
                                    buffer_sb.append((char) _reader.read());
                                    continue;
                                }

                                if ((buffer_char >= 48 && buffer_char <= 57)) //(0-9) 或 .
                                {
                                    buffer_sb.append((char) _reader.read());
                                    continue;
                                }

                                //69:E,101:e
                                if (buffer_char == 45 || buffer_char == 69 || buffer_char == 101)
                                {
                                    buffer_sb.append((char) _reader.read());
                                    continue;
                                }

                                outputNumber(buffer_sb, value_type);
                                return true;
                            }
                        }
                    }
                    else
                        buffer_sb.append((char) buffer_char);

                }
                break;
                //#endregion
            }
        }
    }

    public boolean ReadName() throws IOException
    {
        Token = JsonToken.None;
        Value = null;
        int buffer_char         = 0;
        ValueType value_type    = ValueType.None;
        StringBuilder buffer_sb = new StringBuilder();

        while (true)
        {
            buffer_char = _reader.read();

            if (buffer_char == -1)
            {
                Token = JsonToken.End;
                return true;
            }

            char temp = (char)buffer_char;

            switch (temp)
            {
                //#region object,array
                case '{':
                {
                    if (ObjTryRead(buffer_sb, buffer_char, JsonToken.ObjectStart))
                    {
                        return true;
                    }
                } break;
                case '}':
                {
                    if (ObjTryRead(buffer_sb, buffer_char, JsonToken.ObjectEnd))
                    {
                        return true;
                    }
                } break;
                case '[':
                {
                    if (ObjTryRead(buffer_sb, buffer_char, JsonToken.ArrayStart))
                    {
                        return true;
                    }
                } break;
                case ']':
                {
                    if (ObjTryRead(buffer_sb, buffer_char, JsonToken.ArrayEnd))
                    {
                        return true;
                    }
                } break;

                //case ',':
                //    {
                //        Token = JsonToken.End;

                //        return false;
                //    }break;
                //#endregion

                //#region string
                case '\"':
                {
                    if (Token != JsonToken.ValueStart)
                    {
                        Token = JsonToken.ValueStart;
                        value_type = ValueType.String2;
                    }
                    else
                    {
                        if (value_type == ValueType.String2)
                        {
                            Token = JsonToken.String;
                            Value = buffer_sb.toString();

                            //if (last_buffer_char != '\\')
                            //{
                            return true;
                            //}
                        }

                        buffer_sb.append((char) buffer_char);
                    }
                }
                break;

                case '\'':
                {
                    if (Token != JsonToken.ValueStart)
                    {
                        Token = JsonToken.ValueStart;
                        value_type = ValueType.String1;
                    }
                    else
                    {
                        if (value_type == ValueType.String1)
                        {
                            Token = JsonToken.String;
                            Value = buffer_sb.toString();
                            return true;
                        }

                        buffer_sb.append((char) buffer_char);
                    }
                }
                break;
                //#endregion

                case ' ':
                case ':':
                {
                    if (value_type == ValueType.String3)
                    {
                        Token = JsonToken.String;
                        Value = buffer_sb.toString();
                        return true;
                    }

                    if (Token == JsonToken.ValueStart)
                        buffer_sb.append((char) buffer_char);
                }
                break;

                default: //对字符串处理的补充
                {
                    // 值由::/,字母,",',数字 开始
                    //字符串::以",'结束
                    //否则以 ,: ]}结束
                    if (Token != JsonToken.ValueStart)
                    {
                        if (buffer_char == 36 || buffer_char == 95 || (buffer_char >= 65 && buffer_char <= 90) || (buffer_char >= 97 && buffer_char <= 122))
                        {
                            Token = JsonToken.ValueStart;
                            value_type = ValueType.String3;
                        }
                    }

                    if (Token == JsonToken.ValueStart)
                        buffer_sb.append((char) buffer_char);

                }
                break;

            }
        }
    }

    private void outputNumber(StringBuilder buffer_sb, ValueType value_type)
    {
        if (value_type == ValueType.Number)
        {
            long temp = Long.parseLong(buffer_sb.toString());

            if (temp > Integer.MAX_VALUE)
            {
                Token = JsonToken.Long;
                Value = temp;
            }
            else
            {
                Token = JsonToken.Int;
                Value = (int)temp;
            }
        }
        else
        {
            Token = JsonToken.Double;
            Value = Double.parseDouble(buffer_sb.toString());
        }
    }

    private void outputDateTime(String buffer_str)
    {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher m1 = pattern.matcher(buffer_str);

        if (m1.matches())
        {
            Date temp = new Date(Long.parseLong(m1.group()));
            if (m1.groupCount() > 1)
            {

                int kind = Integer.parseInt(m1.group(1));
                int _op = (buffer_str.indexOf('+') > 0 ? 1 : -1);
//                if (kind > 100)
//                {
//                    int kind_m = kind - (kind / 100) * 100;
//
//
//
//                    temp.AddHours(_op * kind / 100);
//                    temp.AddMinutes(_op * kind_m);
//                }
//                else
//                    temp.AddHours(_op * kind);
            }

            Value = temp;
        }
        else
            Value = new Date();
    }

}
