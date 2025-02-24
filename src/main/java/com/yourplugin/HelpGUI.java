package com.yourplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HelpGUI {

    private final Inventory helpInventory;
    private final Player player;

    public HelpGUI(@NotNull Player player) {
        this.player = player;
        this.helpInventory = Bukkit.createInventory(null, 54, Component.text("Yardım"));

        // Yardım mesajlarını oluştur
        List<Component> helpMessages = Stream.of(
                "Kumar Etkinliği hakkında bilgiler:",
                "1. Etkinlik sırasında bahis yapabilirsiniz.",
                "2. Kazanırsanız ödül alırsınız.",
                "3. Kaybederseniz bahis yaptığınız eşyaları kaybedersiniz.",
                "4. Risk alarak ödülünüzü katlama şansı elde edebilirsiniz."
        ).map(LegacyComponentSerializer.legacyAmpersand()::deserialize).collect(Collectors.toList());

        for (int i = 0; i < helpMessages.size() && i < 54; i++) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(helpMessages.get(i));
                item.setItemMeta(meta);
            }
            helpInventory.setItem(i, item);
        }
    }

    public void open() {
        player.openInventory(helpInventory);
    }
}