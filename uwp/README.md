
DEMO for UWP::
```java
public static void GetShareFavs(ListViewModel<BookShareModel> viewModel, Action<int> callback) {

    ONode args = new ONode();

    args.set("userID", Session.userID);
    args.set("t", CMDs.Ticks());
    args.set("min", viewModel.minID);
    args.set("beta", 1);
    args.set("p", 3);

    CMDs.argsAddLatlng(args);

    CmdUtil.call("S.1.1", args, (code, data) => {

        if (code == 1) {
            viewModel.minID = data.get("min").getLong();

            ONode list = data.get("list");
            foreach (ONode d1 in list) {
                BookShareModel u1 = new BookShareModel();

                u1.url = d1.get("url").getString();
                u1.name = d1.get("name").getString();
                u1.author = d1.get("a").getString();
                u1.logo = d1.get("l").getString();
                u1.source = d1.get("s").getString();
                u1.updateTime = d1.get("lt").getString();//t
                u1.newSection = d1.get("n").getString();
                u1.status = d1.get("ct").getString();
                u1.type = d1.get("p").getInt();
                u1.comment = d1.get("c").getString();
                u1.isWeb = (d1.get("w").getInt() == 1);

                viewModel.list.Add(u1);
            }
        }

        callback(code);
    });
}
```
