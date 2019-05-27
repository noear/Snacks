package org.noear.snacks;

import org.noear.snacks.json.JsonReader;
import org.noear.snacks.json.JsonToken;
import org.noear.snacks.json.JsonWriter;
import org.noear.snacks.xml.XmlReader;
import org.noear.snacks.xml.XmlToken;
import org.noear.snacks.xml.XmlWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by noear on 14-6-11.
 */
public class ONodeBase implements Iterable<ONode>{

    //========================
    protected OArray  _array  = null;
    protected OObject _object = null;
    protected OValue  _value  = null;

    protected ONodeType _type = ONodeType.Null;

    //========================

    public static ONode tryLoad(String ops){
        try{
            ops = ops.trim();

            return load(ops);
        }catch (Exception ex){
            return new ONode();
        }
    }

    public static ONode tryLoadXml(String ops){
        if (ops == null || ops.length() < 2)
            return new ONode();

        try{
            ops = ops.trim();

            if (ops.charAt(0) == '<')
                return readXmlValue(new XmlReader(ops));
            else
                return new ONode();
        }catch (Exception ex){
            return new ONode();
        }
    }

    public static ONode tryLoadJson(String ops){
        if (ops == null || ops.length() < 2)
            return new ONode();

        try{
            ops = ops.trim();

            if (ops.charAt(0) == '{' || ops.charAt(0) == '[')
                return readJsonValue(new JsonReader(ops));
            else
                return new ONode();
        }catch (Exception ex){
            return new ONode();
        }
    }

    public static ONode load(String ops) throws Exception {
        if (ops == null || ops.length() < 2)
            return new ONode();

        if (ops.charAt(0) == '<')
            return readXmlValue(new XmlReader(ops));
        else
            return readJsonValue(new JsonReader(ops));

    }

    public String toJson(){
        return toJson(null);
    }

    public String toJson(FormatHanlder dateFormat) {
        StringBuilder sb = new StringBuilder();
        JsonWriter writer = new JsonWriter(sb,dateFormat);

        writeJson(this, writer);

        return sb.toString();
    }

    public String toXml() {
        StringBuilder sb = new StringBuilder();
        XmlWriter writer = new XmlWriter(sb);

        writer.WriteNodeStart("xml");
        writeXml(this, writer);
        writer.WriteNodeEnd("xml");

        return sb.toString();
    }

    public OValue internalValue(){
        return _value;
    }

    public OObject internalObject(){
        return _object;
    }

    public OArray internalArray(){
        return _array;
    }

    //==============

    protected  void tryInitValue() {
        if (_value == null)
            _value = new OValue();

        if (_type != ONodeType.Value)
            _type = ONodeType.Value;
    }

    protected  void tryInitObject() {
        if (_object == null)
            _object = new OObject();

        if (_type != ONodeType.Object)
            _type = ONodeType.Object;
    }

    protected  void tryInitArray() {
        if (_array == null)
            _array = new OArray();

        if (_type != ONodeType.Array)
            _type = ONodeType.Array;
    }

    protected void shiftToArray(){
        tryInitArray();

        if(_object!=null) {
            for (ONode n1 : _object.members.values()) {
                _array.add(n1);
            }

            _object.clear();
            _object = null;
        }
    }

    //================

    private static ONode readXmlValue(XmlReader reader) throws IOException,SnacksException {
        reader.Read();

        if (reader.Token == XmlToken.End)
            return null;

        ONode instance = new ONode();

        if (reader.Token == XmlToken.TargetEnd) {
            instance.setString(null);
            return instance;
        }

        if (reader.Token != XmlToken.TargetStart) {
            instance.setString(reader.Value);

//            if (reader.Token == XmlToken.CDATA)
//                instance.isCDATA = true;
        } else {
            instance.tryInitObject();

            while (true) {
                if (reader.Token != XmlToken.TargetStart)
                    break;

                String popName = reader.Value;

                if (popName == null) //如果取不到值，则停止解析
                    throw new SnacksException("XML格式有问题");

                if (instance.contains(popName)) //尝试将obj转换为ary
                    instance.shiftToArray();

                ONode node = readXmlValue(reader);

                if (instance.isArray())
                    instance.add(node);
                else
                    instance.set(popName, node);

                if (reader.Token == XmlToken.Value || reader.Token == XmlToken.CDATA)
                    reader.Read();

                reader.Read();
            }
        }

        return instance;
    }

