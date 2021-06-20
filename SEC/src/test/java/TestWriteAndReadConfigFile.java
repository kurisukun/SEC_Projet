import ConfigFile.FileConfigManagement;
import Crypto.AesCBC;
import Crypto.HashPassword;
import Entity.JsonConfigFile;
import java.io.File;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class TestWriteAndReadConfigFile {

  @Test
  public void testWriteRead(){
    HashPassword hashArgon = new HashPassword();
    String password = "secret password";
    byte[] Bytekey = hashArgon.argon2Hash(password);
    String fileName = "confFile.json";
    SecretKey key = new SecretKeySpec(Bytekey, 0, Bytekey.length, "AES");
    AesCBC aesCBC = new AesCBC(key);
    FileConfigManagement fileConfigManagement = new FileConfigManagement(fileName);
    File file = new File(fileName);

    fileConfigManagement.writeConfigToFile(hashArgon.getSalt(), aesCBC.getIv());
    JsonConfigFile jsonConfigFile = fileConfigManagement.readConfigToFile();

    assertArrayEquals( hashArgon.getSalt(), jsonConfigFile.getSalt());
    file.delete();
  }
}
