using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Noear.Snacks
{
    public class SnacksException:Exception
    {
        public SnacksException():base()
        {
            
        }
        public SnacksException(String message):base(message)
        {
            
        }
    }
}
