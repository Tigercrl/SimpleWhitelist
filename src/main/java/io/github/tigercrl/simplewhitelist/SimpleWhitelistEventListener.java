package io.github.tigercrl.simplewhitelist;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class SimpleWhitelistEventListener implements Listener {
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        if ((e.getResult() == PlayerLoginEvent.Result.KICK_WHITELIST || e.getResult() == PlayerLoginEvent.Result.ALLOWED))
            if (SimpleWhitelist.whitelistConfig.playerExists(e.getPlayer().getName())) e.allow();
            else {
                e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, SimpleWhitelist.langConfig.getMessage("prefix") + SimpleWhitelist.langConfig.getMessage("kick-message"));
            }
    }
}
