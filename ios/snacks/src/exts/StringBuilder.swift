//
//  StringBuilder.swift
//  snacks
//
//  Created by 谢月甲 on 16/2/21.
//  Copyright © 2016年 noear. All rights reserved.
//

import Foundation

class StringBuilder {
    private var writer = String();
    
    func append(str:String){
        writer.appendContentsOf(str);
    }
    
    func append(c:Character){
        writer.append(c);
    }
    
    func append(val:Int32){
        writer.appendContentsOf(String(val));
    }
    
    func append(val:Int64){
        writer.appendContentsOf(String(val));
    }
    
    func append(val:Double){
        writer.appendContentsOf(String(val));
    }
    
    
    func toString()->String{
        return writer;
    }
}