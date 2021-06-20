import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import org.apache.log4j.Logger;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

public class HashPassword {
  private static Logger logger;

  public HashPassword(Logger logger) {
    HashPassword.logger = logger;
  }

  public byte[] argon2Hash(String password, byte[] salt) {
    logger.debug("Génération du hash argon2");
    Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
        .withVersion(Argon2Parameters.ARGON2_VERSION_13) // 19
        .withIterations(4)
        .withMemoryAsKB(64 * 1024)
        .withParallelism(4)
        .withSalt(salt);
    Argon2BytesGenerator gen = new Argon2BytesGenerator();
    gen.init(builder.build());
    byte[] key = new byte[32];
    gen.generateBytes(password.getBytes(StandardCharsets.UTF_8), key, 0, key.length);
    return key;
  }

  public byte[] generateSalt16Byte() {
    logger.debug("Génération du sel");
    SecureRandom secureRandom = new SecureRandom();
    byte[] salt = new byte[16];
    secureRandom.nextBytes(salt);
    return salt;
  }
}
