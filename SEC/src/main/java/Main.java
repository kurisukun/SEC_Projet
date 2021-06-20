
import com.google.gson.Gson;
import java.io.File;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import picocli.CommandLine.Command;

@Command(name = "keyDestroyer", mixinStandardHelpOptions = true, version = "keyDestroyer 0.0",
    description = "try to encrypt and decrypt your usb key")
public class Main {

  public static void main(String[] args) {
    Logger logger = LogManager.getLogger(Main.class);
    logger.debug("Début du Main");
    final String pathName = "/dev/sdc3";
    HashPassword hashArgon = new HashPassword(logger);

    logger.debug("Vérification de l'existence de la partition");
    if (fileExists(logger, pathName)) {
      return;
    }

    String password = "secret password";
    byte[] salt = {-107, -56, 76, -102, 66, 2, -35, 91, -125, -86, 16, 87, 29, 54, 4, -87};//hashArgon.generateSalt16Byte();
    byte[] Bytekey = hashArgon.argon2Hash(password, salt);
    SecretKey key = new SecretKeySpec(Bytekey, 0, Bytekey.length, "AES");
    //int exitCode = new CommandLine(new Sec()).execute(args);
    AesCBC aesCBC = new AesCBC(key, logger);

    writeConfigToFile(logger, salt, aesCBC);

    //logger.info("Chiffrement des données");

    //logger.info("Déchiffrement des données");
  }

  private static void writeConfigToFile(Logger logger, byte[] salt, AesCBC aesCBC) {
    try {
      // create book object
      JsonConfigFile confFile = new JsonConfigFile(salt, aesCBC.getIv());

      // create Gson instance
      Gson gson = new Gson();
      // create a writer
      Writer writer = Files.newBufferedWriter(Paths.get("confFile.json"));

      // convert book object to JSON file
      gson.toJson(confFile, writer);

      // close writer
      writer.close();

    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  private static boolean fileExists(Logger logger, String pathName) {
    File file = new File(pathName);
    if (!file.exists()) {
      logger.error("Le fichier " + pathName + " n'existe pas!");
      return true;
    }
    return false;
  }
}
