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

public class WinsGUI {

    private final Inventory winsInventory;
    private final Player player;

    public WinsGUI(@NotNull Player player, @NotNull HashMap<UUID, List<ItemStack>> allPlayerWins) {
        this.player = player;
        List<ItemStack> playerWins = allPlayerWins.getOrDefault(player.getUniqueId(), List.of());
        this.winsInventory = Bukkit.createInventory(null, 54, Component.text("Kazançlar"));

        // Kazanılan eşyaları göster
        for (int i = 0; i < playerWins.size() && i < 54; i++) {
            winsInventory.setItem(i, playerWins.get(i));
        }
    }

    public void open() {
        player.openInventory(winsInventory);
    }
}