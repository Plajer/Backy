package pl.plajer.backy.stages;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import pl.plajer.backy.BackyLogger;

/**
 * @author Plajer
 * <p>
 * Created at 11.06.2019
 */
public class CleanupTask {

  public CleanupTask() {
    cleanupBackupsFolder();
  }

  private void cleanupBackupsFolder() {
    BackyLogger.log("STAGE 3 | Backups folder cleanup");
    try {
      FileUtils.deleteDirectory(new File("backups"));
      new File("backups").mkdir();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

}