    private static ONode readJsonValue(JsonReader reader) throws IOException,SnacksException
    {
        reader.read();

        if (reader.Token == JsonToken.ArrayEnd)
            return null;

        if (reader.Token == JsonToken.ObjectEnd)
            return null;

        ONode instance = new ONode();

        if (reader.Token == JsonToken.Null)
        {
            instance._type = ONodeType.Null;
            return instance;
        }

        if (reader.Token == JsonToken.String)
        {
            instance.setString(tryUnescape((String) reader.Value));
            return instance;
        }

        if (reader.Token == JsonToken.Double)
        {
            instance.setDouble((Double) reader.Value);
            return instance;
        }

        if (reader.Token == JsonToken.Int)
        {
            instance.setInt((Integer) reader.Value);
            return instance;
        }

        if (reader.Token == JsonToken.Long)
        {
            instance.setLong((Long) reader.Value);
            return instance;
        }

        if (reader.Token == JsonToken.Boolean)
        {
            instance.setBoolean((Boolean) reader.Value);
            return instance;
        }

        if (reader.Token == JsonToken.DateTime)
        {
            instance.setDateTime((Date) reader.Value);
            return instance;
        }

        if (reader.Token == JsonToken.ArrayStart)
        {
            instance.tryInitArray();

            while (true)
            {
                ONode item = readJsonValue(reader);

                if (reader.Token == JsonToken.ArrayEnd)
                {
                    //解决数组套数组的问题
                    if (!(item != null && item.isArray() == true))
                        break;
                }

                if (item == null) //如果取不到值，则停止解析
                    throw new SnacksException("JSON格式有问题");

                instance.add(item);

            }
        }
        else if (reader.Token == JsonToken.ObjectStart)
        {
            instance.tryInitObject();

            while (true)
            {
                reader.ReadName();

                if (reader.Token == JsonToken.ObjectEnd)
                    break;

                String property = (String)reader.Value;

                if (property == null)//如果取不到值，则停止解析
                    throw new SnacksException("JSON格式有问题");

                ONode val = readJsonValue(reader);

                if (val == null) //如果取不到值，则停止解析
                    throw new SnacksException();

                instance.set(property, val);
            }
        }

        return instance;
    }

    //=================
    protected static void writeJson(ONodeBase node, JsonWriter writer)
    {
        if(node._type == ONodeType.Null) {
            writer.WriteValue(node._value);
            return;
        }

        if(node._type == ONodeType.Value)
        {
            writer.WriteValue(node._value);
            return;
        }

        if(node._type == ONodeType.Object)
        {
            writer.WriteObjectStart();
            for(Map.Entry<String, ONode> kv:node._object.members.entrySet())
            {
                writer.WritePropertyName(kv.getKey());
                writeJson(kv.getValue(), writer);
            }
            writer.WriteObjectEnd();
            return;
        }

        if(node._type == ONodeType.Array)
        {
            writer.WriteArrayStart();
            for(ONode v:node._array.elements)
                writeJson(v,writer);
            writer.WriteArrayEnd();
            return;
        }
    }

