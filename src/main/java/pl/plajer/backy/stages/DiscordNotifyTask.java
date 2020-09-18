package pl.plajer.backy.stages;

import com.google.gson.JsonObject;

import java.io.IOException;

import pl.plajer.backy.BackyLogger;
import pl.plajer.backy.discord.DiscordWebhook;

/**
 * @author Plajer
 * <p>
 * Created at 18.09.2020
 */
public class DiscordNotifyTask {

  private JsonObject config;

  public DiscordNotifyTask(JsonObject config) {
    this.config = config;
    executeWebhook();
  }

  private void executeWebhook() {
    BackyLogger.log("STAGE 5 | Discord backup notification");
    DiscordWebhook webhook = new DiscordWebhook(config.get("discord-webhook-url").getAsString());
    webhook.setUsername("Backy");
    DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
    embed.setTitle("Backup complete!");
    embed.setDescription("Backup finished without any problems.");
    webhook.addEmbed(embed);
    try {
      webhook.execute();
    } catch (IOException e) {
      BackyLogger.log("Stage 5 failed to execute, moving on...");
      e.printStackTrace();
    }
  }

}
