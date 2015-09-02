package demo;

import noear.snacks.ONode;

import java.util.Date;
import java.util.List;

/**
 * Created by yuety on 15/9/2.
 */
public class Build_demo {
    public void build_demo1(){
        /*
        List<CarBrandModel> gcbList = BaseDb.getCarBrands();

        ONode list = data.$("list").asArray();
        for(CarBrandModel d : gcbList)
        {
            ONode n = new ONode();
            n.set("id", d.brand_id);
            n.set("name", d.brand_name);
            n.set("PY", d.py);

            if(vid<112) {
                n.set("logo", d.logo_url);
            }

            list.add(n);
        }*/
    }

    public String build_demo2(ONode p){
        ONode data = new ONode();
        /*
        if (checkParamsIsOk("brandID") == false)
            return;

        if(checkParamsIsNot0("brandID")==false)
            return;

        int brandID = p.get("brandID").getInt();

        List<CarModelModel> gcmList = BaseDb.getCarModels(brandID);

        if(gcmList.size()==0){
            data.set("code",0);
        }
        else {
            ONode list = data.get("list").asArray();
            gcmList.forEach((carModel) -> {
                ONode n = new ONode();
                n.set("id", carModel.model_id);
                n.set("name", carModel.model_name);

                list.add(n);
            });
            data.set("code",1);
        }*/

        return data.toJson(); // or: data.toXml(); //或者自己构建数据
    }
}
