package com.steel9.MCAlerty;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class MyListener implements Listener {
    private Plugin mPlugin;

    public MyListener(Plugin plugin) {
        mPlugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String msg = Main.getAlertsString(event.getPlayer().getUniqueId(), "Welcome!", true);

        if (!msg.equalsIgnoreCase("")) {
            event.getPlayer().sendMessage(msg);
            //Main.updateAlertsRead(mPlugin, event.getPlayer().getUniqueId());
        }
    }
}
