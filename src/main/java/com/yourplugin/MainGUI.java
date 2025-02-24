package com.yourplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainGUI {

    private final Inventory mainInventory;
    private final Player player;
    private final HashMap<UUID, List<ItemStack>> playerWins;
    private final HashMap<UUID, List<ItemStack>> playerLosses;
    private final List<Material> bannedItems;

    public MainGUI(@NotNull Player player, @NotNull HashMap<UUID, List<ItemStack>> playerWins, @NotNull HashMap<UUID, List<ItemStack>> playerLosses, @NotNull List<Material> bannedItems) {
        this.player = player;
        this.playerWins = playerWins;
        this.playerLosses = playerLosses;
        this.bannedItems = bannedItems;
        GamblingPlugin plugin = JavaPlugin.getPlugin(GamblingPlugin.class);
        this.mainInventory = Bukkit.createInventory(null, 54, Component.text(plugin.getGuiTitle("main")));

        // Menünün itemlerini oluşturmak için ortak metod kullanımı
        addMenuItem(13, Material.DIAMOND, ChatColor.GOLD + "Kumar Oyna", "Kumar etkinliğine katıl");
        addMenuItem(31, Material.BARRIER, "Kumarda Oynanamayacak Eşyalar", "Yasaklı eşyaları gör");
        addMenuItem(48, Material.GOLD_INGOT, "Kazançlar", "Kazanılan eşyaları gör");
        addMenuItem(50, Material.IRON_INGOT, "Kayıplar", "Kaybedilen eşyaları gör");
        addMenuItem(53, Material.BOOK, "Yardım", "Bilgi al");
    }

    private void addMenuItem(int slot, Material material, String name, String... lore) {
        mainInventory.setItem(slot, createMenuItem(material, name, lore));
    }

    private ItemStack createMenuItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Component displayName = LegacyComponentSerializer.legacyAmpersand().deserialize(name);
            meta.displayName(displayName);

            List<Component> loreComponents = Stream.of(lore)
                    .map(line -> LegacyComponentSerializer.legacyAmpersand().deserialize(line))
                    .collect(Collectors.toList());
            meta.lore(loreComponents);

            item.setItemMeta(meta);
        }
        return item;
    }

    public void open() {
        player.openInventory(mainInventory);
    }

    public void handleClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true); // Eşyaların alınmasını engellemek için eventi iptal et

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (clickedItem.getItemMeta() != null) {
            Component displayNameComponent = clickedItem.getItemMeta().displayName();
            if (displayNameComponent != null) {
                String displayName = LegacyComponentSerializer.legacyAmpersand().serialize(displayNameComponent);
                handleItemClick(displayName);
            }
        }
    }

    private void handleItemClick(String displayName) {
        switch (displayName) {
            case "Kumar Oyna":
                new KumarOynaGUI(player).open();
                break;
            case "Kumarda Oynanamayacak Eşyalar":
                new BannedItemsGUI(player, bannedItems).open();
                break;
            case "Kazançlar":
                new WinsGUI(player, playerWins).open();
                break;
            case "Kayıplar":
                new LossesGUI(player, playerLosses).open();
                break;
            case "Yardım":
                new HelpGUI(player).open();
                break;
            default:
                break;
        }
    }
}