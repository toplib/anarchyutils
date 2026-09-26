package my.toplib.anarchyutils.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import my.toplib.anarchyutils.AnarchyUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#[a-fA-F\0-9]{6}");

    public static String color(String message){
        Matcher matcher = HEX_PATTERN.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace("&#", "x");
            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch)
                builder.append("&").append(c);
            message = message.replace(hexCode, builder.toString());
            matcher = HEX_PATTERN.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void checkUpdate(Player p) throws URISyntaxException, IOException, InterruptedException {
        if(AnarchyUtils.instance.getConfig().getBoolean("Settings.checkForUpdates", true)){
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.github.com/repos/TOPLIB/anarchyutils/releases/latest"))
                    .GET()
                    .build();

            String body = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).body();
            JsonObject json = new JsonParser().parse(body).getAsJsonObject();

            String latestVersion = json.get("name").getAsString();
            String downloadLink = json
                    .getAsJsonArray("assets")
                    .get(0).getAsJsonObject()
                    .get("browser_download_url").getAsString();

            if (!AnarchyUtils.instance.getDescription().getVersion().equals(latestVersion)) {
                AnarchyUtils.instance.getLogger().warning("New version is available!");
                AnarchyUtils.instance.getLogger().warning("Latest version: " + latestVersion + " Your version: " + AnarchyUtils.instance.getDescription().getVersion());
                p.sendMessage(color("&fNew version! &c(Only for admins)"));
                p.sendMessage(color("&fLatest version: &a" + latestVersion + " &fYour version: &c" + AnarchyUtils.instance.getDescription().getVersion()));
                p.sendMessage(color("&fYou can download update on: Github, BlackMinecraft, Spigot"));
            }
        }
    }


    public static String format(String text, List<Placeholder> placeholders){
        for(Placeholder placeholder : placeholders){
            text.replaceAll(placeholder.getPlaceholder(), placeholder.getReplacement());
        }
        return text;
    }
}
