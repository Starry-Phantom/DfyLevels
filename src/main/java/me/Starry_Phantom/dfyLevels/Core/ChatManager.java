package me.Starry_Phantom.dfyLevels.Core;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.Starry_Phantom.dfyLevels.DfyLevels;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatManager implements Listener, ChatRenderer {
    private final DfyLevels PLUGIN;

    public ChatManager(DfyLevels plugin) {this.PLUGIN = plugin;}

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        event.renderer(this);
    }

    @Override
    public Component render(Player player, Component displayName, Component message, Audience audience) {
        ComponentBuilder builder = Component.text();
        builder.append(Component.text("§8[]-"));
        // TODO: Add badge handling

        String prefix;
        try {
            prefix = PLUGIN.getLuckPerms().getUserManager().getUser(player.getUniqueId()).getCachedData().getMetaData().getPrefix();
        } catch (NullPointerException e) {prefix = "§f";}
        if (prefix == null) prefix = "§f";

        builder.append(TextUtilities.applyHexColoring(prefix + player.getName()));
        builder.append(Component.text("§r§f: "));
        builder.append(message);
        return builder.build();
    }
}
