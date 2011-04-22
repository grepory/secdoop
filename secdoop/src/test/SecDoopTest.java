import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

import org.apache.hadoop.io.*;
import org.apache.hadoop.io.Text;
import org.junit.*;


import java.io.*;
import java.util.*;

import javax.crypto.*; 
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;

import org.apache.hadoop.mapred.*;

public class SecDoopTest {


  private Cipher encAESCipher;
  private Cipher decAESCipher;
  private Cipher encDESCipher;
  private Cipher decDESCipher;
  private Cipher encDESedeCipher;
  private Cipher decDESedeCipher;

	@Before
	public void setUp() {
		try{
			//AES key generation:
			char[] aesPassword = new char[] {'J','a','m','e','s'};
			byte[] salt = new byte[] {0,0,0,0,0,0,0,0};
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(aesPassword, salt, 1024, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey sKey = new SecretKeySpec(tmp.getEncoded(), "AES");
			// Create an 8-byte initialization vector
			byte[] aesiv = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };
			AlgorithmParameterSpec aesParamSpec = new IvParameterSpec(aesiv);
			encAESCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			decAESCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			// CBC requires an initialization vector
			encAESCipher.init(Cipher.ENCRYPT_MODE, sKey, aesParamSpec);
			decAESCipher.init(Cipher.DECRYPT_MODE, sKey, aesParamSpec);


			//DES key generation:
			//Create local cipher for test case, and check to see if the results are the same as my program.
			String desPassword = "James123";
			// Create the PBE key.
			DESKeySpec desKeySpec = new DESKeySpec(desPassword.getBytes());
			SecretKey desPasswordKey = SecretKeyFactory.getInstance("DES").generateSecret(desKeySpec);
			// Initialize a cipher using this password based key
			byte[] iv = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };
			AlgorithmParameterSpec desParamSpec = new IvParameterSpec(iv);
			encDESCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			decDESCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			encDESCipher.init(Cipher.ENCRYPT_MODE, desPasswordKey, desParamSpec);
			decDESCipher.init(Cipher.DECRYPT_MODE, desPasswordKey, desParamSpec);

			//DESede key generation:
			String desedePassword = "James123James456James789";
			// Create the PBE key.
			DESedeKeySpec desedeKeySpec = new DESedeKeySpec(desedePassword.getBytes());
			SecretKey desedePasswordKey = SecretKeyFactory.getInstance("DESede").generateSecret(desedeKeySpec);
			AlgorithmParameterSpec desedeParamSpec = new IvParameterSpec(iv);
			encDESedeCipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
			decDESedeCipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
			encDESedeCipher.init(Cipher.ENCRYPT_MODE, desedePasswordKey, desedeParamSpec);
			decDESedeCipher.init(Cipher.DECRYPT_MODE, desedePasswordKey, desedeParamSpec);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	@Test
	public void aesEncryptMapperTest() throws IOException {
		try{
		  AESEncryptMapper eMapper = new AESEncryptMapper();

			Text value = new Text("This is a test!");
			// Encrypt
			byte[] buffer = new byte[value.getLength()];
			//Replace the value from the input splits with the resulting byte array
			System.arraycopy(value.getBytes(),0,buffer,0,value.getLength());
			BytesWritable outValue = new BytesWritable(encAESCipher.doFinal(buffer));

		  //Check Encryption
		  OutputCollector<LongWritable, BytesWritable> eOutput = mock(OutputCollector.class);
		  eMapper.map(null, value, eOutput, null);
		  verify(eOutput).collect(null, outValue);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void aesDecryptMapperTest() throws IOException {
		try{
		  AESDecryptMapper dMapper = new AESDecryptMapper();

			Text value = new Text("This is a test!");
			// Encrypt
			byte[] buffer = new byte[value.getLength()];
			//Replace the value from the input splits with the resulting byte array
			System.arraycopy(value.getBytes(),0,buffer,0,value.getLength());
			BytesWritable outValue = new BytesWritable(encAESCipher.doFinal(buffer));

		  	//Switch to Decrypt
			buffer = new byte[outValue.getLength()];
			System.arraycopy(outValue.getBytes(),0,buffer,0,outValue.getLength());
			Text textValue = new Text(decAESCipher.doFinal(buffer));

		  //Check the Decryption
		  OutputCollector<LongWritable, Text> dOutput = mock(OutputCollector.class);
		  dMapper.map(null, outValue, dOutput, null);
		  verify(dOutput).collect(null, value);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Test
	public void desEncryptMapperTest() throws IOException {
		try{
		  DESEncryptMapper eMapper = new DESEncryptMapper();

			Text value = new Text("This is a test!");
			// Encrypt
			byte[] buffer = new byte[value.getLength()];
			//Replace the value from the input splits with the resulting byte array
			System.arraycopy(value.getBytes(),0,buffer,0,value.getLength());
			BytesWritable outValue = new BytesWritable(encDESCipher.doFinal(buffer));

		  //Check Encryption
		  OutputCollector<LongWritable, BytesWritable> eOutput = mock(OutputCollector.class);
		  eMapper.map(null, value, eOutput, null);
		  verify(eOutput).collect(null, outValue);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void desDecryptMapperTest() throws IOException {
		try{
		  DESDecryptMapper dMapper = new DESDecryptMapper();

			Text value = new Text("This is a test!");
			// Encrypt
			byte[] buffer = new byte[value.getLength()];
			//Replace the value from the input splits with the resulting byte array
			System.arraycopy(value.getBytes(),0,buffer,0,value.getLength());
			BytesWritable outValue = new BytesWritable(encDESCipher.doFinal(buffer));

			//Switch to Decrypt
			buffer = new byte[outValue.getLength()];
			System.arraycopy(outValue.getBytes(),0,buffer,0,outValue.getLength());

			Text textValue = new Text(decDESCipher.doFinal(buffer));

		  //Check the Decryption
		  OutputCollector<LongWritable, Text> dOutput = mock(OutputCollector.class);
		  dMapper.map(null, outValue, dOutput, null);
		  verify(dOutput).collect(null, value);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	@Test
	public void desedeEncryptMapperTest() throws IOException {
		try{
		  DESedeEncryptMapper eMapper = new DESedeEncryptMapper();

			Text value = new Text("This is a test!");
			// Encrypt
			byte[] buffer = new byte[value.getLength()];
			//Replace the value from the input splits with the resulting byte array
			System.arraycopy(value.getBytes(),0,buffer,0,value.getLength());
			BytesWritable outValue = new BytesWritable(encDESedeCipher.doFinal(buffer));

		  //Check Encryption
		  OutputCollector<LongWritable, BytesWritable> eOutput = mock(OutputCollector.class);
		  eMapper.map(null, value, eOutput, null);
		  verify(eOutput).collect(null, outValue);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void desedeDecryptMapperTest() throws IOException {
		try{
		  DESedeDecryptMapper dMapper = new DESedeDecryptMapper();

			Text value = new Text("This is a test!");
			// Encrypt
			byte[] buffer = new byte[value.getLength()];
			//Replace the value from the input splits with the resulting byte array
			System.arraycopy(value.getBytes(),0,buffer,0,value.getLength());
			BytesWritable outValue = new BytesWritable(encDESedeCipher.doFinal(buffer));

			//Switch to Decrypt
			buffer = new byte[outValue.getLength()];
			System.arraycopy(outValue.getBytes(),0,buffer,0,outValue.getLength());

			Text textValue = new Text(decDESedeCipher.doFinal(buffer));

		  //Check the Decryption
		  OutputCollector<LongWritable, Text> dOutput = mock(OutputCollector.class);
		  dMapper.map(null, outValue, dOutput, null);
		  verify(dOutput).collect(null, value);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}

