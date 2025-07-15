package me.Starry_Phantom.dfyLevels.Commands;

import me.Starry_Phantom.dfyLevels.Core.BitManager;
import me.Starry_Phantom.dfyLevels.DfyLevels;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BitsCommand implements CommandExecutor {
   private final DfyLevels PLUGIN;

    /*
        /bits <set|get|add|remove> [player] <amount>
     */

    public BitsCommand(DfyLevels plugin) {this.PLUGIN = plugin;}

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            Player target = null;
            if (commandSender instanceof Player p) target = p;
            if (target == null) return false;

            int bits = BitManager.getBits(target);
            commandSender.sendMessage(PLUGIN.getMainPrefix() + "You have§b " + bits + "§e bits.");
            return true;
        }
        if (!commandSender.hasPermission("dfylevels.bits.manage")) {
            commandSender.sendMessage(PLUGIN.getPermissionPrefix() + "You do not have permission to run this command!");
            return true;
        }
        if (args.length > 3) return false;

        Player target;
        if (args.length == 2 || (args[0].equals("get") && args.length == 1)) {
            if (commandSender instanceof Player p) target = p;
            else {
                commandSender.sendMessage(PLUGIN.getErrorPrefix() + "Please specify a target!");
                return true;
            }
        } else if (args.length == 3) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                commandSender.sendMessage(PLUGIN.getErrorPrefix() + "Invalid target!");
                return true;
            }
        } else return false;

        int amount = 0;
        try {
            amount = Integer.parseInt(args[args.length - 1]);
        } catch (NumberFormatException e) {
            if (!args[0].equals("get")) {
                commandSender.sendMessage(PLUGIN.getErrorPrefix() + "Invalid amount!");
                return true;
            }
        }

        switch (args[0].toLowerCase()) {
            case "set":
                BitManager.setBits(target, amount);
                commandSender.sendMessage(PLUGIN.getPrefix() + "Set bits of§6 " + target.getName() + "§e to§b " + amount + "§e.");
                return true;
            case "get":
                int bits = BitManager.getBits(target);
                commandSender.sendMessage(PLUGIN.getPrefix() + "§6" + target.getName() + " §ehas§b " + bits + "§e bits.");
                return true;
            case "add", "remove":
                if (args[0].equalsIgnoreCase("remove")) amount *= -1;
                BitManager.incrementBits(target, amount);
                commandSender.sendMessage(PLUGIN.getPrefix() + "Changed bits of§6 " + target.getName() + "§e by§b " + amount + "§e. §3(" + BitManager.getBits(target) + ")");
                return true;
            default:
                return false;

        }
    }
}
