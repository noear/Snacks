package demo;

import noear.snacks.ONode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuety on 15/9/2.
 */
public class Param_demo {
    //从外部传一个数据过来
    public void in_demo(ONode n) {
        long orderID = n.get("orderID").getLong();
        long userID = n.get("userID").getLong();
        double lat = n.get("lat").getDouble(6);
        double lng = n.get("lng").getDouble(6);
    }


    //发送一条定制的消息
    public void out_demo(int poolID , int orderID, long userID, String userName) throws Exception{
        String content = "车主"+userName+"取消了这次拼车，拼车费用将回到您的账户。";

        ONode pd = new ONode();

        pd.set("CMD", 1);
        pd.set("USERID",userID);

        pd.set("CONTENT",content);
        pd.set("POOLID",poolID);
        pd.set("ORDERID",orderID);
    }

    //批量推送（消息数据，上外部定制）
    public ONode out_demo2(ONode input){
        ONode users = input.get("users");
        String alert = input.get("alert").getString();
        String data = input.get("data").getString();
        List<Long> list = new ArrayList<Long>();

        for(ONode u : users) //users为数据
        {
            list.add(u.getLong());
        }

        boolean isOk = false;


        ONode output = new ONode();
        if (isOk == false) {
            output.set("code",0);
            output.set("msg","失败了");
        }else{
            output.set("code",1);
        }

        return output;
    }
}
