package mc.platinum.com;

import me.ialistannen.mininbt.ItemNBTUtil;
import me.ialistannen.mininbt.NBTWrappers;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class FlashbangEvent implements Listener, CommandExecutor {
    private final Logger logger = Logger.getLogger(String.valueOf(getClass()));
    private final Set<ItemStack> flashBangItems = new HashSet<>();
    private final Set<Egg> flashBangEntities = new HashSet<>();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player) {
            if (sender.hasPermission("platinum.flashbang")) {
                ItemStack flashBang = new ItemStack(Material.EGG, 1);

                ItemMeta meta = flashBang.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lFLASHBANG"));

                ArrayList<String> lore = new ArrayList<String>();
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Throw a flashbang to temporarily blind your enemies!"));
                lore.add("");
                lore.add(ChatColor.translateAlternateColorCodes('&', "&620 Seconds &7Cooldown"));
                meta.setLore(lore);
                flashBang.setItemMeta(meta);

                NBTWrappers.NBTTagCompound compound = ItemNBTUtil.getTag(flashBang);
                compound.setBoolean("flashBangItem", true);
                compound.setLong("flashBangItemUnique", ThreadLocalRandom.current().nextLong());
                flashBang = ItemNBTUtil.setNBTTag(compound, flashBang);


                Player player = (Player) sender;
                player.getInventory().addItem(flashBang);
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou Need flashbang permission to use this command!"));



            }

        }else {
            sender.sendMessage("You Need To Be a Player To Execute this command.");
        }
        return true;
    }
    @EventHandler
    public void onRightClickHoldingFlashBang(PlayerInteractEvent e) {
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            ItemStack item  = e.getItem();
            NBTWrappers.NBTTagCompound compound = ItemNBTUtil.getTag(item);
            if(compound.getBoolean("flashBangItem")) {
                logger.info("Found flashbang.");
                e.setCancelled(true);
                Egg launched  = e.getPlayer().launchProjectile(Egg.class);
                flashBangEntities.add(launched);
                Player player = e.getPlayer();
                player.getInventory().setItemInHand(null);
            }


        }
    }

    @EventHandler
    public void onThrowFlashBang(ProjectileHitEvent e) {
        Projectile flashBang = e.getEntity();
        ProjectileSource shooter = flashBang.getShooter();

        if(flashBang instanceof Egg) {
            if (shooter instanceof Player) {
                if (flashBangEntities.remove(flashBang)) {
                    Location loc = flashBang.getLocation();
                    World world = flashBang.getWorld();
                    for (Entity entity : world.getNearbyEntities(loc, 10, 10, 10)) {
                        if (entity instanceof Player) {
                            if (entity.getLocation().distance(flashBang.getLocation()) <= 9) {
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



}
