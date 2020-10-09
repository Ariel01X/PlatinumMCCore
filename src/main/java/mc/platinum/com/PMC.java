package mc.platinum.com;

import org.bukkit.plugin.java.JavaPlugin;

public class PMC extends JavaPlugin {
    public void onEnable() {
        getCommand("pmc").setExecutor(new pmccommand());
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new LightningEvent(), this);
        getServer().getPluginManager().registerEvents(new SwitcherEvent(), this);
        getServer().getPluginManager().registerEvents(new SwitcherEvent(), this);
        getServer().getPluginManager().registerEvents(new FlashbangEvent(), this);
        getCommand("flashtest").setExecutor(new FlashbangEvent());
        getCommand("switcher").setExecutor(new SwitcherEvent());

    }
    public void onDisable() {

    }
}
