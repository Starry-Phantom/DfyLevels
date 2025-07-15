package me.Starry_Phantom.dfyLevels.Core;

import me.Starry_Phantom.dfyLevels.DfyLevels;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Notifier  implements Listener {
    private final DfyLevels PLUGIN;

    public Notifier(DfyLevels plugin) {this.PLUGIN = plugin;}

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("dfylevels.admin.notifications")) return;

        if (!PLUGIN.doGroupsExist()) {
            String message = "Warning: LuckPerms groups do not exist! Run '/dfl create' to resolve this issue.";
            player.sendMessage(Component.text(PLUGIN.getErrorPrefix() + message));
        }
    }
}
