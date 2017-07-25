package noear.snacks;


import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by noear on 14-6-11.
 */
public class ONode extends ONodeBase {

    public static String  NULL_DEFAULT="null";
    public static boolean BOOL_USE01=false;
    public static FormatHanlder TIME_FORMAT_ACTION=new FormatHanlder(){
        @Override
        public String run(Date e)
        {
            if(e == null)
                return "null";
            else
                return e.toString();
        }
    };

    //=============
    public ONode(){

    }
    public ONode(int value){
        tryInitValue();

        _value.set(value);
    }

    public ONode(long value){
        tryInitValue();

        _value.set(value);
    }

    public ONode(double value){
        tryInitValue();

        _value.set(value);
    }

    public ONode(String value){
        tryInitValue();

        _value.set(value);
    }

    public ONode(boolean value){
        tryInitValue();

        _value.set(value);
    }

    public ONode(Date value){
        tryInitValue();

        _value.set(value);
    }

    public boolean contains(String key) {
        if (_object == null || _type != ONodeType.Object)
            return false;
        else
            return _object.contains(key);
    }

    public boolean remove(String key) {
        if (_object == null || _type != ONodeType.Object)
            return false;
        else
            return _object.members.remove(key) != null;
    }

    public int count()
    {
        if(isObject())
            return _object.count();

        if(isArray())
            return _array.count();

        return 0;
    }


    //========================
    public double getDouble() {
        if (_value == null)
            return 0;
        else
            return _value.getDouble();
    }

    public double getDouble(int scale)
    {
        double temp = getDouble();

        if(temp==0)
            return 0;
        else
            return new BigDecimal(temp)
                    .setScale(scale,BigDecimal.ROUND_HALF_UP)
                    .doubleValue();
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

    public String getString() {
        if (_value == null)
            return "";
        else
            return _value.getString();
    }

    //=============
    //返回结果节点
    public ONode get(int index) {
        tryInitArray();

        if (_array.elements.size() > index)
            return _array.elements.get(index);
        else
            return null;
    }
    //返回结果节点
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
    public ONode add(ONode value) {
        tryInitArray();

        _array.add(value);

        return this;
    }

    public ONode add(String value) {
        return add(new ONode(value));
    }

    public ONode add(String value, boolean isOps) {
        if (isOps) {
            return add(ONode.tryLoad(value));
        } else {
            return add(new ONode(value));
        }
    }

    public ONode add(int value) {
        return add(new ONode(value));
    }

    public ONode add(long value) {
        return add(new ONode(value));
    }

    public ONode add(double value) {
        return add(new ONode(value));
    }

    public ONode add(boolean value) {
        return add(new ONode(value));
    }

    public ONode add(Date value) {
        return add(new ONode(value));
    }


    //返回新节点
    public ONode add(){
        ONode n = new ONode();

        add(n);

        return n;
    }

    //返回自己
    public ONode set(String key,ONode value) {
        tryInitObject();
        _object.set(key, value);

        return this;
    }

    //返回自己
    public  ONode set(String key,String value) {
        return set(key, new ONode(value));
    }
    public  ONode set(String key,String value, boolean isOps) {
        if (isOps) {
            return set(key, ONode.tryLoad(value));
        } else {
            return set(key, new ONode(value));
        }
    }

    //返回自己
    public  ONode set(String key,int value) {
        return set(key, new ONode(value));
    }

    //返回自己
    public  ONode set(String key,long value) {
        return set(key, new ONode(value));
    }

    //返回自己
    public  ONode set(String key,double value) {
        return set(key, new ONode(value));
    }

    //返回自己
    public  ONode set(String key,boolean value) {
        return set(key, new ONode(value));
    }

    //返回自己
    public  ONode set(String key,Date value) {
        return set(key, new ONode(value));
    }
}
