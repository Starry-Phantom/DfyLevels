package me.Starry_Phantom.dfyLevels.Commands;

import me.Starry_Phantom.dfyLevels.Core.BitGroup;
import me.Starry_Phantom.dfyLevels.DfyLevels;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.types.DisplayNameNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.WeightNode;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

public class CoreCommand implements CommandExecutor {
    private final DfyLevels PLUGIN;

    public CoreCommand(DfyLevels plugin) {this.PLUGIN = plugin;}

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!commandSender.hasPermission("dfylevels.admin")) {
            if (commandSender instanceof Player p) p.sendMessage(Component.text(PLUGIN.getPermissionPrefix() + "You do not have permission to run this command!"));
            else commandSender.sendMessage(Component.text(PLUGIN.getSystemPrefix() + "No permission!"));
            return true;
        }
        if (args.length == 0) return sendHelpMessage(commandSender);
        if (args.length == 1 && args[0].equalsIgnoreCase("create")) return initGroups(commandSender);
        if (args.length == 1 && args[0].equalsIgnoreCase("update")) return updateGroups(commandSender);
        return false;
    }

    private boolean initGroups(CommandSender sender) {
        if (sender instanceof Player p) p.sendMessage(Component.text(PLUGIN.getPrefix() + "Initializing groups..."));
        else sender.sendMessage(PLUGIN.getSystemPrefix() + "Initializing groups...");
        loadGroups(sender);
        return true;
    }

    private void loadGroups(CommandSender sender) {
        Server server = PLUGIN.getServer();
        CommandSender console = server.getConsoleSender();
        LuckPerms lp = PLUGIN.getLuckPerms();
        BitGroup[] groups = PLUGIN.getBitsGroups();
        Group[] loadedGroups = new Group[groups.length];

        for (int i = 0; i < groups.length; i++) {
            String s = groups[i].getID();
            int weight = groups[i].getWeight();
            Group group = lp.getGroupManager().getGroup(s);
            if (group == null) {
                server.dispatchCommand(console, "lp creategroup " + s + " " + weight + " " + s.toUpperCase());
            }
        }

        sender.sendMessage(Component.text(PLUGIN.getPrefix() + "Groups created. Run '/dfl update' to update their data."));
    }

    private boolean updateGroups(CommandSender sender) {
        sender.sendMessage(Component.text(PLUGIN.getPrefix() + "Initializing..."));
        LuckPerms lp = PLUGIN.getLuckPerms();
        BitGroup[] groups = PLUGIN.getBitsGroups();
        Executor executor = runnable -> Bukkit.getScheduler().runTask(PLUGIN, runnable);

        CompletableFuture<Optional<Group>>[] loadingGroups = new CompletableFuture[groups.length];
        for (int i = 0; i < groups.length; i++) {
            loadingGroups[i] = lp.getGroupManager().loadGroup(groups[i].getID());
        }

        for (CompletableFuture<Optional<Group>> f : loadingGroups) {
            f.whenCompleteAsync(new BiConsumer<Optional<Group>, Throwable>() {
                @Override
                public void accept(Optional<Group> group, Throwable throwable) {
                    if (throwable != null) return;
                    if (group.isEmpty()) {
                        PLUGIN.severe("A group was not found while initializing groups!");
                        return;
                    }

                    Group g = group.get();
                    BitGroup bitGroup = PLUGIN.getBitGroup(g.getName());

                    PrefixNode prefix = PrefixNode.builder(bitGroup.getPrefix(), bitGroup.getWeight()).build();
                    WeightNode weight = WeightNode.builder(bitGroup.getWeight()).build();
                    DisplayNameNode displyName = DisplayNameNode.builder(bitGroup.getID().toUpperCase()).build();

                    g.data().add(prefix);
                    g.data().add(weight);
                    g.data().add(displyName);

                    lp.getGroupManager().saveGroup(g);

                }
            }, executor);
        }

        CompletableFuture.allOf(loadingGroups).whenCompleteAsync(new BiConsumer<Void, Throwable>() {
            @Override
            public void accept(Void unused, Throwable throwable) {
                if (throwable != null) {
                    sender.sendMessage(Component.text(PLUGIN.getErrorPrefix() + "An error occurred while executing this command..."));
                }
                sender.sendMessage(Component.text(PLUGIN.getPrefix() + "All groups initialized!"));
            }
        }, executor);
        return true;
    }

    private boolean sendHelpMessage(CommandSender commandSender) {
        return false;
    }
}
