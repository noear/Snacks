using Noear.Snacks.Json;
using Noear.Snacks.Xml;
using System;
using System.Text;

namespace Noear.Snacks {
    public partial class ONode {
        public static String NULL_DEFAULT = "\"\"";
        public static bool BOOL_USE01 = true;
        public static FormatHanlder TIME_FORMAT_ACTION = (DateTime e) => {
            return e.ToString("yyyy-MM-dd HH:mm:ss");
        };
        //========================

        public static ONode load(String ops) {
            if (ops == null || ops.Length < 2)
                return new ONode();

            if (ops[0] == '<')
                return readXmlValue(new XmlReader(ops));
            else
                return readJsonValue(new JsonReader(ops));
        }

        public static ONode tryLoad(String ops) {
            if (ops == null || ops.Length < 2)
                return new ONode();

            try {
                if (ops[0] == '<')
                    return readXmlValue(new XmlReader(ops));
                else
                    return readJsonValue(new JsonReader(ops));
            }
            catch {
                return new ONode();
            }
        }

        public String toJson() {
            StringBuilder sb = new StringBuilder();
            JsonWriter writer = new JsonWriter(sb);

            writeJson(this, writer);

            return sb.ToString();
        }

        public String toXml() {
            StringBuilder sb = new StringBuilder();
            XmlWriter writer = new XmlWriter(sb);

            writer.WriteNodeStart("xml");
            writeXml(this, writer);
            writer.WriteNodeEnd("xml");

            return sb.ToString();
        }
        

        //=============
        public ONode() {

        }
        public ONode(int value) {
            tryInitValue();

            _value.set(value);
        }

        public ONode(long value) {
            tryInitValue();

            _value.set(value);
        }

        public ONode(double value) {
            tryInitValue();

            _value.set(value);
        }

        public ONode(String value) {
            tryInitValue();

            _value.set(value);
        }

        public ONode(bool value) {
            tryInitValue();

            _value.set(value);
        }

        public ONode(DateTime value) {
            tryInitValue();

            _value.set(value);
        }

        public Boolean contains(String key) {
            if (_object == null || _type != ONodeType.Object)
                return false;
            else
                return _object.contains(key);
        }

        public bool remove(String key) {
            if (_object == null || _type != ONodeType.Object)
                return false;
            else
                return _object.members.Remove(key);
        }

        public int count() {
            if (isObject)
                return _object.members.Count;

            if (isArray)
                return _array.elements.Count;

            return 0;
        }


        //========================
        public double getDouble() {
            if (_value == null)
                return 0;
            else
                return _value.getDouble();
        }

        public int getInt() {
            if (_value == null)
                return 0;
            else
                return _value.getInt();
        }

        public long getLong() {
            if (_value == null)
                return 0;
            else
                return _value.getLong();
        }

        public bool getBoolean() {
            if (_value == null)
                return false;
            else
                return _value.getBoolean();
        }

        public DateTime getDate() {
            if (_value == null)
                return DateTime.MinValue;
            else
                return _value.getDate();
        }

        public String getString() {
            if (_value == null)
                return "";
            else
                return _value.getString();
        }

        //=============
        //返回目标节点
        public ONode this[string key] {
            get {
                return get(key);
            }
            set {
                set(key, value);
            }
        }
        //返回自己
        public ONode get(int index) {
            tryInitArray();

            if (_array.elements.Count > index)
                return _array.elements[index];
            else
                return null;
        }

        //返回自己
        public ONode get(String key) {
            tryInitObject();

            if (_object.contains(key))
                return _object.get(key);
            else {
                ONode temp = new ONode();
                _object.set(key, temp);
                return temp;
            }
        }
        //返回自己
        public ONode set(String key, ONode value) {
            tryInitObject();

            _object.set(key, value);
            return this;
        }

        //返回自己
        public ONode add(ONode value) {
            tryInitArray();

            _array.add(value);

            return this;
        }

        //返回新节点
        public ONode add() {
            tryInitArray();
            ONode temp = new ONode();
            _array.add(temp);

            return temp;
        }
    }
}
