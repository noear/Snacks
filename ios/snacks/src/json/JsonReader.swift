//
//  JsonReader.swift
//  snacks
//
//  Created by 谢月甲 on 16/2/21.
//  Copyright © 2016年 noear. All rights reserved.
//

import Foundation

class JsonReader {
    private var _reader:StringReader!;
    
    init(_ json_text:String)
    {
        _reader = StringReader(json_text);
    }
    
    
     var Value:AnyObject?;
     var Token:JsonToken = .Null;
    
    
    /// <summary>
    /// try read obj
    /// </summary>
    private func ObjTryRead( buffer_sb:StringBuilder,_  buffer_char:Character,_ token:JsonToken)->Bool
    {
        if (Token != JsonToken.ValueStart)
        {
            Token = token;
            return true;
        }
        else
        {
            buffer_sb.append(buffer_char);
            return false;
        }
    }
    
    func read() throws ->Bool
    {
        Token = JsonToken.None;
        Value = nil;
        var buffer_char:Character;
        var value_type:ValueType = .None;
        let buffer_sb = StringBuilder();
        
        while (true)
        {
            buffer_char = try _reader.read();
            
            if (buffer_char.toAscInt() == -1)
            {
                Token = JsonToken.End;
                return true;
            }
            
            let temp = buffer_char;
            
            switch (temp)
            {
                //#region object,array
            case "{":
                
                    if (ObjTryRead(buffer_sb, buffer_char, JsonToken.ObjectStart)){
                        return true;
                    }
                break;
                case "}":
                
                    if (ObjTryRead(buffer_sb, buffer_char, JsonToken.ObjectEnd)){
                    return true;
                    }
                break;
            case "[":
            
                if (ObjTryRead(buffer_sb, buffer_char, JsonToken.ArrayStart)){
                    return true;
                }
                break;
            case "]":
                
                    if (ObjTryRead(buffer_sb, buffer_char, JsonToken.ArrayEnd)){
                        return true;
                    }
                break;
                //#endregion
                
            case "\\":
                
                    if (Token == JsonToken.ValueStart) //转义符处理
                    {
                        let c = try _reader.read();
                        switch (c)
                        {
                        case "\'":
                            buffer_sb.append("\'");
                            break;
                            
                        case "\"":
                            buffer_sb.append("\"");
                                break;
                            
                        case "/":
                            buffer_sb.append("/");
                            break;
                            
                        case "n":
                            buffer_sb.append("\n");
                            break;
                            
                        case "r":
                            buffer_sb.append("\r");
                            break;
                            
                        case "t":
                            buffer_sb.append("\t");
                            break;
                            
                            
                        default:
                            buffer_sb.append(buffer_char);
                            buffer_sb.append(c);
                            break;
                            
                        }
                    }
                
                break;
                
                //#region string
            case "\"":
                
                    if (Token != JsonToken.ValueStart)
                    {
                        Token = JsonToken.ValueStart;
                        value_type = ValueType.String2;
                    }
                    else
                    {
                        if (value_type == ValueType.String2)
                        {
                            Token = JsonToken.String;
                            Value = buffer_sb.toString();
                            return true;
                        }
                        
                        buffer_sb.append(buffer_char);
                    }
                
                break;
                
            case "\'":
                
                    if (Token != JsonToken.ValueStart)
                    {
                        Token = JsonToken.ValueStart;
                        value_type = ValueType.String1;
                    }
                    else
                    {
                        if (value_type == ValueType.String1)
                        {
                            Token = JsonToken.String;
                            Value = buffer_sb.toString();
                            return true;
                        }
                        
                        buffer_sb.append( buffer_char);
                    }
                
                break;
                //#endregion
                
                //#region bool
            case "t":
                
                    if (Token != JsonToken.ValueStart)
                    {
                        Token = JsonToken.Boolean;
                        try _reader.read();//true
                        try _reader.read();
                        buffer_char = try _reader.read();
                        Value = true;
                        return true;
                    }
                    
                    buffer_sb.append( buffer_char);
                
                break;
                
            case "f":
                
                    if (Token != JsonToken.ValueStart)
                    {
                        Token = JsonToken.Boolean;
                        try _reader.read();//false
                        try _reader.read();
                        try _reader.read();
                        buffer_char = try _reader.read();
                        Value = false;
                        return true;
                    }
                    
                    buffer_sb.append( buffer_char);
                 break;
                //#endregion
                
                //#region datetime
            case "/":
                
                    if (Token != JsonToken.ValueStart)
                    {
                        Token = JsonToken.DateTime;
                        
                        while (true)
                        {
                            buffer_char = try _reader.read();
                            if (buffer_char == "/") //结束
                            {
                                outputDateTime(buffer_sb.toString());
                                return true;
                            }
                            else{
                                buffer_sb.append(buffer_char);
                            }
                            
                            if (buffer_char.toAscInt() < 0){ //出错
                                return true;
                            }
                        }
                    }
                    
                    buffer_sb.append( buffer_char); //不需要先打入/
                 break;
                
                //#endregion
                
                //#region datetime 或 null
            case "n":
                
                    buffer_sb.append( buffer_char); //要先打入字符
                    
                    if (Token != JsonToken.ValueStart)
                    {
                        buffer_sb.append(try _reader.read());
                        buffer_sb.append(try _reader.read());
                        buffer_char = try _reader.read()
                        buffer_sb.append(buffer_char);
                        
                        if (buffer_char == "l") //为null
                        {
                            Value = nil;
                            Token = JsonToken.Null;
                            return true;
                        }
                        else //为时间
                        {
                            while (true)
                            {
                                buffer_char = try _reader.read()
                                buffer_sb.append(buffer_char);
                                
                                if (buffer_char == ")") //结束
                                {
                                    outputDateTime(buffer_sb.toString());
                                    return true;
                                }
                                
                                if (buffer_char.toAscInt() < 0){ //出错
                                    return false;
                                }
                            }
                        }
                    }
                 break;
                //#endregion
                
                //#region number
            default: //对字符串处理的补充
                
                    // 值由::/,字母,",',数字 开始
                    //字符串::以",'结束
                    //否则以 ,: ]}结束
                    
                    if (Token != JsonToken.ValueStart) //如果还没有开始值
                    {
                        //48 === 0
                        //45 === -（负号）20110809
                        var int_char = buffer_char.toAscInt();
                        if ((int_char >= 48 && int_char <= 57) || int_char == 45) //0-9的值
                        {
                            buffer_sb.append(buffer_char);
                            
                            Token = JsonToken.ValueStart;
                            value_type = ValueType.Number;
                            
                            while (true)
                            {
                                buffer_char = try _reader.peek();
                                int_char = buffer_char.toAscInt();
                                
                                if (int_char == 46) //小数点.
                                {
                                    value_type = ValueType.Number_Double;
                                    buffer_sb.append(try _reader.read());
                                    continue;
                                }
                                
                                if ((int_char >= 48 && int_char <= 57)) //(0-9) 或 .
                                {
                                    buffer_sb.append(try _reader.read());
                                    continue;
                                }
                                
                                //69:E,101:e
                                if (int_char == 45 || int_char == 69 || int_char == 101)
                                {
                                    buffer_sb.append(try _reader.read());
                                    continue;
                                }
                                
                                outputNumber(buffer_sb, value_type);
                                return true;
                            }
                        }
                    }
                    else{
                        buffer_sb.append( buffer_char);
                    }
                
                break;
                //#endregion
            }
        }
    }
    
