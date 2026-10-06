package my.toplib.anarchyutils.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import my.toplib.anarchyutils.AnarchyUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#[a-fA-F0-9]{6}");

    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.builder()
                    .character('&')
                    .hexColors()
                    .useUnusualXRepeatedCharacterHexFormat()
                    .build();

    /**
     * Legacy string-based colour translator (&-codes, &#RRGGBB hex).
     * Prefer {@link #component(String)} for new code.
     */
    public static String color(String message){
        if (message == null) return "";
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

    /**
     * Converts a &-code / &#RRGGBB hex string into an Adventure Component.
     */
    public static Component component(String message) {
        if (message == null) return Component.empty();
        return LEGACY.deserialize(message);
    }

    public static void checkUpdate(Player p) throws URISyntaxException, IOException, InterruptedException {
        if(AnarchyUtils.instance.getConfig().getBoolean("Settings.checkForUpdates", true)){
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.github.com/repos/TOPLIB/anarchyutils/releases/latest"))
                    .GET()
                    .build();

            String body = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).body();
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();

            String latestVersion = json.get("name").getAsString();

            if (!AnarchyUtils.instance.getDescription().getVersion().equals(latestVersion)) {
                AnarchyUtils.instance.getLogger().warning("New version is available!");
                AnarchyUtils.instance.getLogger().warning("Latest version: " + latestVersion + " Your version: " + AnarchyUtils.instance.getDescription().getVersion());
                p.sendMessage(component("&fNew version! &c(Only for admins)"));
                p.sendMessage(component("&fLatest version: &a" + latestVersion + " &fYour version: &c" + AnarchyUtils.instance.getDescription().getVersion()));
                p.sendMessage(component("&fYou can download update on: Github, BlackMinecraft, Spigot"));
            }
        }
    }


    public static String format(String text, List<Placeholder> placeholders){
        for(Placeholder placeholder : placeholders){
            text = text.replace(placeholder.getPlaceholder(), placeholder.getReplacement());
        }
        return text;
    }
}
