package noear.snacks.xml;

/**
 * Created by noear on 14-6-13.
 */
public class XmlReader {
    String xml_text;
    int xml_index, xml_count;

    public String Value;
    public XmlToken Token;

    public XmlReader(String  _xml_text)
    {
        xml_text  = _xml_text;
        xml_count = _xml_text.length();
    }

    //------------------------
    private int text_read()
    {
        if (xml_index >= xml_count)
            return -1;

        return xml_text.charAt(xml_index++);
    }

    private int text_peek()
    {
        return xml_text.charAt(xml_index);
    }

    private void text_jump(int len)
    {
        xml_index += len+1;
    }

    private void text_jump(char _char)
    {
        xml_index = xml_text.indexOf(_char, xml_index)+1;
    }

    //------------------------
    private void ReadName()
    {
        int temp = xml_text.indexOf('>', xml_index);
        if (temp < 1)
            this.Value = "";
        else
        {
            this.Value = xml_text.substring(xml_index, temp);
            xml_index = temp+1;
        }
    }

    private void ReadCDATA()
    {
        int xml_index2 = xml_index;

        while (true)
        {
            xml_index = xml_text.indexOf(']', xml_index);

            if (xml_text.charAt(xml_index + 1) == ']' && xml_text.charAt(xml_index + 2) == '>')
            {
                this.Value = xml_text.substring(xml_index2, xml_index);
                xml_index = xml_index + 3;
                return;
            }
            else
                xml_index++;
        }
    }

    private void ReadValue()
    {
        int temp = xml_text.indexOf('<', xml_index);
        if (temp < 1)
            this.Value = "";
        else
        {
            xml_index = xml_index - 1;//回退一个字符

            String s = xml_text.substring(xml_index, temp);

            s.replace("&gt;", ">");
            s.replace("&lt;", "<");
            s.replace("&amp;", "&");

            //this.Value = xml_text.Substring(xml_index, temp - xml_index);
            this.Value = s;
            xml_index  = temp;
        }
    }
    //------------------------
    public boolean Read()
    {
        Token = XmlToken.None;
        Value = null;
        int buffer_char = 0;
        int buffer_peek = 0;

        while (true)
        {
            buffer_char = text_read();

            if (buffer_char < 0)
            {
                Token = XmlToken.End;
                return true;
            }

            if (buffer_char == '<')
            {
                buffer_peek = text_peek();

                if (buffer_peek == '/')
                {
                    Token = XmlToken.TargetEnd;
                    ReadName();
                    return true;
                }
                else if (buffer_peek == '!')
                {
                    text_read();
                    buffer_peek = text_peek();
                    if (buffer_peek == '-')//<!-- 注释
                        text_jump('>');
                    else //<![CDATA[
                    {
                        text_jump(6);
                        Token = XmlToken.CDATA;
                        ReadCDATA();
                        return true;
                    }
                }
                else if (buffer_peek == '?')
                    text_jump('>');
                else
                {
                    Token = XmlToken.TargetStart;
                    ReadName();
                    return true;
                }
            }
            else
            {
                if (Token == XmlToken.None && buffer_char > 32)
                {
                    Token = XmlToken.Value;
                    ReadValue();
                    return true;
                }
            }
        }
    }
}
