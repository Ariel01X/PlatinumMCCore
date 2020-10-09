package mc.platinum.com;


import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class LightningEvent implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player killed = e.getEntity();
        Player killer = e.getEntity().getKiller();
        Player player = (Player) e.getEntity();
        if(killer instanceof Player){
        Location location = killed.getLocation();
        World world = killed.getWorld();
        world.strikeLightningEffect(location);
        killer.playEffect(killer.getLocation(), Effect.FLAME, Integer.MAX_VALUE);
        killer.playSound(killer.getLocation(), Sound.ITEM_PICKUP, 2f, 2f);
            System.out.println("PvP: " + killer + "Killed " + killed);

    }
}}
