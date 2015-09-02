using System;
using System.Text;

namespace Noear.Snacks.Xml
{
    internal class XmlWriter
    {
        private StringBuilder _writer;

        public XmlWriter(StringBuilder writer)
        {
            _writer = writer;
        }


        public void WriteNodeStart(String nodeName)
        {
            _writer.Append('<');
            _writer.Append(nodeName);
            _writer.Append('>');
        }

        public void WriteNodeEnd(String nodeName)
        {
            _writer.Append('<');
            _writer.Append('/');
            _writer.Append(nodeName);
            _writer.Append('>');
        }

        public void WriteCDATAValue(String str)
        {
            if (str == null)
            {
                return;
            }

            _writer.Append("<![CDATA[");
            _writer.Append(str);
            _writer.Append("]]>");
        }

        public void WriteValue(String str)
        {
            if (str == null)
                return;

            int n = str.Length;
            for (int i = 0; i < n; i++)
            {
                if (str[i] == '<') //5个xml转义符
                {
                    _writer.Append("&lt;");
                }
                else if (str[i] == '>')
                {
                    _writer.Append("&gt;");
                }
                else if (str[i] == '&')
                {
                    _writer.Append("&amp;");
                }
                else
                {
                    //case '\"':
                    //    _writer.Write("&quot;");
                    //    continue;
                    //case '\'':
                    //    _writer.Write("&apos;");
                    //    continue;

                    _writer.Append(str[i]);
                }
            }
        }

        public void WriteValue(int value)
        {
            _writer.Append(value);
        }

        public void WriteValue(long value)
        {
            _writer.Append(value);
        }

        public void WriteValue(double value)
        {
            _writer.Append(value);
        }


        public void WriteValue(bool value)
        {
            if (ONode.BOOL_USE01)
            {
                _writer.Append(value ? "1" : "0");
            }
            else
            {
                _writer.Append(value ? "true" : "false");
            }
        }

        public void WriteValue(DateTime value)
        {
            _writer.Append(ONode.TIME_FORMAT_ACTION(value));
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
