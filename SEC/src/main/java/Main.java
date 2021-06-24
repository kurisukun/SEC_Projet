import java.io.*;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.Scanner;

import ConfigFile.FileConfigManagement;
import Crypto.AesCBC;
import Crypto.HashPassword;
import Validation.PasswordValidation;
import YubikeyVerification.YubikeyVerification;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.io.FileUtils;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import picocli.CommandLine.Command;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Command(name = "ango", mixinStandardHelpOptions = true, version = "ango 0.1",
    description = "try to encrypt and decrypt your usb key")
public class Main {

  public static void main(String[] args) {
    Logger logger = LogManager.getLogger(Main.class);

    logger.trace("main");



    System.out.println("### Welcome in the menu of KeyDestroyer ### \n");

    System.out.print("Put your finger on the YubiKey to authentify: ");
    Scanner scan = new Scanner(System.in);
    //String otp = scan.next();


    YubikeyVerification v = new YubikeyVerification();

    //fileConfigManagement.readConfigToFile();

      //if (v.verify(otp)) {
        System.out.println("You are authentified");
      String[] list = {"testDirectory/test1", "TestDirectory/test2", "yolo", "testDirectory/test2", "testDirectory/subTestDirectory/subtest1", "testDirectory", "testDirectory/subTestDirectory"};
      for(String s: list) {
        File f = new File(s);
        System.out.println(s + " " + f.exists());
      }

      try {
        ZipMaker z = new ZipMaker();
        z.zipFiles(list, "listOfFiles.zip");

        z.zipFile("test", "notWorking");
        z.zipFile("testDirectory/subTestDirectory/subtest2", "singleFile.zip");

        z.zipFile("testDirectory", "directory.zip");

        z.unzip("singleFile.zip", "unzippedSingleFile");
        z.unzip("listOfFiles.zip", "unzippedListOfFiles");
      }
      catch (FileNotFoundException fileNotFoundException) {
        fileNotFoundException.printStackTrace();
      }
      catch (IOException ioException) {
        ioException.printStackTrace();
      }

      /*
        final String pathName = "testDirectory";
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


         */


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


