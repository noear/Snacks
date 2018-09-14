using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Noear.Snacks
{
    internal class OObject
    {
        public Dictionary<String, ONode> members;

        public OObject()
        {
            members = new Dictionary<String, ONode>();
        }

        public void set(String key, ONode value)
        {
            members[key] = value;
        }

        public ONode get(String key)
        {
            return members[key];
        }

        public bool contains(String key)
        {
            return members.ContainsKey(key);
        }

        public void clear() { }
    }
}
