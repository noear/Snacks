//
//  OValue.swift
//  snacks
//
//  Created by noear on 16/2/21.
//  Copyright © 2016年 noear. All rights reserved.
//

import Foundation

public class OValue{
    private var _int:Int32 = 0;
    private var _long:Int64 = 0;
    private var _double:Double = 0;
    private var _string:String?;
    private var _bool:Bool = false;
    private var _date:Date?;
    
    public var type:OValueType = .Null;
    
    public func set(_ value:Int32)
    {
        _int = value;
        type=OValueType.Int;
    }
    
    public func set(_ value:Int64)
    {
        _long = value;
        type=OValueType.Long;
    }
    
    public func set(_ value:Double)
    {
        _double = value;
        type=OValueType.Double;
    }
    
    public func set(_ value:String?)
    {
        _string = value;
        type=OValueType.String;
    }
    
    public func set(_ value:Bool)
    {
        _bool = value;
        type=OValueType.Boolean;
    }
    
    public func set(_ value:Date)
    {
        _date = value;
        type=OValueType.DateTime;
    }
    //==================
    
    
    public func getInt()->Int32
    {
        switch (type)
        {
        case .Int: return _int;
        case .Long:return Int32(_long);
        case .Double:return Int32(_double);
        case .String:
            if (_string == nil || _string!.characters.count == 0){
                return 0;
            }
            else{
                return Int32(_string!)!;
            }
        case .Boolean:return _bool ? 1 : 0;
        case .DateTime:return 0;
        default:return 0;
        }
    }
    
    public func getLong()->Int64
    {
        switch (type)
        {
        case .Int: return Int64(_int);
        case .Long:return _long;
        case .Double:return Int64(_double);
        case .String:
            if(_string == nil || _string?.characters.count==0){
                return 0;
            }
            else{
                return Int64(_string!)!;
            }
            
        case .Boolean:return _bool ? 1 : 0;
        case .DateTime:return Int64((_date?.timeIntervalSinceNow)!);
        default:return 0;
        }
    }
    
    public func getDouble()->Double
    {
        switch (type)
        {
        case .Int: return Double(_int);
        case .Long:return Double(_long);
        case .Double:return _double;
        case .String:
            if (_string == nil || _string?.characters.count == 0){
                return 0;
            }
            else{
                return Double(_string!)!;
            }
            
        case .Boolean:return _bool ? 1 : 0;
        case .DateTime:return _date!.timeIntervalSinceNow;
        default:return 0;
        }
    }
    
    public func getString()->String
    {
        switch (type)
        {
        case .Int: return String(_int);
        case .Long:return String(_long);
        case .Double:return String(_double);
        case .String:return _string!;
        case .Boolean:return String(_bool);
        case .DateTime:return _date!.toString("yyyy-MM-dd HH:mm:ss");
        default:return "";
        }
    }
    
    public func getBoolean()->Bool
    {
        switch (type)
        {
        case .Int: return _int>0;
        case .Long:return _long>0;
        case .Double:return _double>0;
        case .String:return false;
        case .Boolean:return _bool;
        case .DateTime:return false;
        default:return false;
        }
    }
    
    public func getDate()->Date?
    {
        switch (type)
        {
        case .String:return parseDate(_string);
        case .DateTime:return _date;
        default:return nil;
        }
    }
    
    private func parseDate(_ dateString:String?)->Date? {
        if(dateString == nil){
            return nil;
        }
        else{
            let df = DateFormatter();
            df.dateFormat = "yyyy-MM-dd HH:mm:ss";
            
            return df.date(from: dateString!);
        }
    }
}
