import ConfigFile.FileConfigManagement;
import Crypto.AesCBC;
import Crypto.HashPassword;
import Validation.PasswordValidation;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import picocli.CommandLine.Command;

@Command(name = "keyDestroyer", mixinStandardHelpOptions = true, version = "keyDestroyer 0.0",
    description = "try to encrypt and decrypt your usb key")
public class Main {

  public static void main(String[] args) {
    Logger logger = LogManager.getLogger(Main.class);
    logger.debug("Début du Main");
    final String pathName = "A2.tar.gz";
    HashPassword hashArgon = new HashPassword();
    FileConfigManagement fileConfigManagement = new FileConfigManagement("confFile.json");

    logger.debug("Vérification de l'existence de la partition");
    if (fileExists(logger, pathName)) {
      return;
    }

    String password = "simple password";
    PasswordValidation passwordValidation = new PasswordValidation();
    if (!passwordValidation.validatePasword(password)) {
      logger.warn("Tentative de mot de passe faible :" + password);
    }
    byte[] Bytekey = hashArgon.argon2Hash(password);
    SecretKey key = new SecretKeySpec(Bytekey, 0, Bytekey.length, "AES");
    //int exitCode = new CommandLine(new Sec()).execute(args);
    AesCBC aesCBC = new AesCBC(key);

    fileConfigManagement.writeConfigToFile(hashArgon.getSalt(), aesCBC.getIv());
    //fileConfigManagement.readConfigToFile();

    logger.info("Chiffrement des données");
    try {
      aesCBC.setEncryptMode(pathName, "cipher");
    } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
      e.printStackTrace();
    }

    try {
      aesCBC.setDecryptMode("cipher", "unencrypted.tar.gz");
    } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
      return;
    }
    File f = new File(pathName);
    File f1 = new File("unencrypted.tar.gz");
    try {
      if (FileUtils.contentEquals(f, f1)) {
        logger.debug("fichier identique");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    //logger.debug("Copie sur le périphérique");

    //logger.debug("Supression du fichier chiffré");

    //logger.info("Déchiffrement des données");
  }

  private static void copyData(String srcPath, String dstPath, Logger logger) {
    try (FileInputStream inputStream = new FileInputStream(srcPath);
        FileOutputStream outputStream = new FileOutputStream(dstPath)) {

      byte[] buffer = new byte[1024 * 1024 * 1024]; // lecture de 1Go
      while (inputStream.read(buffer, 0, buffer.length) != -1) {
        outputStream.write(buffer);
      }
    } catch (IOException e) {
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
