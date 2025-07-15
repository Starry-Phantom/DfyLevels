package me.Starry_Phantom.dfyLevels.Core;

import me.Starry_Phantom.dfyLevels.DfyLevels;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

public class BitManager {
    private static File bitsFile;
    private static DfyLevels PLUGIN;
    private static int[] thresholds;

    private static ConcurrentHashMap<String, Integer> BITS;

    public static void setPlugin(DfyLevels plugin) {PLUGIN = plugin;}

    public static boolean loadBits() {
        bitsFile = new File(PLUGIN.getDataFolder(), "bits.yml");
        if (!bitsFile.exists()) {
            BITS = new ConcurrentHashMap<>();
            try {
                return bitsFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Yaml yaml = new Yaml();
        try (InputStream input = new FileInputStream(bitsFile)) {
            Map<String, Integer> temp = yaml.load(input);
            if (temp == null) BITS = new ConcurrentHashMap<>();
            else BITS = new ConcurrentHashMap<>(temp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        loadBitThresholds();

        return true;
    }

    private static void loadBitThresholds() {
        BitGroup[] groups = PLUGIN.getBitsGroups();
        int length = groups.length;

        thresholds = new int[length];

        for (int i = 0; i < length; i++) {
            thresholds[i] = groups[i].getBitAmount();
        }
    }

    public static BitGroup getBitGroup(Player p) {
        return PLUGIN.getBitsGroups()[getBitGroupIndex(p)];
    }

    private static int getBitGroupIndex(Player p) {
        int bits = getBits(p);
        int i;
        for (i = 0; i < thresholds.length; i++) {
            if (bits >= thresholds[i]) break;
        }
        return i;
    }

    public static void saveBits() {
        try {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml yaml = new Yaml(options);
            FileWriter writer = new FileWriter(bitsFile);
            yaml.dump(BITS, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getBits(UUID uuid) {
        String query = uuid.toString();
        if (BITS.containsKey(query)) return BITS.get(query);
        return 0;
    }

    public static int getBits(Player p) {
        return getBits(p.getUniqueId());
    }

    public static void setBits(UUID uuid, int amount) {
        Player player = Bukkit.getPlayer(uuid);

        int currentBits = getBits(uuid);
        String bitGroup = getBitGroup(player).getID();

        if (currentBits == 0) BITS.put(uuid.toString(), amount);
        else BITS.replace(uuid.toString(), amount);

        String newGroup = getBitGroup(player).getID();

        if (newGroup.equals(bitGroup)) return;
        updateBitGroup(player);
    }

    public static void setBits(Player p, int amount) {
        setBits(p.getUniqueId(), amount);
    }

    public static void incrementBits(UUID uuid, int amount) {
        Player player = Bukkit.getPlayer(uuid);

        int currentBits = getBits(uuid);
        String bitGroup = getBitGroup(player).getID();

        if (currentBits == 0) BITS.put(uuid.toString(), amount);
        else BITS.replace(uuid.toString(), currentBits + amount);

        String newGroup = getBitGroup(player).getID();

        if (newGroup.equals(bitGroup)) return;
        updateBitGroup(player);
    }

    public static void incrementBits(Player p, int amount) {
        incrementBits(p.getUniqueId(), amount);
    }

    private static void updateBitGroup(Player player) {
        CompletableFuture<User> f = PLUGIN.getLuckPerms().getUserManager().loadUser(player.getUniqueId());
        Executor executor = runnable -> Bukkit.getScheduler().runTask(PLUGIN, runnable);

        f.whenCompleteAsync(new BiConsumer<User, Throwable>() {
            @Override
            public void accept(User user, Throwable throwable) {
                if (user == null) throw new RuntimeException("Cannot query user from LuckPerms database! (" + player.getName() + "|" + player.getUniqueId() + ")");

                int hasGroupsBelow = getBitGroupIndex(player);
                BitGroup[] groups = PLUGIN.getBitsGroups();

                for (int i = groups.length - 1; i >= 0; i--) {
                    InheritanceNode checkNode = InheritanceNode.builder(groups[i].getID()).build();
                    if (i >= hasGroupsBelow) {
                        user.data().add(checkNode);
                    } else {
                        if (user.getNodes(NodeType.INHERITANCE).contains(checkNode)) user.data().remove(checkNode);
                    }
                }

                PLUGIN.getLuckPerms().getUserManager().saveUser(user);
            }
        }, executor);

    }
}
