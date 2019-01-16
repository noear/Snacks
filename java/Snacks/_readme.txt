ONode::
即是 value
也是 object
还是 array

================
1.0.26::
添加addAll() 聚合数组
添加setAll() 聚合对象

1.0.29::
添加exp(n->..) 接口

1.1.1::
n.val(Object) //为值节点设置或修值（仅基础基础类型）
n.exp((ONode)->{}) //输出自己进行操作

n.set(key, Object) //为对象节点设置属性（T仅支持基础类型）
n.setAll(Map<String,Object>) //为对象节点添加集合（仅支持基础类型）

n.add(Object) //为数组节点添加值（仅支持基础类型）
n.addAll(Iterable<Object>) //为数组节点添加集合（仅支持基础类型）
