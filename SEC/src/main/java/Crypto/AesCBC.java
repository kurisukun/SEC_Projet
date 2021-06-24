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

import Zip.ZipMaker;
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

  public void encrypt(String srcPath, String dstPath)
      throws InvalidAlgorithmParameterException, InvalidKeyException, FileNotFoundException {
    logger.trace("setEncryptMode");
    cipher.init(Cipher.ENCRYPT_MODE, key, iv);
    ZipMaker z = new ZipMaker();

    try{
      z.zip(srcPath, "compressed.zip");
    } catch (IOException e) {
      e.printStackTrace();
    }
    processData("compressed.zip", dstPath);
  }

  public void decrypt(String srcPath, String dstPath)
      throws InvalidAlgorithmParameterException, InvalidKeyException {
    logger.trace("setDecryptMode");
    cipher.init(Cipher.DECRYPT_MODE, key, iv);
    processData(srcPath, dstPath);
    ZipMaker z = new ZipMaker();
    try{
      z.unzip(srcPath, "uncompressed");
    } catch (IOException e) {
      e.printStackTrace();
    }
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
