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

  private static Logger logger = LogManager.getLogger(Main.class);

  @Option(names = "-f", description = "file to encrypt ou decrypt")
  String file;

  @Option(names = "-e", description = "Encrypt file")
  boolean encrypt;

  @Option(names = "-d", description = "Decrypt file")
  boolean decrypt;

  @Option(names = "--help", usageHelp = true, description = "display this help and exit")
  private boolean help = false;

  public void run() {
    if (file == null) {
      logger.error("file can't be null");
      return;
    }

    if (encrypt) {
      System.out.println("encrypt");
    } else if (decrypt) {
      System.out.println("decrypt");
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
    //int exitCode = new CommandLine(new Sec()).execute(args);
    AesCBC aesCBC = new AesCBC(key);

    fileConfigManagement.writeConfigToFile(hashArgon.getSalt(), aesCBC.getIv());
    //fileConfigManagement.readConfigToFile();

    logger.info("Chiffrement des données");
    try {
      aesCBC.encrypt(pathName, "cipher");
    } catch (InvalidAlgorithmParameterException | InvalidKeyException | FileNotFoundException e) {
      e.printStackTrace();
    }

    try {
      aesCBC.decrypt("cipher", "unencrypted");
    } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
      return;
    }
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


