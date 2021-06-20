package Entity;

import javax.crypto.spec.IvParameterSpec;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor()
public class JsonConfigFile {
  private byte[] salt;
  private IvParameterSpec iv;
}