    func ReadName() throws ->Bool
    {
        Token = JsonToken.None;
        Value = nil;
        var buffer_char:Character;
        var value_type:ValueType = .None;
        var buffer_sb = StringBuilder();
        
        while (true)
        {
            buffer_char = try _reader.read();
            
            if (buffer_char.toAscInt() == -1)
            {
                Token = JsonToken.End;
                return true;
            }
            
            let temp = buffer_char;
            
            switch (temp)
            {
                //#region object,array
            case "{":
                
                    if (ObjTryRead(buffer_sb, buffer_char, JsonToken.ObjectStart))
                    {
                        return true;
                    }
                 break;
                case "}":
                
                    if (ObjTryRead(buffer_sb, buffer_char, JsonToken.ObjectEnd))
                    {
                        return true;
                    }
                 break;
            case "[":
            
            if (ObjTryRead(buffer_sb, buffer_char, JsonToken.ArrayStart))
            {
            return true;
            }
             break;
            case "]":
                
                    if (ObjTryRead(buffer_sb, buffer_char, JsonToken.ArrayEnd))
                    {
                        return true;
                    }
                 break;
                
                //case ',':
                //    {
                //        Token = JsonToken.End;
                
                //        return false;
                //    }break;
                //#endregion
                
                //#region string
            case "\"":
                
                    if (Token != JsonToken.ValueStart)
                    {
                        Token = JsonToken.ValueStart;
                        value_type = ValueType.String2;
                    }
                    else
                    {
                        if (value_type == ValueType.String2)
                        {
                            Token = JsonToken.String;
                            Value = buffer_sb.toString();
                            
                            //if (last_buffer_char != '\\')
                            //{
                            return true;
                            //}
                        }
                        
                        buffer_sb.append(buffer_char);
                    }
                
                break;
                
            case "\'":
                
                    if (Token != JsonToken.ValueStart)
                    {
                        Token = JsonToken.ValueStart;
                        value_type = ValueType.String1;
                    }
                    else
                    {
                        if (value_type == ValueType.String1)
                        {
                            Token = JsonToken.String;
                            Value = buffer_sb.toString();
                            return true;
                        }
                        
                        buffer_sb.append( buffer_char);
                    }
                
                break;
                //#endregion
                
            case " ",":":
            
                if (value_type == ValueType.String3)
                {
                    Token = JsonToken.String;
                    Value = buffer_sb.toString();
                    return true;
                }
                
                if (Token == JsonToken.ValueStart){
                    buffer_sb.append( buffer_char);
                }
            
            break;
                
            default: //对字符串处理的补充
                
                    // 值由::/,字母,",',数字 开始
                    //字符串::以",'结束
                    //否则以 ,: ]}结束
                    if (Token != JsonToken.ValueStart)
                    {
                        let int_char = buffer_char.toAscInt();
                        if (int_char == 36 || int_char == 95 || (int_char >= 65 && int_char <= 90) || (int_char >= 97 && int_char <= 122))
                        {
                            Token = JsonToken.ValueStart;
                            value_type = ValueType.String3;
                        }
                    }
                    
                    if (Token == JsonToken.ValueStart){
                        buffer_sb.append(buffer_char);
                    }
                
                break;
                
            }
        }
    }
    
