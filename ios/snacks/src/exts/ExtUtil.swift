//
//  ExtUtil.swift
//  snacks
//
//  Created by 谢月甲 on 16/2/21.
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

extension String{
    func charAt(i:Int) -> Character{
        let idx =  self.startIndex.advancedBy(i)
        return self[idx];
        
    }
    
    func indexOf(char:Character,_ start:Int)-> Int{
        var idx:Int = 0;
        for c in self.characters
        {
            if(idx >= start){
                if(c == char){
                    return idx;
                }
            }
            
            idx++;
        }
        
        return -1;
    }
    
    func replace(oldVal:String,_ newVal:String)->String
    {
        return self.stringByReplacingOccurrencesOfString(oldVal,withString:newVal);
    }
    
    func length()->Int{
        return self.characters.count;
    }
    
    
    func subString(start:Int)->String{
        return self.subString(start, self.length() - start);
    }
    
    func subString(start:Int,_ end:Int)->String{
        let range=NSMakeRange(start,end-start);
        
        return (self as NSString).substringWithRange(range);
    }
}