//
//  StringBuilder.swift
//  snacks
//
//  Created by noear on 16/2/21.
//  Copyright © 2016年 noear. All rights reserved.
//

import Foundation

class StringBuilder {
    private var writer = String();
    
    func append(_ str:String){
        writer.append(str);
    }
    
    func append(_ c:Character){
        writer.append(c);
    }
    
    func append(_ val:Int32){
        writer.append(String(val));
    }
    
    func append(_ val:Int64){
        writer.append(String(val));
    }
    
    func append(_ val:Double){
        writer.append(String(val));
    }
    
    
    func toString()->String{
        return writer;
    }
}
