# Snacks for .net/mono/java/android(java)/ios(swift 3.0)/uwp(c#)
轻量级数据DOM，可与XML/JSON相互转换

```java
//反向:::::::::::::::::
//object
ONode n = ONode.tryLoad("{name:'noear',sex:1}");
int sex = n.get("sex").getInt();
String name = n.get("name").getString();

//array
ONode n = ONode.tryLoad("[{id:1,url:'http://xxx.xxx.xx'},{id:2,url:'http://ccc.ccc.ccc'}]");
foreach(ONode n1 in n){
   var id = n1.get("id").getLong();
   String url = n1.get("url").getString();
}

//反向:::::::::::::::::（如果有unicode需要转码）
//object
ONode n = ONode.tryLoad("{name:'noear',sex:1}").unescape(true);




//正向:::::::::::::::::
//object
ONode n = new ONode();
n.set("name","noear").set("sex",1);
String json = n.toJson(); //or n.toXml(); //或者根据自己情况，增加新的转换

//array
ONode n = new ONode().asArray();
for(int i=0;i<2; i++){
   ONode n1 = n.add();
   n1.set("id",i)
     .set("url","http://xxx.xx.xx/p/"+i);
}
String json = n.toJson();//or n.toXml();

```
一极光推送的调用示例：
```java
ONode data = new ONode().exp((d)->{
    d.get("platform").val("all");

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



String message = data.toJson();
String author = Base64Util.encode(appKey+":"+masterSecret);

Map<String,String> headers = new HashMap<>();
headers.put("Content-Type","application/json");
headers.put("Authorization","Basic "+author);

HttpUtil.postString(apiUrl, message, headers);
```
