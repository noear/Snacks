//
//  ONodeBase.swift
//  snacks
//
//  Created by 谢月甲 on 16/2/21.
//  Copyright © 2016年 noear. All rights reserved.
//

import Foundation

public class ONodeBase{
    var _array:OArray?
    var _object:OObject?
    var _value:OValue?
    
    var _type:ONodeType = ONodeType.Null;
    
    public static func tryLoad(ops:String)->ONode{
        do{
           return try load(ops);
        }catch{
            return ONode();
        }
    }
    
    public static func load(ops:String?) throws ->ONode {
        if (ops == nil || ops!.length() < 2){
            return  ONode();
        }
        
        if (ops!.charAt(0) == "<"){
            return try readXmlValue(XmlReader(ops!))!;
        }
        else{
            return try readJsonValue(JsonReader(ops!))!;
        }
    }
    
    public func toJson()->String{
        let sb =  StringBuilder();
        let writer =  JsonWriter(sb);
        
        ONode.writeJson(self, writer);
        
        return sb.toString();
    }
    
    public func toXml()->String{
        let sb =  StringBuilder();
        let writer =  XmlWriter(sb);
        
        writer.WriteNodeStart("xml");
        ONode.writeXml(self, writer);
        writer.WriteNodeEnd("xml");
        
        return sb.toString();
    }
    
    //==============
    
    func tryInitValue() {
        if (_value == nil){
            _value =  OValue();
        }
        
        if (_type != ONodeType.Value){
            _type = ONodeType.Value;
        }
    }
    
    func tryInitObject() {
        if (_object == nil){
            _object =  OObject();
        }
        
        if (_type != ONodeType.Object){
            _type = ONodeType.Object;
        }
    }
    
    func tryInitArray() {
        if (_array == nil){
            _array =  OArray();
        }
        
        if (_type != ONodeType.Array){
            _type = ONodeType.Array;
        }
    }
    
    func shiftToArray(){
        tryInitArray();
        
        if(_object != nil) {
            for n1 in _object!.members.values {
                _array!.add(n1);
            }
            
            _object!.clear();
            _object = nil;
        }
    }
    
    //================
    
    public func isObject()->Bool
    {
        return _type == ONodeType.Object;
    }
    
    public func isArray()->Bool
    {
        return _type == ONodeType.Array;
    }
    
    public func isValue()->Bool
    {
        return _type == ONodeType.Value;
    }
    
    public func asObject()->ONode
    {
        tryInitObject();
        return self as! ONode;
    }
    
    public func asArray()->ONode
    {
        tryInitArray();
        return self as! ONode;
    }
    
    func setInt(value:Int32){
        tryInitValue();
        
        _value!.set(value);
    }
    
    func setLong(value:Int64){
        tryInitValue();
        
        _value!.set(value);
    }
    
    func setDouble(value:Double){
        tryInitValue();
        
        _value!.set(value);
    }
    
    func setString(value:String?){
        tryInitValue();
        
        _value!.set(value);
    }
    
    func setBoolean( value:Bool){
        tryInitValue();
        
        _value!.set(value);
    }
    
    func setDateTime( value:NSDate){
        tryInitValue();
        
        _value!.set(value);
    }
    
    //------------------------------------
    //================
    
     static func readXmlValue(reader:XmlReader) throws ->ONode? {
        reader.Read();
        
        if (reader.Token == XmlToken.End){
            return nil;
        }
        
        let instance = ONode();
        
        if (reader.Token == XmlToken.TargetEnd) {
            instance.setString(nil);
            return instance;
        }
        
        if (reader.Token != XmlToken.TargetStart) {
            instance.setString(reader.Value);
            
            //            if (reader.Token == XmlToken.CDATA)
            //                instance.isCDATA = true;
        } else {
            instance.tryInitObject();
            
            while (true) {
                if (reader.Token != XmlToken.TargetStart){
                    break;
                }
                
                let popName = reader.Value;
                
                if (popName == nil){ //如果取不到值，则停止解析
                    throw SnacksException.XmlFormatError;
                }
                
                if (instance.contains(popName!)){ //尝试将obj转换为ary
                    instance.shiftToArray();
                }
                
                let node = try readXmlValue(reader);
                
                if (instance.isArray()){
                    instance.add(node!);
                }
                else{
                    instance.set(popName!, node!);
                }
                
                if (reader.Token == XmlToken.Value || reader.Token == XmlToken.CDATA){
                    reader.Read();
                }
                
                reader.Read();
            }
        }
        
        return instance;
    }
    
