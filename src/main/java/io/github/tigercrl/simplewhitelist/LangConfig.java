package io.github.tigercrl.simplewhitelist;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LangConfig {
    private YamlConfiguration config;
    private String playerLanguage;
    private String consoleLanguage;
    private String fallbackLanguage;
    private final Map<String, YamlConfiguration> languages = new HashMap<>();

    public void saveDefaultConfig() {
        if (!new File(SimpleWhitelist.instance.getDataFolder(), "lang.yml").exists())
            SimpleWhitelist.instance.saveResource("lang.yml", false);
        for (String lang : new String[]{"en_us", "zh_cn"}) {
            if (!new File(new File(SimpleWhitelist.instance.getDataFolder(), "lang"), lang + ".yml").exists())
                SimpleWhitelist.instance.saveResource("lang/" + lang + ".yml", false);
        }
    }

    public void loadConfig() {
        File configFile = new File(SimpleWhitelist.instance.getDataFolder(), "lang.yml");
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        languages.clear();
        for (File file : new File(SimpleWhitelist.instance.getDataFolder(), "lang").listFiles()) {
            YamlConfiguration lang = new YamlConfiguration();
            try {
                lang.load(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            languages.put(file.getName().replace(".yml", ""), lang);
        }
        playerLanguage = config.getString("player-language");
        consoleLanguage = config.getString("console-language");
        fallbackLanguage = config.getString("fallback-language");
        if (fallbackLanguage == null || !languages.containsKey(fallbackLanguage)) {
            fallbackLanguage = "en_us";
            SimpleWhitelist.instance.getLogger().warning("Fallback language is not set or invalid, using en_us instead");
        }
        if (playerLanguage == null || !playerLanguage.equals("auto") && !languages.containsKey(playerLanguage)) {
            playerLanguage = fallbackLanguage;
            SimpleWhitelist.instance.getLogger().warning("Player language is not set or invalid, using fallback language instead");
        }
        if (consoleLanguage == null || !languages.containsKey(consoleLanguage)) {
            consoleLanguage = fallbackLanguage;
            SimpleWhitelist.instance.getLogger().warning("Console language is not set or invalid, using fallback language instead");
        }
    }

    public String getMessage(String key) {
        String message = languages.get(consoleLanguage).getString(key);
        return message != null ? ChatColor.translateAlternateColorCodes('&', message) : key;
    }

    public String getMessage(String key, Player p) {
        String message = null;
        if (playerLanguage.equals("auto")) {
            if (languages.containsKey(p.getLocale())) message = languages.get(p.getLocale()).getString(key);
            else message = languages.get(fallbackLanguage).getString(key);
        } else message = languages.get(playerLanguage).getString(key);
        return message != null ? ChatColor.translateAlternateColorCodes('&', message) : key;
    }

    public String getMessage(String key, ServerOperator op) {
        if (op instanceof Player) {
            return getMessage(key, (Player) op);
        } else {
            return getMessage(key);
        }
    }

    public void reloadConfig() {
        loadConfig();
    }
}
