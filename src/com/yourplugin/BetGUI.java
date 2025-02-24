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

public class BetGUI {

    private final Inventory betInventory;
    private final Player player;
    private final HashMap<UUID, Integer> gambleCounts;
    private final HashMap<UUID, List<ItemStack>> playerWins;
    private final HashMap<UUID, List<ItemStack>> playerLosses;
    private final List<Material> bannedItems;

    public BetGUI(Player player, HashMap<UUID, Integer> gambleCounts, HashMap<UUID, List<ItemStack>> playerWins, HashMap<UUID, List<ItemStack>> playerLosses, List<Material> bannedItems) {
        this.player = player;
        this.gambleCounts = gambleCounts;
        this.playerWins = playerWins