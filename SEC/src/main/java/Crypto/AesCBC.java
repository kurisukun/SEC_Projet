package Crypto;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import lombok.Getter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/*
inspiré de https://www.baeldung.com/java-aes-encryption-decryption
 */
public class AesCBC {

  private static final String algorithm = "AES/CBC/PKCS5Padding";
  private Cipher cipher;

  @Getter
  private final IvParameterSpec iv;
  private final SecretKey key;
  private final Logger logger = LogManager.getLogger(AesCBC.class);

  public AesCBC(SecretKey key) {
    this.key = key;
    iv = new IvParameterSpec(getIvBytes());
    try {
      cipher = Cipher.getInstance(algorithm);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      logger.error(e.getMessage());
    }
  }

  public void setEncryptMode(String srcPath, String dstPath)
      throws InvalidAlgorithmParameterException, InvalidKeyException, FileNotFoundException {
    logger.trace("setEncryptMode");
    cipher.init(Cipher.ENCRYPT_MODE, key, iv);


    String[] fileList = fileList(srcPath);
    for (String s: fileList){
      System.out.println(s);
    }
    //processData(srcPath, dstPath);
  }

  public void setDecryptMode(String srcPath, String dstPath)
      throws InvalidAlgorithmParameterException, InvalidKeyException {
    logger.trace("setDecryptMode");
    cipher.init(Cipher.DECRYPT_MODE, key, iv);
    //processData(srcPath, dstPath);
  }

  private String[] fileList(String srcPath) throws FileNotFoundException {
    logger.trace("fileList");
    File f = new File(srcPath);

    if(f.exists()) {
      if (f.isFile()) {
        return new String[] {srcPath};
      }
      else if (f.isDirectory()) {
        return f.list();
      }
    }

    throw new FileNotFoundException();
  }

  private void processData(String srcPath, String dstPath) {
    logger.trace("From : " + srcPath + ", To : " + dstPath);
    try (FileInputStream inputStream = new FileInputStream(srcPath);
        FileOutputStream outputStream = new FileOutputStream(dstPath)) {

      int bytesRead;
      byte[] buffer = new byte[1024 * 1024 * 1024];
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        byte[] output = cipher.update(buffer, 0, bytesRead);
        if (output != null) {
          outputStream.write(output);
        }
      }
      byte[] outputBytes = cipher.doFinal();
      if (outputBytes != null) {
        outputStream.write(outputBytes);
      }
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
