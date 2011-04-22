import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.crypto.spec.DESKeySpec;
import java.util.*;
import java.security.spec.AlgorithmParameterSpec;

public class DESEncrypter
{
   private static FileInputStream inFile;
   private static FileOutputStream outFile;
 
 
   public static void main(String[] args) throws Exception
   {
 
      // Password must be at least 8 characters (bytes) long
      String password = "James123";


      // File to encrypt.  It does not have to be a text file! 
      inFile = new FileInputStream(args[0]);
      outFile = new FileOutputStream("Encrypted.txt");


       // Create the PBE key.

       DESKeySpec keySpec = new DESKeySpec(password.getBytes());
       SecretKey passwordKey = SecretKeyFactory.getInstance("DES").generateSecret(keySpec);

       // Initialize a cipher using this password based key
	byte[] iv = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };
	AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, passwordKey, paramSpec);

      // Need to write the salt to the (encrypted) file.  The
      // salt is needed when reconstructing the key for decryption.
 

      // Read the file and encrypt its bytes.
 
      byte[] input = new byte[64];
      int bytesRead;
      while ((bytesRead = inFile.read(input)) != -1)
      {
         byte[] output = cipher.update(input, 0, bytesRead);
         if (output != null) outFile.write(output);
      }

      byte[] output = cipher.doFinal();
      if (output != null) outFile.write(output);

      inFile.close();
      outFile.flush();
      outFile.close();
 
   }
}


