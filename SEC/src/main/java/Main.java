import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.Scanner;

import ConfigFile.FileConfigManagement;
import Crypto.AesCBC;
import Crypto.HashPassword;
import Validation.PasswordValidation;
import YubikeyVerification.YubikeyVerification;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import picocli.CommandLine.Command;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Command(name = "Ango", mixinStandardHelpOptions = true, version = "Ango 0.1",
    description = "try to encrypt and decrypt your usb key")
public class Main {

  public static void main(String[] args) {
    Logger logger = LogManager.getLogger(Main.class);
    logger.trace("main");

    System.out.println("### Welcome in the menu of KeyDestroyer ### \n");

    System.out.print("Put your finger on the YubiKey to authentify: ");
    Scanner scan = new Scanner(System.in);
    String otp = scan.next();

    YubikeyVerification v = new YubikeyVerification();

    try {
      if (v.verify(otp)) {
        System.out.println("You are authentified");

        /*
        final String pathName = "/dev/sdb3";
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

        logger.info("Chiffrement des données");
        try {
          aesCBC.setDecryptMode("test.xlsx", "cipher");
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
          e.printStackTrace();
        }*/

      }
    }
    catch (Exception e){
      logger.error(e);
    }


    //logger.info("Déchiffrement des données");
  }

  private void copyData(String srcPath, String dstPath, Logger logger) {
    try (FileInputStream inputStream = new FileInputStream(srcPath);
        FileOutputStream outputStream = new FileOutputStream(dstPath)) {

      int bytesRead;
      int offset = 0;
      byte[] buffer = new byte[1024 * 1024 * 1024]; // lecture de 1Go
      while ((bytesRead = inputStream.read(buffer, offset, buffer.length)) != -1) {
        outputStream.write(buffer);
      }
      inputStream.close();
      outputStream.close();
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
