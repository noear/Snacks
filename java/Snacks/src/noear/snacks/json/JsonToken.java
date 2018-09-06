package noear.snacks.json;

/**
 * Created by noear on 14-6-18.
 */
public enum JsonToken {
    None,

    End,

    ObjectStart,
    ObjectEnd,
    ArrayStart,
    ArrayEnd,
    ValueStart,
//    ValueEnd,

    Int,
    Long,
    Double,
    String,
    Boolean,
    DateTime,

    Null,
}
