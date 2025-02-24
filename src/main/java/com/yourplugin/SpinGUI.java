package com.yourplugin;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SpinGUI {

    private final Inventory spinInventory;
    private final Player player;
    private final Inventory betInventory;
    private final GamblingPlugin plugin;
    private final Random random = new Random();

    public SpinGUI(@NotNull Player player, @NotNull Inventory betInventory) {
        this.player = player;
        this.betInventory = betInventory;
        this.plugin = GamblingPlugin.getPlugin(GamblingPlugin.class);
        this.spinInventory = Bukkit.createInventory(null, 54, Component.text(plugin.getGuiTitle("spin")));

        // Spin itemlerini oluştur
        for (int i = 0; i < 54; i++) {
            spinInventory.setItem(i, new ItemStack(Material.RED_STAINED_GLASS_PANE)); // Kırmızı ince cam
        }
    }

    public void open() {
        player.openInventory(spinInventory);
        startSpinAnimation();
    }

    private void startSpinAnimation() {
        new BukkitRunnable() {
            private int iterations = 0;

            @Override
            public void run() {
                if (iterations >= 20) { // 10 saniye boyunca animasyon
                    this.cancel();
                    finalizeSpin();
                    return;
                }

                // Rastgele "KAZANDIN" ve "KAYBETTİN" itemleri arasında geçiş yap
                for (int i = 0; i < 54; i++) {
                    Material material = (random.nextBoolean()) ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
                    spinInventory.setItem(i, new ItemStack(material));
                }

                iterations++;
            }
        }.runTaskTimer(plugin, 0, 10); // 10 tick aralıklarla (0.5 saniye)
    }

    private void finalizeSpin() {
        boolean win = random.nextInt(100) < 35; // %35 kazanma şansı

        if (win) {
            player.sendMessage("Tebrikler, kazandınız!");
            // Kazanılan eşyaları ekle
            for (ItemStack item : betInventory.getContents()) {
                if (item != null) {
                    ItemStack doubledItem = item.clone();
                    doubledItem.setAmount(item.getAmount() * 2);
                    player.getInventory().addItem(doubledItem);
                    plugin.getPlayerWins().computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(doubledItem);
                }
            }
            showWinOptions();
        } else {
            player.sendMessage("Kaybettiniz.");
            // Kaybedilen eşyaları ekle
            for (ItemStack item : betInventory.getContents()) {
                if (item != null) {
                    plugin.getPlayerLosses().computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(item);
                }
            }
        }
    }

    private void showWinOptions() {
        // Yeni GUI ile "Topla" ve "Risk Al" seçeneklerini göster
        Inventory winOptionsInventory = Bukkit.createInventory(null, 54, Component.text("Kazandınız! Seçenekler"));

        ItemStack collectItem = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta collectMeta = collectItem.getItemMeta();
        if (collectMeta != null) {
            collectMeta.displayName(Component.text("Topla"));
            collectItem.setItemMeta(collectMeta);
        }

        ItemStack riskItem = new ItemStack(Material.DIAMOND_BLOCK);
        ItemMeta riskMeta = riskItem.getItemMeta();
        if (riskMeta != null) {
            riskMeta.displayName(Component.text("Risk Al"));
            riskItem.setItemMeta(riskMeta);
        }

        winOptionsInventory.setItem(22, collectItem); // Orta slot
        winOptionsInventory.setItem(31, riskItem); // Orta alt slot

        player.openInventory(winOptionsInventory);
    }

    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true); // Eşyaların alınmasını engellemek için eventi iptal et

        if (event.getClickedInventory() == spinInventory) {
            return;
        }

        if (event.getView().title().toString().equals("Kazandınız! Seçenekler")) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            if (clickedItem.getItemMeta() != null) {
                Component displayNameComponent = clickedItem.getItemMeta().displayName();
                if (displayNameComponent != null) {
                    String displayName = displayNameComponent.toString();

                    if (displayName.equals("Topla")) {
                        collectWinnings();
                    } else if (displayName.equals("Risk Al")) {
                        riskItAll();
                    }
                }
            }
        }
    }

    private void collectWinnings() {
        // Ödülleri topla ve GUI'yi kapat
        player.sendMessage("Kazandığınız ödülleri topladınız!");
        player.closeInventory();
    }

    private void riskItAll() {
        // Risk alarak tekrar spin yap
        new SpinGUI(player, betInventory).open();
    }
}