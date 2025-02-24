package com.yourplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KumarOynaGUI {

    private final Inventory kumarInventory;
    private final Player player;

    public KumarOynaGUI(@NotNull Player player) {
        this.player = player;
        this.kumarInventory = Bukkit.createInventory(null, 54, Component.text("Bahis Yap"));

        // Bahis yapma ve onaylama itemlerini ekle
        addConfirmMenuItem();
    }

    private void addConfirmMenuItem() {
        kumarInventory.setItem(49, createConfirmMenuItem());
    }

    private ItemStack createConfirmMenuItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Component displayName = LegacyComponentSerializer.legacyAmpersand().deserialize(ChatColor.GREEN + "Onayla");
            meta.displayName(displayName);
            List<Component> loreComponents = Stream.of("Bahisinizi onaylayın")
                    .map(line -> LegacyComponentSerializer.legacyAmpersand().deserialize(line))
                    .collect(Collectors.toList());
            meta.lore(loreComponents);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void open() {
        player.openInventory(kumarInventory);
    }

    public void handleClick(@NotNull InventoryClickEvent event) {
        if (event.getClickedInventory() == kumarInventory) {
            if (event.getSlot() == 49 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.NETHER_STAR) {
                handleConfirmClick(event);
            } else {
                event.setCancelled(event.getSlot() != 49); // Diğer itemlerin sürüklenmesine izin ver
            }
        }
    }

    private void handleConfirmClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true); // Onayla butonunun alınmasını engelle
        player.sendMessage(ChatColor.GREEN + "Bahis onaylandı, SpinGUI açılıyor...");
        new SpinGUI(player, kumarInventory).open();
    }

    public void handleDrag(@NotNull InventoryDragEvent event) {
        if (event.getInventory() == kumarInventory) {
            event.setCancelled(false); // Eşyaların sürüklenmesine izin ver
        }
    }
}