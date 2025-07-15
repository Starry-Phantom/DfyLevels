package me.Starry_Phantom.dfyLevels;

import me.Starry_Phantom.dfyLevels.Commands.BitsCommand;
import me.Starry_Phantom.dfyLevels.Commands.CoreCommand;
import me.Starry_Phantom.dfyLevels.Core.*;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public final class DfyLevels extends JavaPlugin {
    private static final String SYSTEM_PREFIX = "[dfyLevels]";
    private static final String PREFIX = "§8[§6dfy§bLevels§8]§e ";
    private static final String PERMISSION_PREFIX = "§c[dfy] ";
    private static final String MAIN_PREFIX = "§8[§6dfy§8]§e ";
    private static final String ERROR_PREFIX = "§c[dfyLevels] ";
    private static final BitGroup[] BITS_GROUPS = {
            new BitGroup("grandmaster", "§5[§dGRANDMASTER§5]§d ", 7000, 50000),
            new BitGroup("master", "§#eb8a4a[MASTER] ", 6000, 25000),
            new BitGroup("epic", "§5[EPIC] ", 5000, 15000),
            new BitGroup("swag", "§3[SWAG] ", 4000, 10000),
            new BitGroup("pog", "§3[§bPOG§3]§b ", 3000, 5000),
            new BitGroup("mvp", "§1[§9MVP§1]§9 ", 2000, 2500),
            new BitGroup("vip", "§2[§aVIP§2]§a ", 1000, 1000),
            new BitGroup("default", "§f", 0, 0),
    };

    private LuckPerms luckPerms;
    private boolean groupsExist;

    @Override
    public void onEnable() {
        // Plugin startup logic
        resolveDependencies();

        loadData();

        registerListeners();
        registerCommands();

        establishAutoSave();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        BitManager.saveBits();
    }

    private void loadData() {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();

        TextUtilities.setPlugin(this);
        BitManager.setPlugin(this);

        BitManager.loadBits();
        groupsExist = checkBitGroups();
    }

    private void resolveDependencies() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        } else {
            throw new RuntimeException("Missing dependency (LuckPerms)");
        }
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new Notifier(this), this);
        Bukkit.getPluginManager().registerEvents(new ChatManager(this), this);
    }

    private void registerCommands() {
        this.getCommand("dfylevels").setExecutor(new CoreCommand(this));
        this.getCommand("bits").setExecutor(new BitsCommand(this));
    }

    private void establishAutoSave() {
        new BukkitRunnable() {
            @Override
            public void run() {
                BitManager.saveBits();
            }
        }.runTaskTimer(this, (long) (20 * 60 * 2.5), 20 * 60 * 5);
    }

    public boolean doGroupsExist() {
        return groupsExist;
    }
    public String getSystemPrefix() {return  SYSTEM_PREFIX;}
    public String getPrefix() {return  PREFIX;}
    public String getPermissionPrefix() {return  PERMISSION_PREFIX;}
    public String getErrorPrefix() {return  ERROR_PREFIX;}
    public String getMainPrefix() {return  MAIN_PREFIX;}

    public BitGroup[] getBitsGroups() {return BITS_GROUPS;}

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    private boolean checkBitGroups() {
        for (BitGroup group : BITS_GROUPS) {
            Group g = luckPerms.getGroupManager().getGroup(group.getID());
            if (g == null) return false;
        }
        return true;
    }

    public void warn(String s) {
        getLogger().warning(SYSTEM_PREFIX + " " + s);
    }

    public void log(String s) {
        getLogger().info(SYSTEM_PREFIX + " " + s);
    }

    public void severe(String s) {
        getLogger().severe(SYSTEM_PREFIX + " " + s);
    }

    public BitGroup getBitGroup(String id) {
        for (BitGroup g : BITS_GROUPS) if (g.getID().equals(id)) return g;
        return null;
    }
}
