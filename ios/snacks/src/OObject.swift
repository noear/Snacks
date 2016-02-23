//
//  OObject.swift
//  snacks
//
//  Created by 谢月甲 on 16/2/21.
//  Copyright © 2016年 noear. All rights reserved.
//

import Foundation

public class OObject {
    public final var members:[String:ONode] = [String:ONode]();
    
    public func set(key:String,_ value:ONode){
        members[key] = value;
    }
    
    public func get(key:String)->ONode{
        return members[key]!;
    }
    
    public func contains(key:String)->Bool{
        return members.contains({ (k,v) -> Bool in
            return key == k;
        });
    }
    
    public func count()->Int{
        return members.count;
    }
    
    public func clear(){
        return members.removeAll();
    }
}