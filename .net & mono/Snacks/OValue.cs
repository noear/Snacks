using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Noear.Snacks
{
    internal class OValue
    {
        protected int _int;
        protected long _long;
        protected double _double;
        protected String _string;
        protected bool _bool;
        protected DateTime _date;

        public OValueType type;

        public void set(int value)
        {
            _int = value;
            type = OValueType.Int;
        }

        public void set(long value)
        {
            _long = value;
            type = OValueType.Long;
        }

        public void set(double value)
        {
            _double = value;
            type = OValueType.Double;
        }

        public void set(String value)
        {
            _string = value;
            type = OValueType.String;
        }

        public void set(bool value)
        {
            _bool = value;
            type = OValueType.Boolean;
        }

        public void set(DateTime value)
        {
            _date = value;
        }
        //==================

        public int getInt()
        {
            switch (type)
            {
                case OValueType.Int: return _int;
                case OValueType.Long: return (int)_long;
                case OValueType.Double: return (int)_double;
                case OValueType.String:
                    {
                        if (_string == null || _string.Length == 0)
                            return 0;
                        else
                            return int.Parse(_string);
                    }
                case OValueType.Boolean: return _bool ? 1 : 0;
                case OValueType.DateTime: return 0;
                default: return 0;
            }
        }

        public long getLong()
        {
            switch (type)
            {
                case OValueType.Int: return _int;
                case OValueType.Long: return _long;
                case OValueType.Double: return (long)_double;
                case OValueType.String:
                    {
                        if (_string == null || _string.Length == 0)
                            return 0;
                        else
                            return long.Parse(_string);
                    }
                case OValueType.Boolean: return _bool ? 1 : 0;
                case OValueType.DateTime: return _date.Ticks;
                default: return 0;
            }
        }

        public double getDouble()
        {
            switch (type)
            {
                case OValueType.Int: return _int;
                case OValueType.Long: return _long;
                case OValueType.Double: return _double;
                case OValueType.String:
                    {
                        if (_string == null || _string.Length == 0)
                            return 0;
                        else
                            return double.Parse(_string);
                    }
                case OValueType.Boolean: return _bool ? 1 : 0;
                case OValueType.DateTime: return _date.Ticks;
                default: return 0;
            }
        }

        public String getString()
        {
            switch (type)
            {
                case OValueType.Int: return _int.ToString();
                case OValueType.Long: return  _long.ToString();
                case OValueType.Double: return  _double.ToString();
                case OValueType.String: return _string;
                case OValueType.Boolean: return  _bool.ToString();
                case OValueType.DateTime: return  ONode.TIME_FORMAT_ACTION(_date);
                default: return "";
            }
        }

        public bool getBoolean()
        {
            switch (type)
            {
                case OValueType.Int: return _int > 0;
                case OValueType.Long: return _long > 0;
                case OValueType.Double: return _double > 0;
                case OValueType.String: return false;
                case OValueType.Boolean: return _bool;
                case OValueType.DateTime: return false;
                default: return false;
            }
        }

        public DateTime getDate()
        {
            switch (type)
            {
                case OValueType.String: return parseDate(_string);
                case OValueType.DateTime: return _date;
                default: return DateTime.MinValue;
            }
        }

        private static DateTime parseDate(String dateString)
        {
            return DateTime.Parse(dateString);
        }
    }
}
