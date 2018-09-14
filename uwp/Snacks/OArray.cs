using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Noear.Snacks
{
    internal class OArray
    {
        public List<ONode> elements;

        public OArray()
        {
            elements = new List<ONode>();
        }

        public void add(ONode value)
        {
            elements.Add(value);
        }

        public void add(int index, ONode value)
        {
            elements.Insert(index, value);
        }

        public ONode this[int index]
        {
            get
            {
                return elements[index];
            }
        }

        public void clear() { }
    }
}
