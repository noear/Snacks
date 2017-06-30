//
//  StringReader.swift
//  snacks
//
//  Created by noear on 16/2/21.
//  Copyright © 2016年 noear. All rights reserved.
//

import Foundation

class StringReader {
    private var str:[Character]?;
    private var length:Int;
    private var next:Int = 0;
    private var mark:Int = 0;
    
    init(_ s:String) {
        self.str = [Character](s.characters);
        self.length = s.length();
    }
    
    private func ensureOpen() throws {
        if (str == nil){
            throw SnacksException.StringIsNull;
        }
    }
    
    @discardableResult
    func peek() throws ->Character {
        try ensureOpen();
        
        if (next >= length){
            return Character(UnicodeScalar(0));
        }
        return str![next];
    }
    
    @discardableResult
    func read() throws ->Character {
        try ensureOpen();
        if (next >= length){
            return Character(UnicodeScalar(0));
        }
        
        
        let temp = str![next];
        next += 1;
        return temp;
    }
    
//     func read(char cbuf[], int off, int len) throws ->Character {
//        
//            ensureOpen();
//            if ((off < 0) || (off > cbuf.length) || (len < 0) ||
//                ((off + len) > cbuf.length) || ((off + len) < 0)) {
//                    throw new IndexOutOfBoundsException();
//            } else if (len == 0) {
//                return 0;
//            }
//            if (next >= length)
//            return -1;
//            int n = Math.min(length - next, len);
//            str.getChars(next, next + n, cbuf, off);
//            next += n;
//            return n;
//        
//    }
    
//    func skip( ns:Int64) throws -> Int64 {
//       
//            ensureOpen();
//            if (next >= length)
//            return 0;
//            // Bound skip by beginning and end of the source
//            long n = Math.min(length - next, ns);
//            n = Math.max(-next, n);
//            next += n;
//            return n;
//        
//    }
//    
//     func ready() throws ->Bool {
//        
//            ensureOpen();
//            return true;
//        
//    }
//    
//     func markSupported()->Bool {
//        return true;
//    }
//    
//    
//     func mark(int readAheadLimit) throws {
//        if (readAheadLimit < 0){
//            throw new IllegalArgumentException("Read-ahead limit < 0");
//        }
//        
//            ensureOpen();
//            mark = next;
//        
//    }
//    
//     func reset() throws  {
//       
//            ensureOpen();
//            next = mark;
//        
//    }
    
     func close() {
        str = nil;
    }
}
