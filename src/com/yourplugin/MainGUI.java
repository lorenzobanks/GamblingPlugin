package com.yourplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MainGUI {

    private final Inventory mainInventory;
    private final Player player;
    private final HashMap<UUID, Integer> gambleCounts;
    private final HashMap<UUID, List<ItemStack>> playerWins;
    private final HashMap<UUID, List<ItemStack>> playerLosses;
    private final List<Material> bannedItems;

    public MainGUI(Player player, HashMap<UUID, Integer> gambleCounts, HashMap<UUID, List<ItemStack>> playerWins, HashMap<UUID, List<ItemStack>> playerLosses, List<Material> bannedItems) {
        this.player = player;
        this.gambleCounts = gambleCounts;
        this.playerWins = playerWins;
        this.playerLosses = playerLosses;
        this.bannedItems = bannedItems;
        this.mainInventory = Bukkit.createInventory(null, 54, "Kumar");

        // Slot 13: Kumar Oyna
        mainInventory.setItem(13, createMenuItem(Material.DIAMOND, ChatColor.GOLD + "Kumar Oyna", "Kumar etkinliğine katıl"));

        // Slot 31: Kumarda Oynanamayacak Eşyalar
        mainInventory.setItem(31, createMenuItem(Material.BARRIER, ChatColor.RED + "Kumarda Oynanamayacak Eşyalar", "Yasaklı eşyaları gör"));

        // Slot 48: Kazançlar
        mainInventory.setItem(48, createMenuItem(Material.GOLD_INGOT, ChatColor.GREEN + "Kazançlar", "Kazanılan eşyaları gör"));

        // Slot 50: Kayıplar
        mainInventory.setItem(50, createMenuItem(Material.IRON_INGOT, ChatColor.RED + "Kayıplar", "Kaybedilen eşyaları gör"));

        // Slot 53: Yardım
        mainInventory.setItem(53, createMenuItem(Material.BOOK, ChatColor.BLUE + "Yardım", "Bilgi al"));
    }

    private ItemStack createMenuItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    public void open() {
        player.openInventory(mainInventory);
    }

    public void handleClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (clickedItem.getItemMeta() != null && clickedItem.getItemMeta().hasDisplayName()) {
            String displayName = clickedItem.getItemMeta().getDisplayName();
            switch (displayName) {
                case ChatColor.GOLD + "Kumar Oyna":
                    new BetGUI(player, gambleCounts, playerWins, playerLosses, bannedItems).open();
                    break;
                case ChatColor.RED + "Kumarda Oynanamayacak Eşyalar":
                    new BannedItemsGUI(player, bannedItems).open();
                    break;
                case ChatColor.GREEN + "Kazançlar":
                    new WinsGUI(player, playerWins).open();
                    break;
                case ChatColor.RED + "Kayıplar":
                    new LossesGUI(player, playerLosses).open();
                    break;
                case ChatColor.BLUE + "Yardım":
                    new HelpGUI(player).open();
                    break;
                default:
                    break;
            }
        }
        event.setCancelled(true);
    }
}
