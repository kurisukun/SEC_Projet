import ConfigFile.FileConfigManagement;
import Crypto.AesCBC;
import Crypto.HashPassword;
import Entity.JsonConfigFile;
import Validation.PasswordValidation;
import YubikeyVerification.YubikeyVerification;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.Scanner;
import javax.crypto.spec.SecretKeySpec;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "ango", version = "ango 0.1",
    description = "try to encrypt and decrypt your usb key")
public class Main implements Runnable {

  private static final Logger logger = LogManager.getLogger(Main.class);

  @Option(names = "--srcPath", description = "file to encrypt or decrypt")
  private String srcPathName;

  @Option(names = "--dstPath", description = "Destination of result")
  private String dstPathName;

  @Option(names = "--confFile", description = "Config file for decryption")
  private String configFile;

  @Option(names = "-p", description = "Passord to encrypt or decrypt file")
  private String password;

  @Option(names = "-e", description = "Encrypt file")
  private boolean encrypt;

  @Option(names = "-d", description = "Decrypt file")
  private boolean decrypt;

  @Option(names = "--help", usageHelp = true, description = "display this help and exit")
  private boolean help;

  public void run() {
    // verify that the path was given
    if (srcPathName == null || dstPathName == null || password == null || configFile == null) {
      logger.error("file can't be null");
      return;
    }

    // verify yubikey
    YubikeyVerification v = new YubikeyVerification();
    System.out.println("To use ango, you have to authenticate");
    System.out.println("Please put your finger on your YubiKey to enter your One-time password");
    Scanner scan = new Scanner(System.in);
    String otp = scan.next();
    try {
      if (v.verify(otp)) {
      // verify that the given file to process exists
      if (fileExists(srcPathName)) {
        logger.error("File doesn't exist" + srcPathName);
        return;
      }

      // verify that the given config file exists
      if (fileExists(srcPathName)) {
        logger.error("File doesn't exist" + srcPathName);
        return;
      }

      // validate the password
      if (!new PasswordValidation().validatePasword(password)) {
        logger.warn("Use of weak password :" + password);
        return;
      }

      if (encrypt) {
        try {
          // transform password into key
          logger.trace("Hash argon2");
          HashPassword hashArgon = new HashPassword();
          byte[] Bytekey = hashArgon.argon2Hash(password);
          AesCBC aesCBC = new AesCBC(new SecretKeySpec(Bytekey, 0, Bytekey.length, "AES"));

          // write info for further actions
          new FileConfigManagement(configFile).writeConfigToFile(hashArgon.getSalt(), aesCBC.getIv());

          logger.trace("Encryption");
          aesCBC.encrypt(srcPathName, dstPathName);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | FileNotFoundException e) {
          logger.trace("Decryption");
          logger.error(e.getMessage());
        }
      } else if (decrypt) {
        try {
          JsonConfigFile jsonConfigFile = new FileConfigManagement(configFile).readConfigToFile();
          // transform password into key
          logger.trace("Hash argon2");
          HashPassword hashArgon = new HashPassword(jsonConfigFile.getSalt());
          byte[] Bytekey = hashArgon.argon2Hash(password);
          AesCBC aesCBC = new AesCBC(new SecretKeySpec(Bytekey, 0, Bytekey.length, "AES"), jsonConfigFile.getIv());

          aesCBC.decrypt(srcPathName, dstPathName);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
          logger.error(e.getMessage());
        }
      }
       }
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  public static void main(String[] args) {
    int exitCode = new CommandLine(new Main()).execute(args);
    System.exit(exitCode);
  }

  private static boolean fileExists(String pathName) {
    File file = new File(pathName);
    return !file.exists();
  }
}


