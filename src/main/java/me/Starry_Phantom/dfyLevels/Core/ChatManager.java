package me.Starry_Phantom.dfyLevels.Core;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.Starry_Phantom.dfyLevels.DfyLevels;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.CompletableFuture;

public class ChatManager implements Listener, ChatRenderer {
    private static LuckPerms luckPerms;
    private static DfyLevels PLUGIN;

    public ChatManager(DfyLevels plugin) {PLUGIN = plugin;}

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        event.renderer(this);
    }

    @Override
    public Component render(Player player, Component displayName, Component message, Audience audience) {
        ComponentBuilder builder = Component.text();
        builder.append(TextUtilities.applyHexColoring("§8[]-"));
        builder.append(getDisplayName(player));
        builder.append(Component.text("§r§f: "));
        builder.append(message);
        return builder.build();
    }

    public static TextComponent getDisplayName(Player player) {
        String prefix;
        CompletableFuture<String> futureString = CompletableFuture.supplyAsync(() -> {
            String futurePrefix;
            try {
                futurePrefix = PLUGIN.getLuckPerms().getUserManager().getUser(player.getUniqueId()).getCachedData().getMetaData().getPrefix();
            } catch (NullPointerException e) {futurePrefix = "§f";}
            if (futurePrefix == null) futurePrefix = "§f";
            return futurePrefix;
        });
        prefix = futureString.join();

        return TextUtilities.applyHexColoring( prefix + player.getName());

    }
}