    private func outputNumber(buffer_sb:StringBuilder, _ value_type:ValueType)
    {
        if (value_type == ValueType.Number)
        {
            let temp = Int64(buffer_sb.toString())!;
            
            if (temp > Int64(Int32.max))
            {
                Token = JsonToken.Long;
                Value = NSNumber(longLong: temp);
            }
            else
            {
                Token = JsonToken.Int;
                Value = NSNumber(int: Int32(temp));
            }
        }
        else
        {
            Token = JsonToken.Double;
            Value = NSNumber(double: Double(buffer_sb.toString())!);
        }
    }
    
    private func outputDateTime(buffer_str:String)
    {
        /*
        Pattern pattern = Pattern.compile("\\d+");
        Matcher m1 = pattern.matcher(buffer_str);
        
        if (m1.matches())
        {
            Date temp = new Date(Long.parseLong(m1.group()));
            if (m1.groupCount() > 1)
            {
                
                int kind = Integer.parseInt(m1.group(1));
                int _op = (buffer_str.indexOf('+') > 0 ? 1 : -1);
                //                if (kind > 100)
                //                {
                //                    int kind_m = kind - (kind / 100) * 100;
                //
                //
                //
                //                    temp.AddHours(_op * kind / 100);
                //                    temp.AddMinutes(_op * kind_m);
                //                }
                //                else
                //                    temp.AddHours(_op * kind);
            }
            
            Value = temp;
        }
        else*/
        Value = NSDate();
    }
}