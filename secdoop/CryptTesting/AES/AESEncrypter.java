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
 
public class AESEncrypter
{
	public static Cipher ecipher;
	public static void encrypt(InputStream in, OutputStream out)
	{
		try
		{
                        // Buffer used to transport the bytes from one stream to another
			byte[] buf = new byte[1024];
			// Bytes written to out will be encrypted
			out = new CipherOutputStream(out, ecipher);
			
			// Read in the cleartext bytes and write to out to encrypt
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
			ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			// CBC requires an initialization vector
			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

			// Encrypt
			encrypt(new FileInputStream(args[0]),new FileOutputStream("Encrypted.txt"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}

