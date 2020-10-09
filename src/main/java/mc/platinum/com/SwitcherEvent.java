package mc.platinum.com;

import me.ialistannen.mininbt.ItemNBTUtil;
import me.ialistannen.mininbt.NBTWrappers;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;


public class SwitcherEvent implements Listener, CommandExecutor {
    private final Logger logger = Logger.getLogger(String.valueOf(getClass()));
    private long lastSnowballTime = System.nanoTime();
    private static final long FIFTY_MILLISECONDS = Duration.ofMillis(50L).toNanos();


    private final Set<ItemStack> snowBallItems = new HashSet<>();
    private final Set<Snowball> snowBallEntities = new HashSet<>();
    private Plugin plugin = PMC.getPlugin(PMC.class);

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Validate user permissions

        // Validate user input
        Player player = (Player) sender;
        if (sender instanceof Player) {
            if (sender.hasPermission("platinum.switcher")) {


                ItemStack ball = new ItemStack(Material.SNOW_BALL);
                NBTWrappers.NBTTagCompound compound = ItemNBTUtil.getTag(ball);
                compound.setBoolean("amazingSnowball", true);
                compound.setLong("amazingSnowballUnique", ThreadLocalRandom.current().nextLong());
                ball = ItemNBTUtil.setNBTTag(compound, ball);
                ItemMeta meta = ball.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&5&lSWITCHER"));
                ball.setItemMeta(meta);
                player.getInventory().addItem(ball);
                logger.info("Adding Switcher" + ball + "To Test");
            }else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "You Need The Switcher Permission To Execute This Command."));
            }
        }else {
            sender.sendMessage("You Need to Be a Player To Execute this Command");
        }

        return true;
    }

    @EventHandler
    public void onRightClickHoldingCustomSnowball(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemStack item = event.getItem();
            logger.info("Target Block is " + event.getClickedBlock());

            NBTWrappers.NBTTagCompound compound = ItemNBTUtil.getTag(item);
            if (compound.getBoolean("amazingSnowball")) {
                logger.info("Found Custom Snowball");
                // Found a custom snowball
                event.setCancelled(true);
                Snowball launched = event.getPlayer().launchProjectile(Snowball.class);
                snowBallEntities.add(launched);
                logger.info("Launched Custom Snowball");
                Player player = event.getPlayer();
                player.getInventory().setItemInHand(null);
            }
        }
    }


    @EventHandler
    public void onSnowballHit(EntityDamageByEntityEvent event)
    {
        Entity damaged = event.getEntity();
        Entity snowball = event.getDamager();

        if(damaged instanceof Player)
            if(snowball instanceof Snowball)
                if (snowBallEntities.remove(snowball))
                {
                    LivingEntity shooter = (LivingEntity) ((Snowball) snowball).getShooter();
                    if(shooter instanceof Player) {
                        Player playerThrower = (Player) shooter;
                        Player playerHit = (Player) damaged;
                        Location shooterLocation = shooter.getLocation();
                        Location hitplayerLocation = damaged.getLocation();
                        shooter.teleport(hitplayerLocation);
                        damaged.teleport(shooterLocation);
                        if (shooter != damaged) {
                            ((Player) shooter).sendTitle("SWAPPED", "With " + damaged.getName());
                            String titleDamaged = "&5&lSWAPPED";
                            String subTitleDamaged = "You Have Been Swapped With: " + shooter.getName();
                            String titleShooter = "&5&lSWAPPED";
                            String subTitleShooter = "With: " + damaged.getName();
                            ((Player) damaged).sendTitle(ChatColor.translateAlternateColorCodes('&', titleDamaged), ChatColor.translateAlternateColorCodes('&', subTitleDamaged));
                            ((Player) shooter).sendTitle(ChatColor.translateAlternateColorCodes('&', titleShooter), ChatColor.translateAlternateColorCodes('&', subTitleShooter));
                            ((Player) shooter).playEffect(shooterLocation, Effect.ENDER_SIGNAL, 1);
                            ((Player) damaged).playEffect(hitplayerLocation, Effect.ENDER_SIGNAL, 1);
                            logger.info("Triggered Event EntityDamageByEntity");
                        }
                    }else
                        damaged.sendMessage("You Can't shoot yourself!");
                    event.setCancelled(true);
                }

    }
}
