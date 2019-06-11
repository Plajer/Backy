package pl.plajer.backy;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;

import pl.plajer.backy.gdrive.GoogleDriveUtils;
import pl.plajer.backy.stages.CleanupTask;
import pl.plajer.backy.stages.FileZipperTask;
import pl.plajer.backy.stages.ScriptManager;

/**
 * @author Plajer
 * <p>
 * Created at 11.06.2019
 */
public class Backy {

  private JsonObject config;
  private File fileMetadata;
  private FileContent mediaContent;

  public void start() {
    BackyLogger.log("Backy instance started!");

    try {
      setupConfig();
    } catch (IOException ex) {
      ex.printStackTrace();
      return;
    }

    if (!prepareScriptsFirstStage()) {
      BackyLogger.log("Stage 1 failed to execute, cancelling...");
      return;
    }
    if (!prepareBackupZipSecondStage()) {
      BackyLogger.log("Stage 2 failed to execute, cancelling...");
      return;
    }
    if (!prepareGDriveUploadThirdStage()) {
      BackyLogger.log("Stage 3 failed to execute...");
    }
    prepareCleanupFourthStage();
  }

  private void setupConfig() throws IOException {
    java.io.File file = new java.io.File("config.json");
    if (!file.exists()) {
      try(InputStream in = getClass().getResourceAsStream("/config.json");
          BufferedReader br = new BufferedReader(new InputStreamReader(in));
          FileWriter wr = new FileWriter(file)) {
        String line;
        while ((line = br.readLine()) != null) {
          wr.write(line);
        }
      }
    }
    config = new Gson().fromJson(new FileReader(file), JsonObject.class);
  }

  private boolean prepareScriptsFirstStage() {
    ScriptManager scriptManager = new ScriptManager();
    if (!scriptManager.processScripts()) {
      BackyLogger.log("Failed to process scripts so we can't continue backup operation!");
      return false;
    }
    return true;
  }

  private boolean prepareBackupZipSecondStage() {
    java.io.File zip = new FileZipperTask().zipTask();

    fileMetadata = new File();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    Date today = new Date();
    fileMetadata.setName("Backy-" + formatter.format(today));
    fileMetadata.setMimeType("application/zip");
    fileMetadata.setParents(Collections.singletonList(config.get("backups-folder-id").getAsString()));
    mediaContent = new FileContent("application/zip", zip);
    return true;
  }

  private boolean prepareGDriveUploadThirdStage() {
    try {
      BackyLogger.log("STAGE 3 | Upload backup to GDrive");
      GoogleDriveUtils.getDriveService().files().create(fileMetadata, mediaContent).execute();

      deleteOlderBackupsIfNeeded();
      BackyLogger.log("Backup process completed! Shutting off...");
      return true;
    } catch (IOException ex) {
      ex.printStackTrace();
      return false;
    }
  }

  private void deleteOlderBackupsIfNeeded() throws IOException {
    FileList list = GoogleDriveUtils.getDriveService().files().list()
        .setQ("'" + config.get("backups-folder-id").getAsString() + "' in parents and trashed = false")
        .setSpaces("drive")
        .setFields("nextPageToken, files(id, name, createdTime)")
        .setPageToken(null)
        .execute();
    if (list.getFiles().size() > config.get("max-backup-files").getAsInt()) {
      List<File> files = list.getFiles();
      files.sort(Comparator.comparingLong(file -> file.getCreatedTime().getValue()));
      for (int i = 0; i < files.size() - config.get("max-backup-files").getAsInt(); i++) {
        GoogleDriveUtils.getDriveService().files().delete(files.get(i).getId()).execute();
      }
    }
  }

  private void prepareCleanupFourthStage() {
    //clean backups folder
    new CleanupTask();

    //delete backup zip, it's on GDrive now
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    Date today = new Date();
    java.io.File file = new java.io.File("Backy-" + formatter.format(today) + ".zip");
    file.delete();
  }

}
