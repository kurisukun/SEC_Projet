import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import Zip.ZipMaker;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;

public class TestZipFile {
  private static final Logger logger = LogManager.getLogger(TestZipFile.class);
  @Test
  public void testZipUnzipFile() {
    ZipMaker z = new ZipMaker();
    try {
      z.zip("TestFile", "compressed.zip");
    } catch (IOException e) {
      logger.error(e.getMessage());
    }

    try {
      z.unzip("compressed.zip", "uncompressed");
    } catch (IOException e) {
      logger.error(e.getMessage());
    }

    try {
      FileInputStream inputStream = new FileInputStream("TestFile");
      byte[] bufferSrc = inputStream.readAllBytes();

      inputStream = new FileInputStream("uncompressed/TestFile");
      byte[] bufferUnzip = inputStream.readAllBytes();
      assertArrayEquals(bufferSrc, bufferUnzip);
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }
}
