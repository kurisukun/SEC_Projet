import com.goterl.lazysodium.LazySodiumJava;
import com.goterl.lazysodium.SodiumJava;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.lf5.util.Resource;
import picocli.CommandLine.Command;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

@Command(name = "keyDestroyer", mixinStandardHelpOptions = true, version = "keyDestroyer 0.0",
    description = "try to encrypt and decrypt your usb key")
public class Sec {
  private static final Logger logger = LogManager.getLogger(Sec.class);

  public static void main(String[] args) {
    logger.debug("Début du Main");
    String password = "secret password";
    byte[] salt = {-107, -56, 76, -102, 66, 2, -35, 91, -125, -86, 16, 87, 29, 54, 4, -87};//generateSalt16Byte();
    byte[] key = argon2Hash(password, salt);

    //int exitCode = new CommandLine(new Sec()).execute(args);

    LazySodiumJava lazySodium = new LazySodiumJava(new SodiumJava());
    byte[] nonce = {-87, -123, -26, 59, -12, 123, -62, -64};//lazySodium.nonce(Stream.CHACHA20_NONCEBYTES);

//    String path = "/dev/sda1";
//    File file = new File(path);
//    if (!file.exists()) {
//      System.out.println("Le fichier n'existe pas!");
//      return;
//    }
//
//    byte[] cipher = null;
//
//    System.out.println("Lecture du fichier et Chiffrement du contenu");
//    cipher = readFromDisk(lazySodium, nonce, key, path, cipher);
//
//    System.out.println("Écriture du fichier chiffé");
//    writeOnDisk(path, cipher);
//
//    System.out.println("Lecture du fichier chiffré et Déchiffrement du contenu");
//    cipher = readFromDisk(lazySodium, nonce, key, path, cipher);
//
//    System.out.println("Écriture du fichier déchiffré");
//    writeOnDisk(path, cipher);
  }

  private static byte[] argon2Hash(String password, byte[] salt) {
    logger.debug("Génération du hash argon2");
    Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
        .withVersion(Argon2Parameters.ARGON2_VERSION_13) // 19
        .withIterations(8)
        .withMemoryAsKB(128 * 1024)
        .withParallelism(8)
        .withSalt(salt);
    Argon2BytesGenerator gen = new Argon2BytesGenerator();
    gen.init(builder.build());
    byte[] key = new byte[64];
    gen.generateBytes(password.getBytes(StandardCharsets.UTF_8), key, 0, key.length);
    return key;
  }

  private static byte[] generateSalt16Byte() {
    logger.debug("Génération du sel");
    SecureRandom secureRandom = new SecureRandom();
    byte[] salt = new byte[16];
    secureRandom.nextBytes(salt);
    return salt;
  }
}
