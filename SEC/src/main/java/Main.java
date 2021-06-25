import ConfigFile.FileConfigManagement;
import Crypto.AesCBC;
import Crypto.HashPassword;
import Validation.PasswordValidation;
import YubikeyVerification.YubikeyVerification;
import Zip.ZipMaker;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.Scanner;
import javax.crypto.SecretKey;
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

  @Option(names = "-f", description = "file to encrypt ou decrypt")
  private String pathName;

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
    if (pathName == null) {
      logger.error("file can't be null");
      return;
    }

    // verify yubikey
    YubikeyVerification v = new YubikeyVerification();
    Scanner scan = new Scanner(System.in);
    String otp = scan.next();
    try {
      if (v.verify(otp)) {
        // verify that the given file exist
        if (fileExists(logger, pathName)) {
          logger.error("file don't exist");
          return;
        }

        // validate the password
        if (!new PasswordValidation().validatePasword(password)) {
          logger.warn("Tentative de mot de passe faible :" + password);
          return;
        }

        // transform password into key
        HashPassword hashArgon = new HashPassword();
        byte[] Bytekey = new HashPassword().argon2Hash(password);
        AesCBC aesCBC = new AesCBC(new SecretKeySpec(Bytekey, 0, Bytekey.length, "AES"));

        // write info for further actions
        new FileConfigManagement("confFile.json").writeConfigToFile(hashArgon.getSalt(), aesCBC.getIv());

        if (encrypt) {
          System.out.println("encrypt");
          try {
            aesCBC.encrypt(pathName, "cipher");
          } catch (InvalidAlgorithmParameterException | InvalidKeyException | FileNotFoundException e) {
            logger.error(e.getMessage());
          }
        } else if (decrypt) {
          System.out.println("decrypt");
          try {
            aesCBC.decrypt(pathName, "unencrypted");
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

    logger.trace("main");

    System.out.println("### Welcome in the menu of KeyDestroyer ### \n");

    System.out.print("Put your finger on the YubiKey to authentify: ");
    Scanner scan = new Scanner(System.in);
    //String otp = scan.next();

    YubikeyVerification v = new YubikeyVerification();

    //fileConfigManagement.readConfigToFile();

    //if (v.verify(otp)) {
    System.out.println("You are authentified");
    String[] list = {"testDirectory/test1", "TestDirectory/test2", "yolo", "testDirectory/test2",
        "testDirectory/subTestDirectory/subtest1", "testDirectory", "testDirectory/subTestDirectory"};
    for (String s : list) {
      File f = new File(s);
      System.out.println(s + " " + f.exists());
    }

    try {
      ZipMaker z = new ZipMaker();
      z.zipFiles(list, "listOfFiles.zip");

      z.zip("test", "notWorking");
      z.zip("testDirectory/subTestDirectory/subtest2", "singleFile.zip");

      z.zip("testDirectory", "directory.zip");

      z.unzip("singleFile.zip", "unzippedSingleFile");
      z.unzip("listOfFiles.zip", "unzippedListOfFiles");
    } catch (IOException e) {
      logger.error(e);
    }

    final String pathName = "testDirectory";
    HashPassword hashArgon = new HashPassword();
    FileConfigManagement fileConfigManagement = new FileConfigManagement("confFile.json");
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
    AesCBC aesCBC = new AesCBC(key);

    fileConfigManagement.writeConfigToFile(hashArgon.getSalt(), aesCBC.getIv());
    //fileConfigManagement.readConfigToFile();

    logger.info("Chiffrement des données");
    try {
      aesCBC.encrypt(pathName, "cipher");
    } catch (InvalidAlgorithmParameterException | InvalidKeyException | FileNotFoundException e) {
      logger.error(e.getMessage());
    }

    try {
      aesCBC.decrypt("cipher", "unencrypted");
    } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
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


