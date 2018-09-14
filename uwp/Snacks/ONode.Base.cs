using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Noear.Snacks.Json;
using Noear.Snacks.Xml;

namespace Noear.Snacks
{
    public partial class ONode : IEnumerable<ONode>
    {
        //========================
        internal OArray _array = null;
        internal OObject _object = null;
        internal OValue _value = null;

        internal ONodeType _type = ONodeType.Null;

        //==============

        protected void tryInitValue()
        {
            if (_value == null)
                _value = new OValue();

            if (_type != ONodeType.Value)
                _type = ONodeType.Value;
        }

        protected void tryInitObject()
        {
            if (_object == null)
                _object = new OObject();

            if (_type != ONodeType.Object)
                _type = ONodeType.Object;
        }

        protected void tryInitArray()
        {
            if (_array == null)
                _array = new OArray();

            if (_type != ONodeType.Array)
                _type = ONodeType.Array;
        }

        protected void shiftToArray()
        {
            tryInitArray();

            if (_object != null)
            {
                foreach (ONode n1 in _object.members.Values)
                {
                    _array.add(n1);
                }

                _object.clear();
                _object = null;
            }
        }

        //================

        private static ONode readXmlValue(XmlReader reader)
        {
            reader.Read();

            if (reader.Token == XmlToken.End)
                return null;

            ONode instance = new ONode();

            if (reader.Token == XmlToken.TargetEnd)
            {
                instance.setString(null);
                return instance;
            }

            if (reader.Token != XmlToken.TargetStart)
            {
                instance.setString(reader.Value);

                //            if (reader.Token == XmlToken.CDATA)
                //                instance.isCDATA = true;
            }
            else
            {
                instance.tryInitObject();

                while (true)
                {
                    if (reader.Token != XmlToken.TargetStart)
                        break;

                    String popName = reader.Value;

                    if (popName == null) //如果取不到值，则停止解析
                        throw new SnacksException("XML格式有问题");

                    if (instance.contains(popName)) //尝试将obj转换为ary
                        instance.shiftToArray();

                    ONode node = readXmlValue(reader);

                    if (instance.isArray)
                        instance.add(node);
                    else
                        instance.set(popName,node);

                    if (reader.Token == XmlToken.Value || reader.Token == XmlToken.CDATA)
                        reader.Read();

                    reader.Read();
                }
            }

            return instance;
        }

        private static ONode readJsonValue(JsonReader reader)
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
                instance.setString((String)reader.Value);
                return instance;
            }

            if (reader.Token == JsonToken.Double)
            {
                instance.setDouble((Double)reader.Value);
                return instance;
            }

            if (reader.Token == JsonToken.Int)
            {
                instance.setInt((int)reader.Value);
                return instance;
            }

            if (reader.Token == JsonToken.Long)
            {
                instance.setLong((long)reader.Value);
                return instance;
            }

            if (reader.Token == JsonToken.Boolean)
            {
                instance.setBoolean((bool)reader.Value);
                return instance;
            }

            if (reader.Token == JsonToken.DateTime)
            {
                instance.setDateTime((DateTime)reader.Value);
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
                        if (!(item != null && item.isArray == true))
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
        internal static void writeJson(ONode node, JsonWriter writer)
        {
            if (node._type == ONodeType.Null)
                return;

            if (node._type == ONodeType.Value)
            {
                writer.WriteValue(node._value);
                return;
            }

            if (node._type == ONodeType.Object)
            {
                writer.WriteObjectStart();
                foreach (KeyValuePair<String, ONode> kv in node._object.members)
                {
                    writer.WritePropertyName(kv.Key);
                    writeJson(kv.Value, writer);
                }
                writer.WriteObjectEnd();
                return;
            }

            if (node._type == ONodeType.Array)
            {
                writer.WriteArrayStart();
                foreach (ONode v in node._array.elements)
                    writeJson(v, writer);
                writer.WriteArrayEnd();
                return;
            }
        }

        internal static void writeXml(ONode node, XmlWriter writer)
        {
            if (node._type == ONodeType.Null)
                return;

            if (node._type == ONodeType.Value)
            {
                writer.WriteValue(node._value);
                return;
            }

            if (node._type == ONodeType.Object)
            {
                foreach (KeyValuePair<String, ONode> kv in node._object.members)
                {
                    writer.WriteNodeStart(kv.Key);
                    writeXml(kv.Value, writer);
                    writer.WriteNodeEnd(kv.Key);
                }
                return;
            }

            if (node._type == ONodeType.Array)
            {
                foreach (ONode v in node._array.elements)
                {
                    writer.WriteNodeStart("item");
                    writeXml(v, writer);
                    writer.WriteNodeStart("item");
                }
                return;
            }
        }

        public bool isObject
        {
            get
            {
                return _type == ONodeType.Object;
            }
        }

        public bool isArray
        {
            get
            {
                return _type == ONodeType.Array;
            }
        }

        public bool isValue
        {
            get
            {
                return _type == ONodeType.Value;
            }
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

        public void setInt(int value)
        {
            tryInitValue();

            _value.set(value);
        }

        public void setLong(long value)
        {
            tryInitValue();

            _value.set(value);
        }

        public void setDouble(double value)
        {
            tryInitValue();

            _value.set(value);
        }

        public void setString(String value)
        {
            tryInitValue();

            _value.set(value);
        }

        public void setBoolean(bool value)
        {
            tryInitValue();

            _value.set(value);
        }

        public void setDateTime(DateTime value)
        {
            tryInitValue();

            _value.set(value);
        }

        public IEnumerator<ONode> GetEnumerator()
        {
            if (isArray)
                return _array.elements.GetEnumerator();
            else
                return null;
        }

        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            if (isArray)
                return _array.elements.GetEnumerator();
            else
                return null;
        }
    }
}
