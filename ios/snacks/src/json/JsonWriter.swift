//
//  JsonWriter.swift
//  snacks
//
//  Created by 谢月甲 on 16/2/21.
//  Copyright © 2016年 noear. All rights reserved.
//

import Foundation

class JsonWriter {
    var _LastIsEnd:Bool = false;
    var _Writer:StringBuilder;
    init(_ writer:StringBuilder){
        _Writer = writer;
    }
    
    func WriteObjectStart()
    {
        if (_LastIsEnd)
        {
            _Writer.append(",");
        }
        
        _Writer.append("{");
        _LastIsEnd = false;
    }
    func WriteObjectEnd()
    {
        _Writer.append("}");
        _LastIsEnd = true;
    }
    func WriteArrayStart()
    {
        if (_LastIsEnd)
        {
            _Writer.append(",");
        }
        
        _Writer.append("[");
        _LastIsEnd = false;
    }
    
    func WriteArrayEnd()
    {
        _Writer.append("]");
        _LastIsEnd = true;
    }
    
    func WritePropertyName(name:String)
    {
        if (_LastIsEnd)
        {
            _Writer.append(",");
        }
        
        _Writer.append("\"");
        _Writer.append(name);
        _Writer.append("\"");
        _Writer.append(":");
        
        _LastIsEnd = false;
    }
    
    private func OnWriteBef()
    {
        if (_LastIsEnd) //是否上一个元素已结束
        {
            _Writer.append(",");
        }
    }
    
    func WriteValue(val:String?)
    {
        OnWriteBef();
        
        if (val == nil)
        {
            _Writer.append(ONode.NULL_DEFAULT);
        }
        else
        {
            _Writer.append("\"");
            
            for c in val!.characters
            {
                switch (c)
                {
                case "\\":
                    _Writer.append("\\\\"); //20110809
                    break;
                    
                case "\"":
                    _Writer.append("\\\"");
                    break;
                    
                case "\n":
                    _Writer.append("\\n");
                    break;
                    
                case "\r":
                    _Writer.append("\\r");
                    break;
                    
                case "\t":
                    _Writer.append("\\t");
                    break;
                    
                default:
                    _Writer.append(c);
                    break;
                }
            }
            
            _Writer.append("\"");
        }
        _LastIsEnd = true;
    }
    
    func WriteValue(val:Bool)
    {
        OnWriteBef();
        if (ONode.BOOL_USE01)
        {
            _Writer.append(val ? "1" : "0");
        }
        else
        {
            _Writer.append(val ? "true" : "false");
        }
        _LastIsEnd = true;
    }
    
    func WriteValue(val:Double)
    {
        OnWriteBef();
        _Writer.append(val);
        _LastIsEnd = true;
    }
    
    func WriteValue(val:Int32)
    {
        OnWriteBef();
        _Writer.append(val);
        _LastIsEnd = true;
    }
    
    func WriteValue(val:Int64)
    {
        OnWriteBef();
        _Writer.append(val);
        _LastIsEnd = true;
    }
    
    func WriteValue(val:NSDate)
    {
        OnWriteBef();
        _Writer.append("\"");
        _Writer.append(ONode.TIME_FORMAT_ACTION(val));
        _Writer.append("\"");
        _LastIsEnd = true;
    }
    
    func WriteValue(val:OValue)
    {
        switch (val.type)
        {
        case .Int:WriteValue(val.getInt());break;
        case .Long:WriteValue(val.getLong());break;
        case .Double:WriteValue(val.getDouble());break;
        case .String:WriteValue(val.getString());break;
        case .Boolean:WriteValue(val.getBoolean());break;
        case .DateTime:WriteValue(val.getDate()!);break;
        case .Null:WriteValue("");break;
        }
    }
}