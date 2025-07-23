package me.Starry_Phantom.dfyLevels.Core;

import me.Starry_Phantom.dfyLevels.DfyLevels;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.OptionalInt;

public class TabManager implements Listener {
    private final DfyLevels PLUGIN;
    private final LuckPerms luckPerms;

    public TabManager(DfyLevels plugin, LuckPerms luckPerms) {
        this.PLUGIN = plugin;
        this.luckPerms = luckPerms;

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!p.isOnline()) this.cancel();
                updateTab(p);
            }
        }.runTaskTimerAsynchronously(PLUGIN, 0, 100);
    }

    private void updateTab(Player player) {
        player.sendPlayerListHeader(Component.text("§b- §6Dungeon§bfy §3| §bv2.0.0 -"));
        player.playerListName(ChatManager.getDisplayName(player));

        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int bits = BitManager.getBits(player);
        player.sendPlayerListFooter(
                Component.text("§6Players: §b"+ onlinePlayers + " §3| §6Bits: §b" + bits).appendNewline().append(
                Component.text("§6Kills: §b" + "#")));

        String groupName = luckPerms.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup();
        OptionalInt weight = luckPerms.getGroupManager().getGroup(groupName).getWeight();
        if (weight.isPresent()) bits = bits + weight.getAsInt() * 1000;
        player.setPlayerListOrder(bits);
    }
}
