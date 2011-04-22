import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import javax.crypto.*; 
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;
 
 
public class AESDecrypter
{
	public static Cipher dcipher;
	public static void decrypt(InputStream in, OutputStream out)
	{
		try
		{
			// Buffer used to transport the bytes from one stream to another
			byte[] buf = new byte[1024];
			// Bytes read from in will be decrypted
			in = new CipherInputStream(in, dcipher);
			
			// Read in the decrypted bytes and write the cleartext to out
			int numRead = 0;
			while ((numRead = in.read(buf)) >= 0)
			{
				out.write(buf, 0, numRead);
			}
			out.close();
		}
		catch (java.io.IOException e)
		{
		}
	}
	
	
	public static void main(String args[])
	{
		try
		{

			char[] password = new char[] {'J','a','m','e','s'};
			byte[] salt = new byte[] {0,0,0,0,0,0,0,0};
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(password, salt, 1024, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey key = new SecretKeySpec(tmp.getEncoded(), "AES");
			


			// Create an 8-byte initialization vector
			byte[] iv = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			// CBC requires an initialization vector
			dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

			// Decrypt
			decrypt(new FileInputStream("Encrypted.txt"),new FileOutputStream("Decrypted.txt"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}

