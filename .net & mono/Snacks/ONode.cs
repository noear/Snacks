using Noear.Snacks.Json;
using Noear.Snacks.Xml;
using System;
using System.Globalization;
using System.IO;
using System.Text;

namespace Noear.Snacks {
    public partial class ONode {
        public static String NULL_DEFAULT = "\"\"";
        public static bool BOOL_USE01 = false;
        public static FormatHanlder TIME_FORMAT_ACTION = (DateTime e) => {
            if (e == null)
                return "";
            else
                return e.ToString("yyyy-MM-dd HH:mm:ss");
        };

        //private bool _unescape = false;

        //public ONode unescape(bool isUnescape) {
        //    _unescape = isUnescape;
        //    return this;
        //}
        //========================

        

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
            if (_value == null) {
                return "";
            } else {
                return _value.getString();
            }
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

        private static void unescapeUnicode(StringWriter out1, String str) {
            if (out1 == null) {
                throw new ArgumentException("The Writer must not be null");
            } else if (str != null) {
                int sz = str.Length;
                StringBuilder unicode = new StringBuilder(4);
                bool hadSlash = false;
                bool inUnicode = false;

                for (int i = 0; i < sz; ++i) {
                    char ch = str[i];
                    if (inUnicode) {
                        unicode.Append(ch);
                        if (unicode.Length == 4) {
                            try {
                                int value = int.Parse(unicode.ToString(), NumberStyles.HexNumber);
                                out1.Write((char)value);
                                unicode.Clear();
                                inUnicode = false;
                                hadSlash = false;
                            } catch (FormatException var9) {
                                throw new FormatException("Unable to parse unicode value: " + unicode, var9);
                            }
                        }
                    } else if (hadSlash) {
                        hadSlash = false;
                        switch (ch) {
                            case '"':
                                out1.Write(34);
                                break;
                            case '\'':
                                out1.Write(39);
                                break;
                            case '\\':
                                out1.Write(92);
                                break;
                            case 'b':
                                out1.Write(8);
                                break;
                            case 'f':
                                out1.Write(12);
                                break;
                            case 'n':
                                out1.Write(10);
                                break;
                            case 'r':
                                out1.Write(13);
                                break;
                            case 't':
                                out1.Write(9);
                                break;
                            case 'u':
                                inUnicode = true;
                                break;
                            default:
                                out1.Write(ch);
                                break;
                        }
                    } else if (ch == 92) {
                        hadSlash = true;
                    } else {
                        out1.Write(ch);
                    }
                }

                if (hadSlash) {
                    out1.Write(92);
                }

            }
        }
    }
}
