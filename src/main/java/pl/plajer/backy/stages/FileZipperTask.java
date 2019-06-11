package pl.plajer.backy.stages;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import pl.plajer.backy.BackyLogger;

/**
 * @author Plajer
 * <p>
 * Created at 11.06.2019
 */
public class FileZipperTask {

  public File zipTask() {
    BackyLogger.log("STAGE 2 | Zip task");
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    Date today = new Date();
    zipDirectory(new File("backups"), new File("Backy-" + formatter.format(today)));
    BackyLogger.log("Zipped backup folder contents into Backy-" + formatter.format(today) + ".zip");
    return new File("Backy-" + formatter.format(today) + ".zip");
  }

  /**
   * Compress a directory to ZIP file including subdirectories
   *
   * @param directoryToCompress directory to zip
   * @param outputDirectory     where to place the compress file
   */
  private void zipDirectory(File directoryToCompress, File outputDirectory) {
    try {
      FileOutputStream dest = new FileOutputStream(new File(outputDirectory.getName() + ".zip"));
      ZipOutputStream zipOutputStream = new ZipOutputStream(dest);

      zipDirectoryHelper(directoryToCompress, directoryToCompress, zipOutputStream);
      zipOutputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void zipDirectoryHelper(File rootDirectory, File currentDirectory, ZipOutputStream out) throws Exception {
    byte[] data = new byte[2048];

    File[] files = currentDirectory.listFiles();
    if (files == null) {
      // no files were found or this is not a directory
      return;
    }
    for (File file : files) {
      if (file.isDirectory()) {
        zipDirectoryHelper(rootDirectory, file, out);
      } else {
        FileInputStream fi = new FileInputStream(file);
        // creating structure and avoiding duplicate file names
        String name = file.getAbsolutePath().replace(rootDirectory.getAbsolutePath(), "");

        ZipEntry entry = new ZipEntry(name);
        out.putNextEntry(entry);
        int count;
        BufferedInputStream origin = new BufferedInputStream(fi, 2048);
        while ((count = origin.read(data, 0, 2048)) != -1) {
          out.write(data, 0, count);
        }
        origin.close();
      }
    }
  }

}
