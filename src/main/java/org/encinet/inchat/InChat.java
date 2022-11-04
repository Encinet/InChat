package org.encinet.inchat;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatPreviewEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum TYPE {
    TEXT, HEX
}
public final class InChat extends JavaPlugin implements Listener {
    public static JavaPlugin jp;
    private static final Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");
    private static final Pattern MC_HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    @Override
    public void onEnable() {
        // Plugin startup logic
        jp = JavaPlugin.getProvidingPlugin(InChat.class);
        saveDefaultConfig();
        reloadConfig();
        Config.load();

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    @EventHandler
    public static void onPlayerChat(AsyncPlayerChatPreviewEvent event) {
        Player player = event.getPlayer();
        String message = color(event.getMessage());
        String displayName = color(PlaceholderAPI.setPlaceholders(player, Config.displayName));

        player.displayName(Component.text(displayName));
        event.setMessage(message);
    }

    private static String color(final String text) {
        return ChatColor.translateAlternateColorCodes('&', translateHexColorCodes(text)).replace("%", "%%");
    }


    private static String translateHexColorCodes(final String message) {
        final char colorChar = ChatColor.COLOR_CHAR;

        final Matcher matcher = MC_HEX_PATTERN.matcher(message);
        final StringBuilder sb = new StringBuilder(message.length() + 4 * 8);

        while (matcher.find()) {
            final String group = matcher.group();

            matcher.appendReplacement(sb, colorChar + "x"
                    + colorChar + group.charAt(0) + colorChar + group.charAt(1)
                    + colorChar + group.charAt(2) + colorChar + group.charAt(3)
                    + colorChar + group.charAt(4) + colorChar + group.charAt(5));
        }
        return matcher.appendTail(sb).toString();
    }

    private static TextComponent getDisplayName(final Player player) {
        TextComponent tc = Component.text().build();

        Map<TYPE, String> map = new LinkedHashMap<>();

        String message = PlaceholderAPI.setPlaceholders(player, Config.displayName);
        final Matcher matcher = HEX_PATTERN.matcher(message);
        while (matcher.find()) {
            String text = message.substring(0, matcher.start());
            if (!text.equals("")) {
                map.put(TYPE.TEXT, text);
            }

            map.put(TYPE.HEX, message.substring(matcher.start(), matcher.end()));
            message = message.substring(matcher.end());
        }
        for(Map.Entry<TYPE, String> entry : map.entrySet()) {
            if (entry.getKey().equals(TYPE.TEXT)) {
                tc.append(Component.text(entry.getValue()));
            } else if (entry.getKey().equals(TYPE.HEX)) {
                tc.color(TextColor.fromHexString(entry.getValue()));
            }
        }
        return tc;
    }
}
