import com.goterl.lazysodium.LazySodiumJava;
import com.goterl.lazysodium.SodiumJava;
import com.goterl.lazysodium.interfaces.Stream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "keyDestroyer", mixinStandardHelpOptions = true, version = "keyDestroyer 0.0",
    description = "try to encrypt and decrypt your usb key")
public class Sec {
  
  public static void main(String[] args) {
    int exitCode = new CommandLine(new Sec()).execute(args);

    LazySodiumJava lazySodium = new LazySodiumJava(new SodiumJava());
    byte[] nonce = {-87, -123, -26, 59, -12, 123, -62, -64};//lazySodium.nonce(Stream.CHACHA20_NONCEBYTES);
    byte[] key = "RANDOM_KEY_OF_32_BYTES_LENGTH121RANDOM_KEY_OF_32_BYTES_LENGTH121".getBytes();

    String path = "/dev/sda1";
    File file = new File(path);
    if (!file.exists()) {
      System.out.println("Le fichier n'existe pas!");
      return;
    }

    byte[] cipher = null;

    System.out.println("Lecture du fichier et Chiffrement du contenu");
    cipher = readFromDisk(lazySodium, nonce, key, path, cipher);

    System.out.println("Écriture du fichier chiffé");
    writeOnDisk(path, cipher);

    System.out.println("Lecture du fichier chiffré et Déchiffrement du contenu");
    cipher = readFromDisk(lazySodium, nonce, key, path, cipher);

    System.out.println("Écriture du fichier déchiffré");
    writeOnDisk(path, cipher);
  }

  private static byte[] readFromDisk(LazySodiumJava lazySodium, byte[] nonce, byte[] key, String path, byte[] cipher) {
    byte[] data;
    try {
      data = Files.readAllBytes(Paths.get(path));
      cipher = new byte[data.length];
      lazySodium.cryptoStreamChaCha20Xor(cipher, data, data.length, nonce, key);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return cipher;
  }

  private static void writeOnDisk(String path, byte[] cipher) {
    try {
      assert cipher != null;
      Files.write(Paths.get(path), cipher);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
