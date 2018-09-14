using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Noear.Snacks
{
    public partial class ONode
    {
        #region Implicit Conversions(稳式转入)
        /// <summary>
        /// 将Boolean值转换为一个ONode实例
        /// </summary>
        /// <param name="data">值</param>
        /// <returns>ONode实例</returns>
        public static implicit operator ONode(Boolean data)
        {
            return new ONode(data);
        }

        /// <summary>
        /// 将Double值转换为一个ONode实例
        /// </summary>
        /// <param name="data">值</param>
        /// <returns>ONode实例</returns>
        public static implicit operator ONode(Double data)
        {
            return new ONode(data);
        }

        /// <summary>
        /// 将Int32值转换为一个ONode实例
        /// </summary>
        /// <param name="data">值</param>
        /// <returns>ONode实例</returns>
        public static implicit operator ONode(Int32 data)
        {
            return new ONode(data);
        }

        /// <summary>
        /// 将Int64值转换为一个ONode实例
        /// </summary>
        /// <param name="data">值</param>
        /// <returns>ONode实例</returns>
        public static implicit operator ONode(Int64 data)
        {
            return new ONode(data);
        }

        /// <summary>
        /// 将String值转换为一个ONode实例
        /// </summary>
        /// <param name="data">值</param>
        /// <returns>ONode实例</returns>
        public static implicit operator ONode(String data)
        {
            return new ONode(data);
        }

        /// <summary>
        /// 将DateTime值转换为一个ONode实例
        /// </summary>
        /// <param name="data">值</param>
        /// <returns>ONode实例</returns>
        public static implicit operator ONode(DateTime data)
        {
            return new ONode(data);
        }

        #endregion

        #region Implicit Conversions(稳式换出)
        /// <summary>
        /// 将ONode转换为Boolean值
        /// </summary>
        /// <param name="data">值</param>
        /// <returns>Boolean值</returns>
        public static implicit operator Boolean(ONode data)
        {
            return data.getBoolean();
        }

        /// <summary>
        /// 将ONode转换为Double值
        /// </summary>
        /// <param name="data">值</param>
        /// <returns>Double值</returns>
        public static implicit operator Double(ONode data)
        {
            return data.getDouble();
        }


        /// <summary>
        /// 将ONode转换为Int32值
        /// </summary>
        /// <param name="data">值</param>
        /// <returns>Int32值</returns>
        public static implicit operator Int32(ONode data)
        {
            return data.getInt();
        }

        /// <summary>
        /// 将ONode转换为Int64值
        /// </summary>
        /// <param name="data">值</param>
        /// <returns>Int64值</returns>
        public static implicit operator Int64(ONode data)
        {
            return data.getLong();
        }

        /// <summary>
        /// 将ONode转换为String值
        /// </summary>
        /// <param name="data">值</param>
        /// <returns>String值</returns>
        public static implicit operator String(ONode data)
        {
            return data.getString();
        }

        /// <summary>
        /// 将ONode转换为DateTime值
        /// </summary>
        /// <param name="data">值</param>
        /// <returns>DateTime值</returns>
        public static implicit operator DateTime(ONode data)
        {
            return data.getDate();
        }
        #endregion
    }
}
