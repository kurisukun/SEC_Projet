import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipMaker {

  private void zipFileProcess(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException{
    FileInputStream fis = new FileInputStream(fileToZip);
    ZipEntry zipEntry = new ZipEntry(fileName);
    zipOut.putNextEntry(zipEntry);
    byte[] bytes = new byte[1024];
    int length;
    while((length = fis.read(bytes)) >= 0) {
      zipOut.write(bytes, 0, length);
    }
    fis.close();
  }

  private void zipFolderProcess(File fileToZip, String fileName, ZipOutputStream zipOut) throws FileNotFoundException, IOException{
    if (fileToZip.isHidden()) {
      return;
    }
    if (fileToZip.isDirectory()) {
      if (fileName.endsWith("/")) {
        zipOut.putNextEntry(new ZipEntry(fileName));
      } else {
        zipOut.putNextEntry(new ZipEntry(fileName + "/"));
      }
      zipOut.closeEntry();
      File[] children = fileToZip.listFiles();
      for (File childFile : children) {
        zipFolderProcess(childFile, fileName + "/" + childFile.getName(), zipOut);
      }
      return;
    }

    zipFileProcess(fileToZip, fileName, zipOut);
  }

  public void zipFile(String fileName, String zipName) throws FileNotFoundException, IOException{
    File fileToZip = new File(fileName);
    if (fileToZip.exists()) {
    FileOutputStream fos = new FileOutputStream(zipName);
    ZipOutputStream zipOut = new ZipOutputStream(fos);
      if(fileToZip.isDirectory()){
        zipFolderProcess(fileToZip, fileToZip.getName(), zipOut);
      }
      else {
        zipFileProcess(fileToZip, fileToZip.getName(), zipOut);
      }
      zipOut.close();
      fos.close();
    }
  }


  public void zipFiles(String[] fileList, String zipName) throws FileNotFoundException, IOException{
    List<File> srcFiles = new ArrayList<File>();
    for(String fileName: fileList){
      File f = new File(fileName);
      if(f.exists()){
        srcFiles.add(f);
      }
    }

    if (!srcFiles.isEmpty()) {
      FileOutputStream fos = new FileOutputStream(zipName);
      ZipOutputStream zipOut = new ZipOutputStream(fos);
      for (File fileToZip : srcFiles) {

        if (fileToZip.isDirectory()) {
          zipFolderProcess(fileToZip, fileToZip.getName(), zipOut);
        } else {
          zipFileProcess(fileToZip, fileToZip.getName(), zipOut);
        }
      }
      zipOut.close();
      fos.close();
    }
  }



  /*
  public void zip(){
    try {
      String sourceFile = "test1.txt";
      FileOutputStream fos = new FileOutputStream("compressed.zip");
      ZipOutputStream zipOut = new ZipOutputStream(fos);
      File fileToZip = new File(sourceFile);
      FileInputStream fis = new FileInputStream(fileToZip);
      ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
      zipOut.putNextEntry(zipEntry);
      byte[] bytes = new byte[1024];
      int length;
      while((length = fis.read(bytes)) >= 0) {
        zipOut.write(bytes, 0, length);
      }
      zipOut.close();
      fis.close();
      fos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  */


  public void unzip(){
    try {
      String fileZip = "src/main/resources/unzipTest/compressed.zip";
      File destDir = new File("src/main/resources/unzipTest");
      byte[] buffer = new byte[1024];
      ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
      ZipEntry zipEntry = zis.getNextEntry();
      while (zipEntry != null) {
        File newFile = newFile(destDir, zipEntry);
        if (zipEntry.isDirectory()) {
          if (!newFile.isDirectory() && !newFile.mkdirs()) {
            throw new IOException("Failed to create directory " + newFile);
          }
        } else {
          // fix for Windows-created archives
          File parent = newFile.getParentFile();
          if (!parent.isDirectory() && !parent.mkdirs()) {
            throw new IOException("Failed to create directory " + parent);
          }

          // write file content
          FileOutputStream fos = new FileOutputStream(newFile);
          int len;
          while ((len = zis.read(buffer)) > 0) {
            fos.write(buffer, 0, len);
          }
          fos.close();
        }
        zipEntry = zis.getNextEntry();
      }
      zis.closeEntry();
      zis.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
    File destFile = new File(destinationDir, zipEntry.getName());

    String destDirPath = destinationDir.getCanonicalPath();
    String destFilePath = destFile.getCanonicalPath();

    if (!destFilePath.startsWith(destDirPath + File.separator)) {
      throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
    }

    return destFile;
  }
}
