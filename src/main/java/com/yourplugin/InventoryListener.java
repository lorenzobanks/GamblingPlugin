package com.yourplugin;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public class InventoryListener implements Listener {

    private final GamblingPlugin plugin;

    public InventoryListener(GamblingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        InventoryView view = event.getView();
        if (view.title().equals(Component.text(plugin.getGuiTitle("bet"))) || view.title().equals(Component.text(plugin.getGuiTitle("spin"))) || view.title().equals(Component.text(plugin.getGuiTitle("main")))) {
            if (event.getClickedInventory() != null && event.getClickedInventory().equals(view.getBottomInventory())) {
                event.setCancelled(true);
            } else {
                if (view.title().equals(Component.text(plugin.getGuiTitle("bet")))) {
                    new KumarOynaGUI((Player) event.getWhoClicked()).handleClick(event);
                } else if (view.title().equals(Component.text(plugin.getGuiTitle("spin")))) {
                    new SpinGUI((Player) event.getWhoClicked(), view.getTopInventory()).handleClick(event);
                } else if (view.title().equals(Component.text(plugin.getGuiTitle("main")))) {
                    new MainGUI((Player) event.getWhoClicked(), plugin.getPlayerWins(), plugin.getPlayerLosses(), plugin.getBannedItems()).handleClick(event);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        InventoryView view = event.getView();
        if (view.title().equals(Component.text(plugin.getGuiTitle("bet")))) {
            new KumarOynaGUI((Player) event.getWhoClicked()).handleDrag(event);
        }
    }
}