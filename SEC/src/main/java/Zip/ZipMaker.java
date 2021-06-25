package Zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipMaker {

  private void zipFileProcess(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
    FileInputStream fis = new FileInputStream(fileToZip);
    ZipEntry zipEntry = new ZipEntry(fileName);
    zipOut.putNextEntry(zipEntry);
    byte[] bytes = new byte[1024];
    int length;
    while ((length = fis.read(bytes)) >= 0) {
      zipOut.write(bytes, 0, length);
    }
    fis.close();
  }

  private void zipFolderProcess(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
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

  /**
   * Zip the given file/folder using his filename
   * @param fileName the filename
   * @param zipName the name of the zip archive
   * @throws IOException
   */
  public void zip(String fileName, String zipName) throws IOException {
    File fileToZip = new File(fileName);
    if (fileToZip.exists()) {
      FileOutputStream fos = new FileOutputStream(zipName);
      ZipOutputStream zipOut = new ZipOutputStream(fos);
      if (fileToZip.isDirectory()) {
        zipFolderProcess(fileToZip, fileToZip.getName(), zipOut);
      } else {
        zipFileProcess(fileToZip, fileToZip.getName(), zipOut);
      }
      zipOut.close();
      fos.close();
    }else{
      throw new FileNotFoundException();
    }
  }

  /**
   * Zip the list of given files
   * @param fileList the list containing all the filenames
   * @param zipName the name of the zip archive
   * @throws IOException
   */
  public void zipFiles(String[] fileList, String zipName) throws IOException {
    List<File> srcFiles = new ArrayList<>();
    for (String fileName : fileList) {
      File f = new File(fileName);
      if (f.exists()) {
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

  /**
   * Unzip the given zip archive into a folder. The content of the newly created folder depends on what the archive
   * contains. The hierarchy of the folder is kept
   * @param zipName
   * @param folderName
   * @throws IOException
   */
  public void unzip(String zipName, String folderName) throws IOException {

    File destDir = new File(folderName);
    byte[] buffer = new byte[1024];
    ZipInputStream zis = new ZipInputStream(new FileInputStream(zipName));
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
  }

  /**
   * Used to prevent zip slip vulnerability which permits an adversary to write files to the file system outside the target folder
   * @param destinationDir the name of the destination folder
   * @param zipEntry the entry to be unzipped
   * @return the file which could have been unzipped
   * @throws IOException
   */
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
