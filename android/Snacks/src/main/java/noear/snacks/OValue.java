package noear.snacks;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by noear on 14-6-11.
 */
public class OValue {
    protected  int _int;
    protected  long _long;
    protected  double _double;
    protected  String _string;
    protected  boolean _bool;
    protected  Date _date;

    public  OValueType type;

    public void set(int value)
    {
        _int = value;
        type=OValueType.Int;
    }

    public void set(long value)
    {
        _long = value;
        type=OValueType.Long;
    }

    public void set(double value)
    {
        _double = value;
        type=OValueType.Double;
    }

    public void set(String value)
    {
        _string = value;
        type=OValueType.String;
    }

    public void set(boolean value)
    {
        _bool = value;
        type=OValueType.Boolean;
    }

    public void set(Date value)
    {
        _date = value;
        type=OValueType.DateTime;
    }
    //==================

    public int getInt()
    {
        switch (type)
        {
            case Int: return _int;
            case Long:return (int)_long;
            case Double:return (int)_double;
            case String: {
                if (_string == null || _string.length() == 0)
                    return 0;
                else
                    return Integer.parseInt(_string);
            }
            case Boolean:return _bool?1:0;
            case DateTime:return 0;
            default:return 0;
        }
    }

    public long getLong()
    {
        switch (type)
        {
            case Int: return _int;
            case Long:return _long;
            case Double:return (long)_double;
            case String: {
                if(_string == null ||_string.length()==0)
                    return 0;
                else
                    return Long.parseLong(_string);
            }
            case Boolean:return _bool?1:0;
            case DateTime:return _date.getTime();
            default:return 0;
        }
    }

    public double getDouble()
    {
        switch (type)
        {
            case Int: return _int;
            case Long:return _long;
            case Double:return _double;
            case String: {
                if (_string == null || _string.length() == 0)
                    return 0;
                else
                    return Double.parseDouble(_string);
            }
            case Boolean:return _bool?1:0;
            case DateTime:return _date.getTime();
            default:return 0;
        }
    }

    public String getString()
    {
        switch (type)
        {
            case Int: return String.valueOf(_int);
            case Long:return String.valueOf(_long);
            case Double:return String.valueOf(_double);
            case String:return _string;
            case Boolean:return String.valueOf(_bool);
            case DateTime:return String.valueOf(_date);
            default:return "";
        }
    }

    public boolean getBoolean()
    {
        switch (type)
        {
            case Int: return _int>0;
            case Long:return _long>0;
            case Double:return _double>0;
            case String:return false;
            case Boolean:return _bool;
            case DateTime:return false;
            default:return false;
        }
    }

    public Date getDate()
    {
        switch (type)
        {
            case String:return parseDate(_string);
            case DateTime:return _date;
            default:return null;
        }
    }

    private static Date parseDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            return sdf.parse(dateString);
        } catch (ParseException ex) {
            return null;
        }
    }
}
