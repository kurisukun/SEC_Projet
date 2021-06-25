import ConfigFile.FileConfigManagement;
import Crypto.AesCBC;
import Crypto.HashPassword;
import Validation.PasswordValidation;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.List;


public class TestEncryptAndDecrypt {

  private static final Logger logger = LogManager.getLogger(TestEncryptAndDecrypt.class);

  private static void initOneFile(File file) {
    try {
      if (!file.exists()) {
        file.createNewFile();
      }
      PrintWriter writer = new PrintWriter(file.getName(), "UTF-8");
      writer.println("The first line");
      writer.println("The second line");
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }



  @Test
  public void testEncryptAndDecriptOneFile(){
    String fileName = "test1";
    String cipherName = "testCipher";
    String decryptedFileName = "testDecrypted";

    File file = new File(fileName);
    initOneFile(file);

    HashPassword hashArgon = new HashPassword();
    String password = "passwordTestVeryComplicated_AndCryptoIsRigolo12345";

    byte[] Bytekey = hashArgon.argon2Hash(password);
    SecretKey key = new SecretKeySpec(Bytekey, 0, Bytekey.length, "AES");
    AesCBC aesCBC = new AesCBC(key);

    byte[] salt = hashArgon.getSalt();
    IvParameterSpec iv = aesCBC.getIv();

    //FileConfigManagement fileConfigManagement = new FileConfigManagement("confFile.json");
    //fileConfigManagement.writeConfigToFile(hashArgon.getSalt(), aesCBC.getIv());
    //fileConfigManagement.readConfigToFile();

    try {
      aesCBC.encrypt(file.getPath(), cipherName);
    } catch (InvalidAlgorithmParameterException | InvalidKeyException | FileNotFoundException e) {
      logger.error(e.getMessage());
    }
    File testCipher = new File(cipherName);
    assert(testCipher.exists());

    try {
      aesCBC.decrypt(cipherName, decryptedFileName);
    } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
      logger.error(e.getMessage());
    }
  }
}
