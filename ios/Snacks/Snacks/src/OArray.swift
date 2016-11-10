//
//  OArray.swift
//  snacks
//
//  Created by noear on 16/2/21.
//  Copyright © 2016年 noear. All rights reserved.
//

import Foundation

public class OArray{
    public final var elements:[ONode] = [];
    
    public func add(_ value:ONode){
        elements.append(value);
    }
    
    public func add(_ index:Int,_ value:ONode){
        elements.insert(value, at: index);
    }
    
    public func get(_ index:Int) -> ONode{
        return elements[index];
    }
    
    public func count()->Int{
        return elements.count;
    }
    
    public func clear(){
        elements.removeAll();
    }
}
