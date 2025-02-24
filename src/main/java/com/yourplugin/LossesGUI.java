package com.yourplugin;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LossesGUI {

    private final Inventory lossesInventory;
    private final Player player;

    public LossesGUI(@NotNull Player player, @NotNull HashMap<UUID, List<ItemStack>> allPlayerLosses) {
        this.player = player;
        List<ItemStack> playerLosses = allPlayerLosses.getOrDefault(player.getUniqueId(), List.of());
        this.lossesInventory = Bukkit.createInventory(null, 54, Component.text("Kayıplar"));

        // Kaybedilen eşyaları göster
        for (int i = 0; i < playerLosses.size() && i < 54; i++) {
            lossesInventory.setItem(i, playerLosses.get(i));
        }
    }

    public void open() {
        player.openInventory(lossesInventory);
    }
}