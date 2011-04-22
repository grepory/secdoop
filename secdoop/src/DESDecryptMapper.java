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

public class DESDecryptMapper extends MapReduceBase
  implements Mapper<LongWritable, BytesWritable, LongWritable, Text> {

  private static Cipher dcipher;
  static {
	try{
		if(dcipher==null){
			String password = "James123";
		
			// Create the PBE key.
			DESKeySpec keySpec = new DESKeySpec(password.getBytes());
			SecretKey passwordKey = SecretKeyFactory.getInstance("DES").generateSecret(keySpec);

			// Initialize a cipher using this password based key
			byte[] iv = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);

			dcipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			dcipher.init(Cipher.DECRYPT_MODE, passwordKey, paramSpec);
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

