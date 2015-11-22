using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;

namespace Noear.Snacks.Json
{
    /// <summary>
    /// 支持{"pop":1}  {'pop':1}  {pop:1} 三种情况
    /// </summary>
    internal class JsonReader
    {
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
        private bool ObjTryRead(StringBuilder buffer_sb, int buffer_char,JsonToken token)
        {
            if (Token != JsonToken.ValueStart)
            {
                Token = token;
                return true;
            }
            else
            {
                buffer_sb.Append((char) buffer_char);
                return false;
            }
        }

        public bool read() 
        {
            Token = JsonToken.None;
            Value = null;
            int buffer_char = 0;
            ValueType value_type = ValueType.None;
            StringBuilder buffer_sb = new StringBuilder();

            while (true)
            {
                buffer_char = _reader.Read();

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
                            int c = _reader.Read();
                            switch (c)
                            {
                                case '\'':
                                    buffer_sb.Append('\'');
                                    break;

                                case '"':
                                    buffer_sb.Append('"');
                                    break;

                                case '/':
                                    buffer_sb.Append('/');
                                    break;

                                case 'n':
                                    buffer_sb.Append('\n');
                                    break;

                                case 'r':
                                    buffer_sb.Append('\r');
                                    break;

                                case 't':
                                    buffer_sb.Append('\t');
                                    break;

                                case 'f':
                                    buffer_sb.Append('\f');
                                    break;

                                case 'b':
                                    buffer_sb.Append('\b');
                                    break;

                                default:
                                    buffer_sb.Append((char) buffer_char);
                                    buffer_sb.Append((char) c);
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
                                Value = buffer_sb.ToString();
                                return true;
                            }

                            buffer_sb.Append((char) buffer_char);
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
                                Value = buffer_sb.ToString();
                                return true;
                            }

                            buffer_sb.Append((char) buffer_char);
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
                            _reader.Read();//true
                            _reader.Read();
                            buffer_char = _reader.Read();
                            Value = true;
                            return true;
                        }

                        buffer_sb.Append((char) buffer_char);
                    }
                    break;

                    case 'f':
                    {
                        if (Token != JsonToken.ValueStart)
                        {
                            Token = JsonToken.Boolean;
                            _reader.Read();//false
                            _reader.Read();
                            _reader.Read();
                            buffer_char = _reader.Read();
                            Value = false;
                            return true;
                        }

                        buffer_sb.Append((char) buffer_char);
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
                                buffer_char = _reader.Read();
                                if (buffer_char == '/') //结束
                                {
                                    outputDateTime(buffer_sb.ToString());
                                    return true;
                                }
                                else
                                    buffer_sb.Append((char) buffer_char);

                                if (buffer_char < 0) //出错
                                    return true;
                            }
                        }

                        buffer_sb.Append((char) buffer_char); //不需要先打入/
                    } break;

                    //#endregion

                    //#region datetime 或 null
                    case 'n':
                    {
                        buffer_sb.Append((char) buffer_char); //要先打入字符

                        if (Token != JsonToken.ValueStart)
                        {
                            buffer_sb.Append((char) _reader.Read());
                            buffer_sb.Append((char) _reader.Read());
                            buffer_sb.Append((char) (buffer_char = _reader.Read()));

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
                                    buffer_sb.Append((char) (buffer_char = _reader.Read()));

                                    if (buffer_char == ')') //结束
                                    {
                                        outputDateTime(buffer_sb.ToString());
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
                                buffer_sb.Append((char) buffer_char);

                                Token = JsonToken.ValueStart;
                                value_type = ValueType.Number;

                                while (true)
                                {
                                    buffer_char = _reader.Peek();

                                    if (buffer_char == 46) //小数点.
                                    {
                                        value_type = ValueType.Number_Double;
                                        buffer_sb.Append((char) _reader.Read());
                                        continue;
                                    }

                                    if ((buffer_char >= 48 && buffer_char <= 57)) //(0-9) 或 .
                                    {
                                        buffer_sb.Append((char) _reader.Read());
                                        continue;
                                    }

                                    //69:E,101:e
                                    if (buffer_char == 45 || buffer_char == 69 || buffer_char == 101)
                                    {
                                        buffer_sb.Append((char) _reader.Read());
                                        continue;
                                    }

                                    outputNumber(buffer_sb, value_type);
                                    return true;
                                }
                            }
                        }
                        else
                            buffer_sb.Append((char) buffer_char);

                    }
                    break;
                    //#endregion
                }
            }
        }

        public bool ReadName()
        {
            Token = JsonToken.None;
            Value = null;
            int buffer_char         = 0;
            ValueType value_type    = ValueType.None;
            StringBuilder buffer_sb = new StringBuilder();

            while (true)
            {
                buffer_char = _reader.Read();

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
                                Value = buffer_sb.ToString();

                                //if (last_buffer_char != '\\')
                                //{
                                return true;
                                //}
                            }

                            buffer_sb.Append((char)buffer_char);
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
                                Value = buffer_sb.ToString();
                                return true;
                            }

                            buffer_sb.Append((char)buffer_char);
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
                            Value = buffer_sb.ToString();
                            return true;
                        }

                        if (Token == JsonToken.ValueStart)
                            buffer_sb.Append((char)buffer_char);
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
                            buffer_sb.Append((char)buffer_char);

                    }
                    break;

                }
            }
        }

        private void outputNumber(StringBuilder buffer_sb, ValueType value_type)
        {
            if (value_type == ValueType.Number)
            {
                long temp = long.Parse(buffer_sb.ToString());

                if (temp > int.MaxValue)
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
                Value = double.Parse(buffer_sb.ToString());
            }
        }

        private void outputDateTime(String buffer_str)
        {
            Match m1 = Regex.Match(buffer_str, @"\d+", RegexOptions.Compiled);

            if (m1.Success)
            {
                DateTime temp = new DateTime(long.Parse(m1.Value));
                if (m1.Groups.Count > 1)
                {
                    int kind = int.Parse(m1.Groups[1].Value);
                    int _op = (buffer_str.IndexOf('+') > 0 ? 1 : -1);
                    if (kind > 100)
                    {
                        int kind_m = kind - (kind / 100) * 100;

                        temp.AddHours(_op * kind / 100);
                        temp.AddMinutes(_op * kind_m);
                    }
                    else
                        temp.AddHours(_op * kind);
                }

                Value = temp;
            }
            else
                Value = DateTime.MinValue;
        }
    }
}
