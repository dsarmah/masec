package com.masec.core.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Utility 
{
   public static byte[] base64ToByte(String data) throws IOException 
   {
       BASE64Decoder decoder = new BASE64Decoder();
       return decoder.decodeBuffer(data);
   }
 
   public static String byteToBase64(byte[] data)
   {
       BASE64Encoder endecoder = new BASE64Encoder();
       return endecoder.encode(data);
   }
   
   public static byte[] getHash(String password, byte[] salt) throws NoSuchAlgorithmException, UnsupportedEncodingException 
   {
       MessageDigest digest = MessageDigest.getInstance("SHA-256");
       digest.reset();
       digest.update(salt);
       byte[] input = digest.digest(password.getBytes("UTF-8"));
       
       for (int i = 0; i < 13; i++) 
       {
           digest.reset();
           input = digest.digest(input);
       }
       return input;
   }

}
