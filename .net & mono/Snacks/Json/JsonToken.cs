using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Noear.Snacks.Json
{
    public enum JsonToken
    {
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
}
