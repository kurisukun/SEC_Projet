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

  public void writeConfigToFile(byte[] salt, IvParameterSpec iv) {
    try {
      JsonConfigFile confFile = new JsonConfigFile(salt, iv);
      Gson gson = new Gson();
      Writer writer = Files.newBufferedWriter(Paths.get(fileName));
      gson.toJson(confFile, writer);
      writer.close();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  public JsonConfigFile readConfigToFile() {
    JsonConfigFile confFile = null;
    try {
      Gson gson = new Gson();
      Reader reader = Files.newBufferedReader(Paths.get(fileName));
      confFile = gson.fromJson(reader, JsonConfigFile.class);
      reader.close();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    return confFile;
  }
}
