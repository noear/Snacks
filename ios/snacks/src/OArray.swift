//
//  OArray.swift
//  snacks
//
//  Created by 谢月甲 on 16/2/21.
//  Copyright © 2016年 noear. All rights reserved.
//

import Foundation

public class OArray{
    public final var elements:[ONode] = [];
    
    public func add(value:ONode){
        elements.append(value);
    }
    
    public func add(index:Int,_ value:ONode){
        elements.insert(value, atIndex: index);
    }
    
    public func get(index:Int) -> ONode{
        return elements[index];
    }
    
    public func count()->Int{
        return elements.count;
    }
    
    public func clear(){
        elements.removeAll();
    }
}