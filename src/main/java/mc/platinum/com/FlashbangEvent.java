package mc.platinum.com;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

public class FlashbangEvent implements Listener, CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        Player player = (Player) sender;
        return true;



    }
    @EventHandler
    public void onThrowFlashBang(ProjectileHitEvent e) {
        Projectile flashBang = e.getEntity();
        ProjectileSource shooter = flashBang.getShooter();

        if(flashBang instanceof Egg) {
            if(shooter instanceof Player)  {
                Location loc = flashBang.getLocation();
                World world = flashBang.getWorld();
                for(Entity entity : world.getNearbyEntities(loc,10,10,10)) {
                    if(entity instanceof Player) {
                        if(entity.getLocation().distance(flashBang.getLocation()) <= 9) {
                            ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 2), true);
                            ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 120, 2), true);
                            ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 2), true);
                            String titleDamaged = "&4&LFLASHBANG";
                            String subTitleDamaged = "&cYou've been Flashbanged By " + ((Player) shooter).getDisplayName();
                            ((Player) entity).sendTitle(ChatColor.translateAlternateColorCodes('&', titleDamaged), ChatColor.translateAlternateColorCodes('&', subTitleDamaged));
                        }
                    }
                }

            }
        }



    }



}
