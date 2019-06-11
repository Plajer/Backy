package pl.plajer.backy.stages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;

import pl.plajer.backy.BackyLogger;

/**
 * @author Plajer
 * <p>
 * Created at 11.06.2019
 */
public class ScriptManager {

  private List<ProcessBuilder> scriptsToExecute = new ArrayList<>();

  public ScriptManager() {
    loadScripts();
  }

  private void loadScripts() {
    BackyLogger.log("STAGE 1 | Attempting to load scripts...");
    try (Stream<Path> paths = Files.walk(Paths.get("scripts"))) {
      List<File> files = paths.filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());
      for (File file : files) {
        if (!FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("sh")) {
          BackyLogger.log("Skipping invalid script {0} from loading!", file.getName());
          continue;
        }
        ProcessBuilder builder = new ProcessBuilder(file.getPath());
        scriptsToExecute.add(builder);
        BackyLogger.log("Loaded script {0}", file.getName());
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public boolean processScripts() {
    try {
      int total = scriptsToExecute.size();
      int i = 1;
      for (ProcessBuilder builder : scriptsToExecute) {
        Process process = builder.start();
        process.waitFor();
        BackyLogger.log("Processed {0} out of {1} scripts!", i, total);
        i++;
      }
      return true;
    } catch (IOException | InterruptedException ex) {
      ex.printStackTrace();
      return false;
    }
  }

}
