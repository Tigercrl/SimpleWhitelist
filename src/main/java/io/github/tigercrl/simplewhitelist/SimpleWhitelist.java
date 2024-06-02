package io.github.tigercrl.simplewhitelist;

import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleWhitelist extends JavaPlugin {
    public static SimpleWhitelist instance;
    public static final WhitelistConfig whitelistConfig = new WhitelistConfig();
    public static final LangConfig langConfig = new LangConfig();

    @Override
    public void onEnable() {
        instance = this;
        whitelistConfig.saveDefaultConfig();
        whitelistConfig.loadConfig();
        langConfig.saveDefaultConfig();
        langConfig.loadConfig();
        getCommand("simplewhitelist").setExecutor(new SimpleWhitelistCommand());
        getServer().getPluginManager().registerEvents(new SimpleWhitelistEventListener(), this);
        getLogger().info(langConfig.getMessage("plugin-enabled"));
    }

    @Override
    public void onDisable() {
        whitelistConfig.saveConfig();
        instance = null;
        getLogger().info(langConfig.getMessage("plugin-disabled"));
    }
}
