//
//  ONode.swift
//  snacks
//
//  Created by 谢月甲 on 16/2/21.
//  Copyright © 2016年 noear. All rights reserved.
//

import Foundation

public class ONode : ONodeBase {
    public static var NULL_DEFAULT = "";
    public static var BOOL_USE01 = true;
    public static func TIME_FORMAT_ACTION(data:NSDate)->String{
        return "";
    }
    
    
    //=============
    override init() {
        super.init();
    }
    
    init(_ int:Int32){
        super.init();
        tryInitValue();
        
        _value!.set(int);
    }
    
    init(_ long:Int64){
        super.init();
        tryInitValue();
        
        _value!.set(long);
    }
    
    init(_ double:Double){
        super.init();
        tryInitValue();
        
        _value!.set(double);
    }
    
    init(_ str:String?){
        super.init();
        tryInitValue();
        
        _value!.set(str);
    }
    
    init(_ bool:Bool){
        super.init();
        tryInitValue();
        
        _value!.set(bool);
    }
    
    init(_ date:NSDate){
        super.init();
        tryInitValue();
        
        _value!.set(date);
    }
    
    public func contains( key:String)->Bool {
        if (_object == nil || _type != ONodeType.Object){
            return false;
        }
        else{
            return _object!.contains(key);
        }
    }
    
    public func remove( key:String)->Bool {
        if (_object == nil || _type != ONodeType.Object){
            return false;
        }
        else{
            return _object!.members.removeValueForKey(key) != nil;
        }
    }
    
    public func count()->Int
    {
        if(isObject()){
            return _object!.count();
        }
        
        if(isArray()){
            return _array!.count();
        }
        
        return 0;
    }
    
    
    //========================
    public func getDouble()->Double {
        if (_value == nil){
            return 0;
        }
        else{
            return _value!.getDouble();
        }
    }
    
//    public func getDouble( scale:Int32)->Double
//    {
//        return getDouble();
//    }
    
    public func getInt()->Int32 {
        if (_value == nil){
            return 0;
        }
        else{
            return _value!.getInt();
        }
    }
    
    public func getLong()->Int64 {
        if (_value == nil){
            return 0;
        }
        else{
            return _value!.getLong();
        }
    }
    
    public func getString() ->String{
        if (_value == nil){
            return "";
        }
        else{
            return _value!.getString();
        }
    }
    
    //=============
    //返回结果节点
    public func get( index:Int) ->ONode?{
        tryInitArray();
        
        if (_array!.count() > index){
            return _array!.get(index);
        }
        else{
            return nil;
        }
    }
    //返回结果节点
    public func get( key:String) ->ONode{
        tryInitObject();
        
        if (_object!.contains(key)){
            return _object!.get(key);
        }
        else {
            let temp = ONode();
            _object!.set(key, temp);
            return temp;
        }
    }
    
    //返回自己
    public func add(value:ONode) ->ONode{
        tryInitArray();
        
        _array!.add(value);
        
        return self;
    }
    
    
    
    //返回自己
    public func set( key:String,_ value:ONode)->ONode {
        tryInitObject();
        
        _object!.set(key, value);
        
        return self;
    }
    
    //返回自己
    public  func set( key:String,_ value:String) ->ONode{
        tryInitObject();
        
        _object!.set(key, ONode(value));
        
        return self;
    }
    
    //返回自己
    public  func set( key:String,_ value:Int32)->ONode {
        tryInitObject();
        _object!.set(key, ONode(value));
        
        return self;
    }
    
    //返回自己
    public  func set( key:String,_ value:Int64) ->ONode{
        tryInitObject();
        _object!.set(key, ONode(value));
        
        return self;
    }
    
    //返回自己
    public  func set( key:String,_ value:Double) ->ONode{
        tryInitObject();
        _object!.set(key, ONode(value));
        
        return self;
    }
    
    //返回自己
    public  func set( key:String,_ value:Bool) ->ONode{
        tryInitObject();
        _object!.set(key,  ONode(value));
        
        return self;
    }
    
    //返回自己
    public  func set( key:String,_ value:NSDate)->ONode {
        tryInitObject();
        _object!.set(key,  ONode(value));
        
        return self;
    }
}