package Crypto;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import lombok.Data;
import lombok.Getter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

@Data
public class HashPassword {

  private Logger logger = LogManager.getLogger(HashPassword.class);
  @Getter
  private byte[] salt;

  public HashPassword() {
    this.salt = generateSalt16Byte();
  }
  public HashPassword(byte[] salt) {
    this.salt = salt;
  }

  /**
   * generate argon2 hash of a password
   * @param password password to hash
   * @return hash
   */
  public byte[] argon2Hash(String password) {
    logger.trace("Génération du hash argon2");
    Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
        .withVersion(Argon2Parameters.ARGON2_VERSION_13) // 19
        .withIterations(3) // environ 2 secondes
        .withMemoryAsKB(128 * 1024)
        .withParallelism(4)
        .withSalt(salt);
    Argon2BytesGenerator gen = new Argon2BytesGenerator();
    gen.init(builder.build());
    byte[] key = new byte[32];
    gen.generateBytes(password.getBytes(StandardCharsets.UTF_8), key, 0, key.length);
    return key;
  }

  /**
   * generate salt for argon2id
   * @return salt
   */
  public byte[] generateSalt16Byte() {
    logger.trace("Génération du sel");
    byte[] salt = new byte[16];
    new SecureRandom().nextBytes(salt);
    return salt;
  }
}
