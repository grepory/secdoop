/************************************************************************************************************/
/* AESDecryptMapper.java by James Majors                                                                    */
/* This is the Mapper function for the AES Encryption application.  The input will be split up and equally  */
/* distributed across the available nodes in the cluster.  The encryption process will be done for each     */
/* input split and paired with a key value that will help maintain the sequence of the file.  The output    */
/* will be sent to an intermediate file that will contain the key/value pairs.                              */
/************************************************************************************************************/
//package org.apache.hadoop.examples;

import java.io.*;
import java.util.*;

import javax.crypto.*; 
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

public class AESDecryptMapper extends MapReduceBase
  implements Mapper<LongWritable, BytesWritable, LongWritable, Text> {


  private static Cipher dcipher;
  static {
	try{
		if(dcipher==null){
			char[] password = new char[] {'J','a','m','e','s'};
			byte[] salt = new byte[] {0,0,0,0,0,0,0,0};
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(password, salt, 1024, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey sKey = new SecretKeySpec(tmp.getEncoded(), "AES");
			// Create an 8-byte initialization vector
			byte[] iv = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			// CBC requires an initialization vector
			dcipher.init(Cipher.DECRYPT_MODE, sKey, paramSpec);
		}
	   }
	   catch (Exception e)
	   {
		e.printStackTrace();
	   }
  	}
  public void map(LongWritable key, BytesWritable value,
      OutputCollector<LongWritable, Text> output, Reporter reporter)
      throws IOException {

	try
	{
		// Decrypt
		byte[] buffer = new byte[value.getLength()];
		System.arraycopy(value.getBytes(),0,buffer,0,value.getLength());

		Text textValue = new Text(dcipher.doFinal(buffer));
		//Output the results to an intermediate file (<LongWritable,Text>)
		output.collect(key, textValue);

	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
  }
}

