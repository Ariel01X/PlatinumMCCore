package mc.platinum.com;

import me.ialistannen.mininbt.ItemNBTUtil;
import me.ialistannen.mininbt.NBTWrappers;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class SwitcherEvent implements Listener, CommandExecutor {

    private final Logger logger = Logger.getLogger(String.valueOf(getClass()));

    private final Map<UUID, Cooldown> cooldowns = new HashMap<>();
    private final Set<Snowball> snowBallEntities = new HashSet<>();

    @Override
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
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            Player player = event.getPlayer();
            logger.info("Target Block is " + event.getClickedBlock());

            if (!ItemNBTUtil.getTag(item).getBoolean("amazingSnowball")) {
                return;
            }
            logger.info("Found Custom Snowball");
            event.setCancelled(true);
            Cooldown cooldown = cooldowns.computeIfAbsent(player.getUniqueId(), (k) -> new Cooldown(Duration.ofSeconds(5L)));
            Duration timeLeft = cooldown.triggerOrGet();
            if (!timeLeft.isZero()) {
                sendMessage(player, "Your cooldown still has " + timeLeft.getSeconds() + " remaining");
                return;
            }
            Snowball launched = player.launchProjectile(Snowball.class);
            snowBallEntities.add(launched);
            logger.info("Launched Custom Snowball");
            player.getInventory().setItemInHand(null);
        }
    }
    
    private void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
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
    
    @EventHandler
    public void cleanupCooldown(PlayerQuitEvent evt) {
        cooldowns.remove(evt.getPlayer().getUniqueId());
    }
}