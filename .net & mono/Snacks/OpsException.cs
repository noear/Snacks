using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Noear.Snacks
{
    public class OpsException:Exception
    {
        public OpsException():base()
        {
            
        }
        public OpsException(String message):base(message)
        {
            
        }
    }
}
