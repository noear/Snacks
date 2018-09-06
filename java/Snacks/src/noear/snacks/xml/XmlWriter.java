package noear.snacks.xml;

import noear.snacks.ONode;
import noear.snacks.OValue;

/**
 * Created by noear on 14-6-11.
 */
public class XmlWriter {
    private StringBuilder _writer;

    public XmlWriter(StringBuilder writer)
    {
        _writer = writer;
    }


    public final void WriteNodeStart(String nodeName)
    {
        _writer.append('<');
        _writer.append(nodeName);
        _writer.append('>');
    }

    public final void WriteNodeEnd(String nodeName)
    {
        _writer.append('<');
        _writer.append('/');
        _writer.append(nodeName);
        _writer.append('>');
    }

    public final void WriteCDATAValue(String str)
    {
        if (str == null)
        {
            return;
        }

        _writer.append("<![CDATA[");
        _writer.append(str);
        _writer.append("]]>");
    }

    public final void WriteValue(String str)
    {
        if (str == null)
            return;

        int n = str.length();
        for (int i = 0; i < n; i++)
        {
            if (str.charAt(i) == '<') //5个xml转义符
            {
                _writer.append("&lt;");
            }
            else if (str.charAt(i) == '>')
            {
                _writer.append("&gt;");
            }
            else if (str.charAt(i) == '&')
            {
                _writer.append("&amp;");
            }
            else
            {
                //case '\"':
                //    _writer.Write("&quot;");
                //    continue;
                //case '\'':
                //    _writer.Write("&apos;");
                //    continue;

                _writer.append(str.charAt(i));
            }
        }
    }

    public final void WriteValue(int value)
    {
        _writer.append(value);
    }

    public final void WriteValue(long value)
    {
        _writer.append(value);
    }

    public final void WriteValue(double value)
    {
        _writer.append(value);
    }


    public final void WriteValue(boolean value)
    {
        if (ONode.BOOL_USE01)
        {
            _writer.append(value ? "1" : "0");
        }
        else
        {
            _writer.append(value ? "true" : "false");
        }
    }

    public final void WriteValue(java.util.Date value)
    {
        _writer.append(ONode.TIME_FORMAT_ACTION.run(value));
    }

    public final  void WriteValue(OValue val)
    {
        switch (val.type)
        {
            case Int:WriteValue(val.getInt());break;
            case Long:WriteValue(val.getLong());break;
            case Double:WriteValue(val.getDouble());break;
            case String:WriteValue(val.getString());break;
            case Boolean:WriteValue(val.getBoolean());break;
            case DateTime:WriteValue(val.getDate());break;
            case Null:WriteValue("");break;
        }
    }
}
