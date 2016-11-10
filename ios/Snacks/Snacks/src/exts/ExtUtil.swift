//
//  ExtUtil.swift
//  snacks
//
//  Created by noear on 16/2/21.
//  Copyright © 2016年 noear. All rights reserved.
//

import Foundation

extension Character{
    func toAscInt()-> Int{
        for c in String(self).unicodeScalars{
            return Int(c.value);
        }
        
        return -1;
    }
}

extension Date{
    func toString(_ format:String)->String{
        let frm = DateFormatter();
        frm.dateFormat = format;
        return frm.string(from: self);
    }
}

extension String{
    func charAt(_ i:Int) -> Character{
        let idx = self.index(self.startIndex, offsetBy: i); //self.startIndex.advancedBy(i)
        return self[idx];
        
    }
    
    func indexOf(_ searchstring:String)->Int
    {
        if let range = self.range(of: searchstring) {
            return self.characters.distance(from: self.startIndex, to: range.lowerBound);
        }else {
            return -1;
        }
    }
    
    func lastIndexOf(_ searchstring:String)->Int
    {
        let idx = self.range(of: searchstring, options:.backwards)?.lowerBound;
        
        if idx != nil {
            return self.distance(from: self.startIndex, to: idx!);
        }else {
            return -1;
        }
    }
    
    func indexOf(_ char:Character,_ start:Int)-> Int{
        var idx:Int = 0;
        for c in self.characters
        {
            if(idx >= start){
                if(c == char){
                    return idx;
                }
            }
            
            idx += 1;
        }
        
        return -1;
    }
    
    @discardableResult
    func replace(_ oldVal:String,_  newVal:String)->String
    {
        return self.replacingOccurrences(of: oldVal,with:newVal);
    }
    
    
    func length()->Int{
        return self.characters.count;
    }
    
    func subString(_ start:Int)->String{
        return self.substring(from: self.index(self.startIndex, offsetBy: start));
    }
    
    func subString(_ start:Int,_ end:Int)->String{
        let sIndex = self.index(self.startIndex, offsetBy: start);
        let eIndex = self.index(self.startIndex, offsetBy: end);
        
        let range = Range(uncheckedBounds: (lower: sIndex, upper: eIndex));
        return self.substring(with: range);
    }
}
