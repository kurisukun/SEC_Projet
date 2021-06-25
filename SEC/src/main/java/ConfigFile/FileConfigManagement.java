package ConfigFile;

import Entity.JsonConfigFile;
import com.google.gson.Gson;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.crypto.spec.IvParameterSpec;
import lombok.AllArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

@AllArgsConstructor()
public class FileConfigManagement {

  private final Logger logger = LogManager.getLogger(FileConfigManagement.class);
  private final String fileName;

  /**
   * take crypto parameters and write it in file
   *
   * @param salt for argon2
   * @param iv   IV for aes-cbc
   */
  public void writeConfigToFile(byte[] salt, IvParameterSpec iv) {
    try {
      Writer writer = Files.newBufferedWriter(Paths.get(fileName));
      new Gson().toJson(new JsonConfigFile(salt, iv), writer);
      writer.close();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  /**
   * read Salt and IV from file
   *
   * @return crypto parameters
   */
  public JsonConfigFile readConfigToFile() {
    JsonConfigFile confFile = null;
    try {
      Reader reader = Files.newBufferedReader(Paths.get(fileName));
      confFile = new Gson().fromJson(reader, JsonConfigFile.class);
      reader.close();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    return confFile;
  }
}
