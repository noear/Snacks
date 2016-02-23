//
//  SnacksException.swift
//  snacks
//
//  Created by 谢月甲 on 16/2/21.
//  Copyright © 2016年 noear. All rights reserved.
//

import Foundation

public enum SnacksException : ErrorType
{
    case StreamClosed
    case StringIsNull
    
    case XmlFormatError
    case JsonFormatError
}