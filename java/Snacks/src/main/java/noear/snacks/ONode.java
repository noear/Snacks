package noear.snacks;


import noear.snacks.exts.Act1;
import noear.snacks.exts.Act2;
import noear.snacks.exts.Act3;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Created by noear on 14-6-11.
 */
public class ONode extends ONodeBase {

    public static String  NULL_DEFAULT="null";
    public static boolean BOOL_USE01=false;
    public static FormatHanlder TIME_FORMAT_ACTION=(date)->{
        if (date == null)
            return "null";
        else
            return "\"" + date.toString() + "\"";
    };

    public static final ONode NULL =  new ONode().asNull();

    //=============
    public ONode(){

    }

    /**输出自己，以供消费*/
    public ONode exp(Act1<ONode> expr){
        expr.run(this);
        return this;
    }

    public <T> T map(T t, Act3<T,String,ONode> hander){
        if(isObject()){
            _object.members.forEach((k,v)->{
                hander.run(t,k,v);
            });
        }

        if(isArray()){
            _array.elements.forEach(v->{
                hander.run(t,null,v);
            });
        }

        return t;
    }

    public <T> void val(T val){
        valSet(val);
    }
    //返回自己（重新设置基础类型的值）//不适合做为公有的
    protected   <T> ONode valSet(T val){
        tryInitValue();
        _value.set(val);
        return this;
    }

    public ONode(int value){
        valSet(value);
    }

    public ONode(long value){
        valSet(value);
    }

    public ONode(double value){
        valSet(value);
    }

    public ONode(String value){
        valSet(value);
    }

    public ONode(boolean value){
        valSet(value);
    }

    public ONode(Date value){
        valSet(value);
    }

    public boolean contains(String key) {
        if (_object == null || _type != ONodeType.Object)
            return false;
        else
            return _object.contains(key);
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

    public boolean getBoolean() {
        if (_value == null)
            return false;
        else
            return _value.getBoolean();
    }

    public Date getDate() {
        if (_value == null)
            return null;
        else
            return _value.getDate();
    }

    public long getLong() {
        if (_value == null)
            return 0;
        else
            return _value.getLong();
    }

    public String getString() {
        if (_value == null) {
            if(isObject()){
                return toJson();
            }

            if(isArray()){
                return toJson();
            }

            return "";
        }
        else {
            return _value.getString();
        }
    }

    public <T> T getModel(Class<T> cls){
        return (T)OMapper.map(this,cls);
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
    //返回结果节点，如果不存在则自动创建
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

    //返回自己
    public ONode addAll(ONode ary) {
        tryInitArray();

        if (ary != null && ary.isArray()) {
            _array.elements.addAll(ary._array.elements);
        }

        return this;
    }

    //返回自己
    public <T> ONode addAll(Iterable<T> ary){
        if(ary!=null) {
            ary.forEach(m -> add(m));
        }
        return this;
    }

    //返回自己
    public <T> ONode addAll(Iterable<T> ary, Act2<ONode,T> handler) {
        if(ary!=null) {
            ary.forEach(m -> handler.run(this.add(), m));
        }
        return this;
    }

    public ONode add(Object value){
        tryInitArray();

        if(value instanceof ONode){
            add((ONode)value);
        }else{
            add(new ONode().valSet(value));
        }

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

    public ONode rename(String key, String newKey){

        if(isObject()) {
            _object.rename(key, newKey);
        }

        if(isArray()){
            for(ONode n : _array.elements) {
                n.rename(key, newKey);
            }
        }

        return this;
    }

    public boolean remove(String key){
        if(isObject()) {
            _object.remove(key);
            return true;
        }

        if(isArray()){
            for(ONode n : _array.elements) {
                if(n.isObject()) {
                    n.remove(key);
                }
            }

            return true;
        }

        return false;
    }

    //返回自己
    public ONode setAll(ONode obj) {
        tryInitObject();

        if (obj != null && obj.isObject()) {
            _object.members.putAll(obj._object.members);
        }

        return this;
    }

    //返回自己
    public <T> ONode setAll(Map<String,T> map) {
        tryInitObject();

        if (map != null) {
            map.forEach((k, v) -> {
                set(k, v);
            });
        }
        return this;
    }

    public <T> ONode setAll(Map<String,T> map, Act2<ONode,T> handler) {
        tryInitObject();

        if (map != null) {
            map.forEach((k, v) -> {
                handler.run(this.get(k), v);
            });
        }
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

    //返回自己
    public ONode set(String key,ONode value) {
        tryInitObject();
        _object.set(key, value);

        return this;
    }

    //返回自己
    public ONode set(String key, Object value){
        tryInitObject();
        if(value instanceof ONode){
            _object.set(key,(ONode) value);
        }else{
            _object.set(key, new ONode().valSet(value));
        }
        return this;
    }
}
