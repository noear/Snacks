package noear.snacks;

import noear.snacks.ONode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by noear on 14-6-11.
 */
public class OArray {
    public List<ONode> elements;

    public  OArray(){
         elements = new ArrayList<ONode>();
    }

    public void add(ONode value)
    {
        elements.add(value);
    }

    public void add(int index, ONode value)
    {
        elements.add(index,value);
    }

    public ONode get(int index)
    {
        return elements.get(index);
    }

    public int count(){
        return elements.size();
    }

    public void clear(){
        elements.clear();
    }
}
