package pl.plajer.backy.stages;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import pl.plajer.backy.BackyLogger;
import pl.plajer.backy.discord.DiscordWebhook;

/**
 * @author Plajer
 * <p>
 * Created at 18.09.2020
 */
public class DiscordNotifyTask {

  private JsonObject config;
  private Instant startTime;
  private Map<String, Long> processTime;

  public DiscordNotifyTask(JsonObject config, Instant startTime, Map<String, Long> processTime) {
    this.config = config;
    this.startTime = startTime;
    this.processTime = processTime;
    executeWebhook();
  }

  private void executeWebhook() {
    BackyLogger.log("STAGE 5 | Discord backup notification");
    DiscordWebhook webhook = new DiscordWebhook(config.get("discord-webhook-url").getAsString());
    webhook.setUsername("Backy");
    DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
    Instant endTime = Instant.now();
    long timeElapsedSeconds = TimeUnit.MILLISECONDS.toSeconds(Duration.between(startTime, endTime).toMillis());
    embed.setTitle("Backup complete - Took " + timeElapsedSeconds + " seconds");
    StringBuilder builder = new StringBuilder();
    builder.append("Backup finished.\n\n**Processed scripts:**\n");
    for(Map.Entry<String, Long> entry : processTime.entrySet()) {
      builder.append("• ").append(entry.getKey()).append(" (").append(entry.getValue()).append("ms)\n");
    }
    embed.setDescription(builder.toString());
    webhook.addEmbed(embed);
    try {
      webhook.execute();
    } catch (IOException e) {
      BackyLogger.log("Stage 5 failed to execute, moving on...");
      e.printStackTrace();
    }
  }

}
