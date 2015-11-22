﻿using Noear.Snacks;
using System;
using System.Text;

namespace Noear.Snacks.Json
{
    internal class JsonWriter
    {
        public JsonWriter(StringBuilder writer)
        {
            _Writer = writer;
        }

        private bool _LastIsEnd = false;
        private StringBuilder _Writer;
        public void WriteObjectStart()
        {
            if (_LastIsEnd)
            {
                _Writer.Append(',');
            }

            _Writer.Append('{');
            _LastIsEnd = false;
        }
        public void WriteObjectEnd()
        {
            _Writer.Append('}');
            _LastIsEnd = true;
        }
        public void WriteArrayStart()
        {
            if (_LastIsEnd)
            {
                _Writer.Append(',');
            }

            _Writer.Append('[');
            _LastIsEnd = false;
        }

        public void WriteArrayEnd()
        {
            _Writer.Append(']');
            _LastIsEnd = true;
        }

        public void WritePropertyName(String name)
        {
            if (_LastIsEnd)
            {
                _Writer.Append(',');
            }

            _Writer.Append('\"');
            _Writer.Append(name);
            _Writer.Append('\"');
            _Writer.Append(':');

            _LastIsEnd = false;
        }

        private void OnWriteBef()
        {
            if (_LastIsEnd) //是否上一个元素已结束
            {
                _Writer.Append(',');
            }
        }

        public void WriteValue(String val)
        {
            OnWriteBef();

            if (val == null)
            {
                _Writer.Append(ONode.NULL_DEFAULT);
            }
            else
            {
                _Writer.Append('\"');

                int n = val.Length;
                char c;
                for (int i = 0; i < n; i++)
                {
                    c = val[i];
                    switch (c)
                    {
                        case '\\':
                            _Writer.Append("\\\\"); //20110809
                            break;

                        case '\"':
                            _Writer.Append("\\\"");
                            break;

                        case '\n':
                            _Writer.Append("\\n");
                            break;

                        case '\r':
                            _Writer.Append("\\r");
                            break;

                        case '\t':
                            _Writer.Append("\\t");
                            break;

                        case '\f':
                            _Writer.Append("\\f");
                            break;

                        case '\b':
                            _Writer.Append("\\b");
                            break;

                        default:
                            _Writer.Append(c);
                            break;
                    }
                }

                _Writer.Append('\"');
            }
            _LastIsEnd = true;
        }

        public void WriteValue(bool val)
        {
            OnWriteBef();
            if (ONode.BOOL_USE01)
            {
                _Writer.Append(val ? "1" : "0");
            }
            else
            {
                _Writer.Append(val ? "true" : "false");
            }
            _LastIsEnd = true;
        }

        public void WriteValue(double val)
        {
            OnWriteBef();
            _Writer.Append(val);
            _LastIsEnd = true;
        }

        public void WriteValue(int val)
        {
            OnWriteBef();
            _Writer.Append(val);
            _LastIsEnd = true;
        }

        public void WriteValue(long val)
        {
            OnWriteBef();
            _Writer.Append(val);
            _LastIsEnd = true;
        }

        public void WriteValue(DateTime val)
        {
            OnWriteBef();
            _Writer.Append('\"');
            _Writer.Append(ONode.TIME_FORMAT_ACTION(val));
            _Writer.Append('\"');
            _LastIsEnd = true;
        }

        public void WriteValue(OValue val)
        {
            switch (val.type)
            {
                case OValueType.Int: WriteValue(val.getInt()); break;
                case OValueType.Long: WriteValue(val.getLong()); break;
                case OValueType.Double: WriteValue(val.getDouble()); break;
                case OValueType.String: WriteValue(val.getString()); break;
                case OValueType.Boolean: WriteValue(val.getBoolean()); break;
                case OValueType.DateTime: WriteValue(val.getDate()); break;
                case OValueType.Null: WriteValue(""); break;
            }
        }
    }
}
