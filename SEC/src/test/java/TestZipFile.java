import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import Zip.ZipMaker;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class TestZipFile {

  @Test
  public void testZipUnzipFile() {
    ZipMaker z = new ZipMaker();
    try {
      z.zip("TestFile", "compressed.zip");
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      z.unzip("compressed.zip", "uncompressed");
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      FileInputStream inputStream = new FileInputStream("TestFile");
      byte[] bufferSrc = inputStream.readAllBytes();

      inputStream = new FileInputStream("uncompressed/TestFile");
      byte[] bufferUnzip = inputStream.readAllBytes();
      assertArrayEquals(bufferSrc, bufferUnzip);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
