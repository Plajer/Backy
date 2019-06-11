package pl.plajer.backy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author Plajer
 * <p>
 * Created at 11.06.2019
 */
public class BackyLogger {

  private static File logsFile;
  private static Logger logger = Logger.getLogger("Backy");

  static {
    try {
      File file = new File("logs.txt");
      file.createNewFile();
      logsFile = file;

      ConsoleHandler handler = new ConsoleHandler();
      handler.setFormatter(new Formatter() {
        @Override
        public String format(LogRecord record) {
          return record.getLevel() + ": " + BackyLogger.format(record.getMessage(), record.getParameters()) + "\n";
        }
      });
      logger.addHandler(handler);
      logger.setLevel(Level.ALL);
      logger.setUseParentHandlers(false);

      //used to write separator for logs.txt
      log("");
      log(" * NEW INSTANCE STARTED * ");
      log("");
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  private BackyLogger() {
  }

  public static void log(String message, Object... params) {
    try {
      String currentTime = getCurrentTime();
      logger.log(Level.INFO, currentTime.concat(message), params);
      Files.write(logsFile.toPath(), ("\n" + format(currentTime.concat(message), params)).getBytes(), StandardOpenOption.APPEND);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  private static String getCurrentTime() {
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
    Date today = new Date();
    String output = formatter.format(today);
    return "[" + output + "] ";
  }

  private static String format(String message, Object... params) {
    MessageFormat format = new MessageFormat(message);
    return format.format(params);
  }

}
