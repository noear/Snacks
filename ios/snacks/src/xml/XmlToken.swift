//
//  XmlToken.swift
//  snacks
//
//  Created by 谢月甲 on 16/2/21.
//  Copyright © 2016年 noear. All rights reserved.
//

import Foundation

enum XmlToken{
    case None
    case End
    
    case TargetStart
    case TargetEnd
    case Value
    case CDATA
}