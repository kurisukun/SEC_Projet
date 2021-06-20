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
import lombok.Getter;
import org.apache.log4j.Logger;

public class AesCBC {
  private static final String algorithm = "AES/CBC/PKCS5Padding";
  private Cipher cipher;

  @Getter
  private IvParameterSpec iv;
  private SecretKey key;
  private Logger logger;

  public AesCBC(SecretKey key, Logger logger) {
    this.key = key;
    this.logger = logger;
    iv = new IvParameterSpec(getIvBytes());
    try {
      cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      logger.error(e.getMessage());
    }
  }

  public void setEncryptMode(String srcPath, String dstPath) throws InvalidAlgorithmParameterException, InvalidKeyException {
    cipher.init(Cipher.DECRYPT_MODE, key, iv);
    processData(srcPath, dstPath);
  }
  public void setDecryptMode(String srcPath, String dstPath) throws InvalidAlgorithmParameterException, InvalidKeyException {
    cipher.init(Cipher.ENCRYPT_MODE, key, iv);
    processData(srcPath, dstPath);
  }

  public void encrypt(){

  }

  public void decrypt(){

  }

  private void processData(String srcPath, String dstPath) {
    try (FileInputStream inputStream = new FileInputStream(srcPath);
        FileOutputStream outputStream = new FileOutputStream(dstPath)) {

      int bytesRead;
      int offset = 0;
      byte[] buffer = new byte[1024 * 1024 * 1024]; // lecture de 1Go
      while ((bytesRead = inputStream.read(buffer, offset, buffer.length)) != -1) {
        byte[] output = cipher.update(buffer, 0, bytesRead);
        if (output != null) {
          outputStream.write(output);
        }
      }
      byte[] outputBytes = cipher.doFinal();
      if (outputBytes != null) {
        outputStream.write(outputBytes);
      }
      inputStream.close();
      outputStream.close();
    } catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
      logger.error(e.getMessage());
    }
  }

  private byte[] getIvBytes() {
    SecureRandom randomSecureRandom = new SecureRandom();
    byte[] iv = new byte[16];
    randomSecureRandom.nextBytes(iv);
    return iv;
  }
}
