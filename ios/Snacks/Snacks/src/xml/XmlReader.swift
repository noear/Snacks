//
//  XmlReader.swift
//  snacks
//
//  Created by noear on 16/2/21.
//  Copyright © 2016年 noear. All rights reserved.
//

import Foundation

class XmlReader{
    private var xml_text:String!;
    private var xml_index:Int = -1;
    private var xml_count:Int = 0;
    
    var Value:String?;
    var Token:XmlToken = .None;
    
    init(_ xml:String) {
        xml_text = xml;
        xml_count = xml.characters.count;
    }
    
    @discardableResult
    private func text_read()->Character{
        if (xml_index >= xml_count){
            return Character(UnicodeScalar(0));
        }
        xml_index += 1;
        return xml_text.charAt(xml_index);
    }
    
    private func text_peek()->Character{
        return xml_text.charAt(xml_index);
    }
    
    private func text_jump(_ len:Int){
        xml_index += len+1;
    }
    
    private func text_jump(_ char:Character){
        xml_index = xml_text.indexOf(char, xml_index)+1;
    }

    
    //------------------------
    private func ReadName()
    {
        let temp = xml_text.indexOf(">", xml_index);
        if (temp < 1){
            self.Value = "";
        }
        else
        {
            self.Value = xml_text.subString(xml_index, temp);
            xml_index = temp+1;
        }
    }
    
    private func ReadCDATA()
    {
        let xml_index2 = xml_index;
        
        while (true)
        {
            xml_index = xml_text.indexOf("]", xml_index);
            
            if (xml_text.charAt(xml_index + 1) == "]" && xml_text.charAt(xml_index + 2) == ">")
            {
                self.Value = xml_text.subString(xml_index2, xml_index);
                xml_index = xml_index + 3;
                return;
            }
            else{
                xml_index += 1;
            }
        }
    }
    
    private func ReadValue()
    {
        let temp = xml_text.indexOf("<", xml_index);
        if (temp < 1){
            self.Value = "";
        }
        else
        {
            xml_index = xml_index - 1;//回退一个字符
            
            let s = xml_text.subString(xml_index, temp);
            
            s.replace("&gt;", ">");
            s.replace("&lt;", "<");
            s.replace("&amp;", "&");
            
            //this.Value = xml_text.Substring(xml_index, temp - xml_index);
            self.Value = s;
            xml_index  = temp;
        }
    }
    //------------------------
    @discardableResult
    func Read()->Bool
    {
        Token = XmlToken.None;
        Value = nil;
        var buffer_char:Character;
        var buffer_peek:Character;
        
        while (true)
        {
            buffer_char = text_read();
            
            if (buffer_char.toAscInt() <= 0)
            {
                Token = XmlToken.End;
                return true;
            }
            
            if (buffer_char == "<")
            {
                buffer_peek = text_peek();
                
                if (buffer_peek == "/")
                {
                    Token = XmlToken.TargetEnd;
                    ReadName();
                    return true;
                }
                else if (buffer_peek == "!")
                {
                    text_read();
                    buffer_peek = text_peek();
                    if (buffer_peek == "-"){//<!-- 注释
                        text_jump(">");
                    }
                    else //<![CDATA[
                    {
                        text_jump(6);
                        Token = XmlToken.CDATA;
                        ReadCDATA();
                        return true;
                    }
                }
                else if (buffer_peek == "?"){
                    text_jump(">");
                }
                else
                {
                    Token = XmlToken.TargetStart;
                    ReadName();
                    return true;
                }
            }
            else
            {
                if (Token == XmlToken.None && buffer_char.toAscInt() > 32)
                {
                    Token = XmlToken.Value;
                    ReadValue();
                    return true;
                }
            }
        }
    }
}
