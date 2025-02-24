package com.yourplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GamblingPlugin extends JavaPlugin {

    private boolean eventActive = false;
    private final HashMap<UUID, Integer> gambleCounts = new HashMap<>();
    private final HashMap<UUID, List<ItemStack>> playerWins = new HashMap<>();
    private final HashMap<UUID, List<ItemStack>> playerLosses = new HashMap<>();
    private final List<Material> bannedItems = Arrays.asList(Material.BARRIER, Material.BEDROCK); // Örnek yasaklı eşyalar

    @Override
    public void onEnable() {
        // Plugin etkinleştirildiğinde yapılacak işlemler
        getCommand("kumarac").setExecutor(new StartEventCommand());
        getCommand("kumarkapa").setExecutor(new StopEventCommand());
        getCommand("kumar").setExecutor(new GambleCommand());

        // Envanter dinleyicisini kaydet
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
    }

    // Kumar etkinliğini başlatan komut
    public class StartEventCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender.hasPermission("gambling.admin")) {
                eventActive = true;
                Bukkit.broadcastMessage("KUMAR ETKİNLİĞİ BAŞLATILMIŞTIR");
                return true;
            }
            return false;
        }
    }

    // Kumar etkinliğini kapatan komut
    public class StopEventCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender.hasPermission("gambling.admin")) {
                eventActive = false;
                Bukkit.broadcastMessage("KUMAR ETKİNLİĞİ BİTMİŞTİR");
                return true;
            }
            return false;
        }
    }

    // Kumar komutu
    public class GambleCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (eventActive) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    MainGUI mainGUI = new MainGUI(player, gambleCounts, playerWins, playerLosses, bannedItems);
                    mainGUI.open();
                }
                return true;
            } else {
                sender.sendMessage("Kumar etkinliği kapalıdır");
                return false;
            }
        }
    }
}
