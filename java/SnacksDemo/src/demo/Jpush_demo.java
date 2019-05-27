package demo;

import noear.snacks.ONode;

import java.util.Collection;

public class Jpush_demo {
    public static void xx(Collection<String> alias_ary, String text){
        ONode data = new ONode().exp((d)->{
            d.set("platform","all");

            d.get("audience").get("alias").addAll(alias_ary);

            d.get("options")
                    .set("apns_production",false);

            d.get("notification").exp(n->{
                n.get("ios")
                        .set("alert",text)
                        .set("badge",0)
                        .set("sound","happy");
            });
        });
    }
}
