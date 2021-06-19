
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import picocli.CommandLine.Command;

@Command(name = "keyDestroyer", mixinStandardHelpOptions = true, version = "keyDestroyer 0.0",
    description = "try to encrypt and decrypt your usb key")
public class Sec {

  public static void main(String[] args)
      throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
    Logger logger = LogManager.getLogger(Sec.class);
    logger.debug("Début du Main");
    final String pathName = "/dev/sdc2";
    hashPassword hashArgon = new hashPassword(logger);

    // 128 pour la taille de bloc
    String password = "secret password";
    byte[] salt = {-107, -56, 76, -102, 66, 2, -35, 91, -125, -86, 16, 87, 29, 54, 4, -87};//hashArgon.generateSalt16Byte();
    byte[] Bytekey = hashArgon.argon2Hash(password, salt);
    IvParameterSpec iv = new IvParameterSpec(getIvBytes());
    //int exitCode = new CommandLine(new Sec()).execute(args);

    if (fileExists(logger, pathName)) {
      return;
    }

    SecretKey key = new SecretKeySpec(Bytekey, 0, Bytekey.length, "AES");
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, key, iv);

    try (FileInputStream inputStream = new FileInputStream(pathName);
        FileOutputStream outputStream = new FileOutputStream(pathName)) {

      //Read each byte and write it to the output file
      //value of -1 means end of file
      int bytesRead;
      byte[] buffer = new byte[1024 * 1024 * 1024]; // lecture de 1Go
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        byte[] output = cipher.update(buffer, 0, bytesRead);
        if (output != null) {
          outputStream.write(output);
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
          outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
      }
      outputStream.write(buffer);
    } catch (IOException e) {
      //Display or throw the error
      System.out.println("Erorr while execting the program: " + e.getMessage());
    } catch (IllegalBlockSizeException | BadPaddingException e) {
      e.printStackTrace();
    }
  }

  private static boolean fileExists(Logger logger, String pathName) {
    File file = new File(pathName);
    if (!file.exists()) {
      logger.error("Le fichier n'existe pas!");
      return true;
    }
    return false;
  }

  private static byte[] getIvBytes() {
    SecureRandom randomSecureRandom = new SecureRandom();
    byte[] iv = new byte[128];
    randomSecureRandom.nextBytes(iv);
    return iv;
  }
}
