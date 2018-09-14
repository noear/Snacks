package noear.snacks;

import noear.snacks.ONode;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by noear on 14-6-11.
 */
public class OObject {

    public Map<String, ONode> members;

    public OObject(){
        members = new LinkedHashMap<String, ONode>();
    }

    public void set(String key, ONode value)
    {
        members.put(key,value);
    }

    public ONode get(String key)
    {
        return members.get(key);
    }

    public boolean contains(String key)
    {
        return members.containsKey(key);
    }

    public int count() {
        return members.size();
    }

    public void clear(){}
}


