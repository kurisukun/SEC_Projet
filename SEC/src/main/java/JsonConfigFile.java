import javax.crypto.spec.IvParameterSpec;

public class JsonConfigFile {
  private byte[] salt;
  private IvParameterSpec iv;

  public JsonConfigFile(byte[] salt, IvParameterSpec iv) {
    this.salt = salt;
    this.iv = iv;
  }
}
