package com.yourplugin;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BannedItemsGUI {

    private final Inventory bannedItemsInventory;
    private final Player player;

    public BannedItemsGUI(@NotNull Player player, @NotNull List<Material> bannedItems) {
        this.player = player;
        this.bannedItemsInventory = Bukkit.createInventory(null, 54, Component.text("Yasaklı Eşyalar"));

        // Yasaklı eşyaları göster
        for (int i = 0; i < bannedItems.size() && i < 54; i++) {
            bannedItemsInventory.setItem(i, new ItemStack(bannedItems.get(i)));
        }
    }

    public void open() {
        player.openInventory(bannedItemsInventory);
    }
}