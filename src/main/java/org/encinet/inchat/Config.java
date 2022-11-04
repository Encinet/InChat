package org.encinet.inchat;

import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import static org.encinet.inchat.InChat.jp;

public class Config {
    public static @NotNull FileConfiguration getConfig() {
        return jp.getConfig();
    }

    public static String displayName;

    public static void load() {
        displayName = getConfig().getString("displayName");
    }
}
