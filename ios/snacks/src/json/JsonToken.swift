//
//  JsonToken.swift
//  snacks
//
//  Created by 谢月甲 on 16/2/21.
//  Copyright © 2016年 noear. All rights reserved.
//

import Foundation

enum JsonToken{
    case None
    
    case End
    
    case ObjectStart
    case ObjectEnd
    case ArrayStart
    case ArrayEnd
    case ValueStart
    //    ValueEnd,
    
    case Int
    case Long
    case Double
    case String
    case Boolean
    case DateTime
    
    case Null
}