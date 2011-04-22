/************************************************************************************************************/
/* DESEncryptMapper.java by James Majors                                                                    */
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

public class DESEncryptMapper extends MapReduceBase
  implements Mapper<LongWritable, Text, LongWritable, BytesWritable> {

  private static Cipher ecipher;
  static {
	try{
		if(ecipher==null){
			String password = "James123";
		
			// Create the PBE key.
			DESKeySpec keySpec = new DESKeySpec(password.getBytes());
			SecretKey passwordKey = SecretKeyFactory.getInstance("DES").generateSecret(keySpec);

			// Initialize a cipher using this password based key
			byte[] iv = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);

			ecipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			ecipher.init(Cipher.ENCRYPT_MODE, passwordKey, paramSpec);
		}
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
  }


  public void map(LongWritable key, Text value,
      OutputCollector<LongWritable, BytesWritable> output, Reporter reporter)
      throws IOException {
	
	try
	{
		// Encrypt
		byte[] buffer = new byte[value.getLength()];
		//Replace the value from the input splits with the resulting byte array
		System.arraycopy(value.getBytes(),0,buffer,0,value.getLength());
		

		BytesWritable outValue = new BytesWritable(ecipher.doFinal(buffer));
		//Output the results to an intermediate file (<LongWritable,BytesWritable>)
		output.collect(key, outValue);
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
  }
}

