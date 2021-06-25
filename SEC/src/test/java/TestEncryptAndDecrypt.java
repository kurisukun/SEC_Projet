import Crypto.AesCBC;
import Crypto.HashPassword;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;


public class TestEncryptAndDecrypt {

  private static final Logger logger = LogManager.getLogger(TestEncryptAndDecrypt.class);

  private void initOneFile(String fileName, String[] content) {
    File file = new File(fileName);
    try {
      if (!file.exists()) {
        file.createNewFile();
      }
      PrintWriter writer = new PrintWriter(fileName, StandardCharsets.UTF_8);
      for (String s : content) {
        writer.println(s);
      }
      writer.close();
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }

  private void initFolderWithFiles(String folderName, String[] content) {
    new File(folderName).mkdirs();
    for (String s : content) {
      String[] contentFile = {"first line", "second line", "third line"};
      initOneFile(folderName + "/" + s, contentFile);
    }
  }

  @Test
  public void testEncryptAndDecriptOneFile() {
    String fileName = "test1";
    String cipherFileName = "testCipher";
    String decryptedFileName = "testDecrypted";

    String[] content = {"first line", "second line", "third line"};
    initOneFile(fileName, content);

    HashPassword hashArgon = new HashPassword();
    String password = "passwordTestVeryComplicated_AndCryptoIsRigolo12345";

    byte[] Bytekey = hashArgon.argon2Hash(password);
    AesCBC aesCBC = new AesCBC(new SecretKeySpec(Bytekey, 0, Bytekey.length, "AES"));

    try {
      aesCBC.encrypt(fileName, cipherFileName);
      File testCipher = new File(cipherFileName);
      assert (testCipher.exists());

      aesCBC.decrypt(cipherFileName, decryptedFileName);

      File decryptedFile = new File(decryptedFileName + "/" + fileName);
      assert (decryptedFile.exists());

      assert (FileUtils.contentEquals(new File(fileName), decryptedFile));
    } catch (InvalidAlgorithmParameterException | InvalidKeyException | IOException e) {
      logger.error(e.getMessage());
    }
  }

  @Test
  public void testEncryptAndDecryptFolder() {
    String folderName = "testFolder";
    String cipherFolderName = "testCipher";
    String decryptedFolderName = "testDecrypted";

    String[] content = {"test1", "test2", "test3"};
    initFolderWithFiles(folderName, content);

    HashPassword hashArgon = new HashPassword();
    String password = "passwordTestVeryComplicated_AndCryptoIsRigolo12345";

    byte[] Bytekey = hashArgon.argon2Hash(password);
    SecretKey key = new SecretKeySpec(Bytekey, 0, Bytekey.length, "AES");
    AesCBC aesCBC = new AesCBC(key);

    try {
      aesCBC.encrypt(folderName, cipherFolderName);
      File testCipher = new File(cipherFolderName);
      assert (testCipher.exists());

      aesCBC.decrypt(cipherFolderName, decryptedFolderName);

      File decryptedFolder = new File(decryptedFolderName);
      assert (decryptedFolder.exists());

      for (String s : content) {
        String suffix = folderName + "/" + s;
        assert (FileUtils.contentEquals(new File(suffix), new File(decryptedFolderName + "/" + suffix)));
      }
    } catch (InvalidAlgorithmParameterException | InvalidKeyException | IOException e) {
      logger.error(e.getMessage());
    }
  }
}
