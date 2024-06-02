package io.github.tigercrl.simplewhitelist;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.Set;

public class WhitelistConfig {
    private File configFile;
    private YamlConfiguration config;
    private List<String> whitelist;

    public void saveDefaultConfig() {
        if (!new File(SimpleWhitelist.instance.getDataFolder(), "whitelist.yml").exists())
            SimpleWhitelist.instance.saveResource("whitelist.yml", false);
    }

    public void saveConfig() {
        config.set("whitelist", whitelist);
        try {
            config.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadConfig() {
        configFile = new File(SimpleWhitelist.instance.getDataFolder(), "whitelist.yml");
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        whitelist = config.getStringList("whitelist");
    }

    public void addPlayer(String name) {
        if (!playerExists(name)) {
            whitelist.add(name);
            saveConfig();
        }
    }

    public boolean playerExists(String name) {
        return whitelist.contains(name);
    }

    public void removePlayer(String name) {
        if (playerExists(name)) {
            whitelist.remove(name);
            saveConfig();
            Player p = Bukkit.getPlayerExact(name);
            if (p != null && p.isOnline())
                p.kickPlayer(SimpleWhitelist.langConfig.getMessage("prefix", p) + SimpleWhitelist.langConfig.getMessage("remove-message", p));
        }
    }

    public void reloadConfig() {
        loadConfig();
    }

    public int importWhitelist() {
        int playersCount = 0;
        for (OfflinePlayer p : Bukkit.getWhitelistedPlayers()) {
            if (!playerExists(p.getName())) {
                whitelist.add(p.getName());
                playersCount++;
            }
        }
        saveConfig();
        return playersCount;
    }

    public int exportWhitelist() {
        Set<OfflinePlayer> vanillaWhitelist = Bukkit.getWhitelistedPlayers();
        int playersCount = 0;
        for (String p : whitelist) {
            OfflinePlayer player = Bukkit.getPlayerExact(p);
            if (player == null) {
                player = Bukkit.getPlayer(p);
            }
            if (!vanillaWhitelist.contains(player)) {
                if (player != null) player.setWhitelisted(true);
                else Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist add " + p);
                playersCount++;
            }
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist reload");
        return playersCount;
    }

    public List<String> getWhitelistedPlayers() {
        return whitelist;
    }
}
