//
//  SnacksException.swift
//  snacks
//
//  Created by noear on 16/2/21.
//  Copyright © 2016年 noear. All rights reserved.
//

import Foundation

public enum SnacksException : Error
{
    case StreamClosed
    case StringIsNull
    
    case XmlFormatError
    case JsonFormatError
}
