using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Noear.Snacks.Json
{
    internal enum ValueType
    {
        None,

        /// <summary>
        /// 单引号字串
        /// </summary>
        String1,//
        /// <summary>
        /// 双引号字符串
        /// </summary>
        String2,//
        String3,

        //    True,  //
        //    False, //

        DateTime, // /或n开头的值
        //    Null,

        Number,
        Number_Double,
    }
}