     static func readJsonValue(reader:JsonReader) throws ->ONode?
    {
        try reader.read();
        
        if (reader.Token == JsonToken.ArrayEnd){
            return nil;
        }
        
        if (reader.Token == JsonToken.ObjectEnd){
            return nil;
        }
        
        let instance =  ONode();
        
        if (reader.Token == JsonToken.Null)
        {
            instance._type = ONodeType.Null;
            return instance;
        }
        
        if (reader.Token == JsonToken.String)
        {
            instance.setString(reader.Value as? String);
            return instance;
        }
        
        if (reader.Token == JsonToken.Double)
        {
            instance.setDouble((reader.Value as! NSNumber).doubleValue);
            return instance;
        }
        
        if (reader.Token == JsonToken.Int)
        {
            instance.setInt((reader.Value as! NSNumber).intValue);
            return instance;
        }
        
        if (reader.Token == JsonToken.Long)
        {
            instance.setLong((reader.Value as! NSNumber).longLongValue);
            return instance;
        }
        
        if (reader.Token == JsonToken.Boolean)
        {
            instance.setBoolean((reader.Value as! NSNumber).boolValue);
            return instance;
        }
        
        if (reader.Token == JsonToken.DateTime)
        {
            instance.setDateTime(reader.Value as! NSDate);
            return instance;
        }
        
        if (reader.Token == JsonToken.ArrayStart)
        {
            instance.tryInitArray();
            
            while (true)
            {
                let item = try readJsonValue(reader);
                
                if (reader.Token == JsonToken.ArrayEnd)
                {
                    //解决数组套数组的问题
                    if (!(item != nil && item!.isArray() == true)){
                        break;
                    }
                }
                
                if (item == nil){ //如果取不到值，则停止解析
                    throw SnacksException.JsonFormatError;
                }
                
                instance.add(item!);
                
            }
        }
        else if (reader.Token == JsonToken.ObjectStart)
        {
            instance.tryInitObject();
            
            while (true)
            {
                try reader.ReadName();
                
                if (reader.Token == JsonToken.ObjectEnd){
                    break;
                }
                
                let property = reader.Value as! String?;
                
                if (property == nil){//如果取不到值，则停止解析
                    throw  SnacksException.JsonFormatError;
                }
                
                let val = try readJsonValue(reader);
                
                if (val == nil) {//如果取不到值，则停止解析
                    throw SnacksException.JsonFormatError;
                }
                
                instance.set(property!, val!);
            }
        }
        
        return instance;
    }
    
    //=================
    static func writeJson( node:ONodeBase, _ writer:JsonWriter)
    {
        if(node._type == ONodeType.Null){
            return;
        }
        
        if(node._type == ONodeType.Value)
        {
            writer.WriteValue(node._value!);
            return;
        }
        
        if(node._type == ONodeType.Object)
        {
            writer.WriteObjectStart();
            for (k,v) in node._object!.members
            {
                writer.WritePropertyName(k);
                writeJson(v, writer);
            }
            writer.WriteObjectEnd();
            return;
        }
        
        if(node._type == ONodeType.Array)
        {
            writer.WriteArrayStart();
            for v in node._array!.elements{
                writeJson(v,writer);
            }
            writer.WriteArrayEnd();
            return;
        }
    }
    
    static func writeXml( node:ONodeBase, _ writer:XmlWriter)
    {
        
        if(node._type == ONodeType.Null){
            return;
        }
        
        if(node._type == ONodeType.Value)
        {
            writer.WriteValue(node._value!);
            return;
        }
        
        if(node._type == ONodeType.Object)
        {
            for (k,v) in node._object!.members
            {
                writer.WriteNodeStart(k);
                writeXml(v, writer);
                writer.WriteNodeEnd(k);
            }
            return;
        }
        
        if(node._type == ONodeType.Array)
        {
            for v in node._array!.elements {
                writer.WriteNodeStart("item");
                writeXml(v, writer);
                writer.WriteNodeEnd("item");
            }
            return;
        }
    }
    
}