    protected static void writeXml(ONodeBase node, XmlWriter writer)
    {
        if(node._type == ONodeType.Null)
            return;

        if(node._type == ONodeType.Value)
        {
            writer.WriteValue(node._value);
            return;
        }

        if(node._type == ONodeType.Object)
        {
            for(Map.Entry<String, ONode> kv:node._object.members.entrySet())
            {
                writer.WriteNodeStart(kv.getKey());
                writeXml(kv.getValue(),writer);
                writer.WriteNodeEnd(kv.getKey());
            }
            return;
        }

        if(node._type == ONodeType.Array)
        {
            for(ONode v:node._array.elements) {
                writer.WriteNodeStart("item");
                writeXml(v, writer);
                writer.WriteNodeEnd("item");
            }
            return;
        }
    }

    public boolean isObject()
    {
        return _type == ONodeType.Object;
    }

    public boolean isArray()
    {
        return _type == ONodeType.Array;
    }

    public boolean isValue()
    {
        return _type == ONodeType.Value;
    }

    public ONode asObject()
    {
        tryInitObject();
        return (ONode)this;
    }

    public ONode asArray()
    {
        tryInitArray();
        return (ONode)this;
    }

    public ONode asNull() {
        tryInitValue();

        _value.type = OValueType.Null;
        return (ONode) this;
    }

    protected void setInt(int value){
        tryInitValue();

        _value.set(value);
    }

    protected void setLong(long value){
        tryInitValue();

        _value.set(value);
    }

    protected void setDouble(double value){
        tryInitValue();

        _value.set(value);
    }

    protected void setString(String value){
        tryInitValue();

        _value.set(value);
    }

    protected void setBoolean(boolean value){
        tryInitValue();

        _value.set(value);
    }

    protected void setDateTime(Date value){
        tryInitValue();

        _value.set(value);
    }

    @Override
    public Iterator<ONode> iterator() {
        if(isArray())
            return _array.elements.iterator();
        else
            return null;
    }

    @Override
    public void forEach(Consumer<? super ONode> action) {
        if(isArray())
            _array.elements.forEach(action);
    }

    @Override
    public Spliterator<ONode> spliterator() {
        if(isArray())
            return _array.elements.spliterator();
        else
            return null;
    }

    public Collection<String> allKeys(){
        if(isObject()){
            return _object.members.keySet();
        }else{
            return null;
        }
    }

    //=======================
    //
    private static String tryUnescape(String str){
        if(str == null){
            return null;
        }

        if(str.indexOf("\\u")<0){
            return str;
        }

        try {

            StringWriter writer = new StringWriter(str.length());
            unescapeUnicode(writer, str);

            return writer.toString();
        }catch (Exception ex){
            return str;
        }
    }

    private static void unescapeUnicode(Writer out, String str) throws IOException {
        if(out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        } else if(str != null) {
            int sz = str.length();
            StringBuilder unicode = new StringBuilder(4);
            boolean hadSlash = false;
            boolean inUnicode = false;

            for(int i = 0; i < sz; ++i) {
                char ch = str.charAt(i);
                if(inUnicode) {
                    unicode.append(ch);
                    if(unicode.length() == 4) {
                        try {
                            int value = Integer.parseInt(unicode.toString(), 16);
                            out.write((char)value);
                            unicode.setLength(0);
                            inUnicode = false;
                            hadSlash = false;
                        } catch (NumberFormatException var9) {
                            throw new IOException("Unable to parse unicode value: " + unicode, var9);
                        }
                    }
                } else if(hadSlash) {
                    hadSlash = false;
                    switch(ch) {
                        case '"':
                            out.write(34);
                            break;
                        case '\'':
                            out.write(39);
                            break;
                        case '\\':
                            out.write(92);
                            break;
                        case 'b':
                            out.write(8);
                            break;
                        case 'f':
                            out.write(12);
                            break;
                        case 'n':
                            out.write(10);
                            break;
                        case 'r':
                            out.write(13);
                            break;
                        case 't':
                            out.write(9);
                            break;
                        case 'u':
                            inUnicode = true;
                            break;
                        default:
                            out.write(ch);
                    }
                } else if(ch == 92) {
                    hadSlash = true;
                } else {
                    out.write(ch);
                }
            }

            if(hadSlash) {
                out.write(92);
            }

        }
    }
}
