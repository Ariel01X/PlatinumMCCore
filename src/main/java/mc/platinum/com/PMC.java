package mc.platinum.com;

import org.bukkit.plugin.java.JavaPlugin;

public class PMC extends JavaPlugin {
    public void onEnable() {
        SwitcherEvent switcherEvent = new SwitcherEvent();
        FlashbangEvent flashbangEvent = new FlashbangEvent();
        getCommand("pmc").setExecutor(new pmccommand());
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new LightningEvent(), this);
        getServer().getPluginManager().registerEvents(switcherEvent, this);
        getServer().getPluginManager().registerEvents(flashbangEvent, this);
        getCommand("flashbang").setExecutor(flashbangEvent);
        getCommand("switcher").setExecutor(switcherEvent);

    }
    public void onDisable() {

    }
}
