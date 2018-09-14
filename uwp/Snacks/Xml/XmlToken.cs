using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Noear.Snacks.Xml
{
    public enum XmlToken
    {
        None,
        End,

        TargetStart,
        TargetEnd,
        Value,
        CDATA,
    }
}
