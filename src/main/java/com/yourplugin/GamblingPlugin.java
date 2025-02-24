package com.yourplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class GamblingPlugin extends JavaPlugin {

    private boolean eventActive = false;
    private final HashMap<UUID, List<ItemStack>> playerWins = new HashMap<>();
    private final HashMap<UUID, List<ItemStack>> playerLosses = new HashMap<>();
    private List<Material> bannedItems;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();
        loadPlayerData();

        Objects.requireNonNull(getCommand("kumarac")).setExecutor(new StartEventCommand());
        Objects.requireNonNull(getCommand("kumarkapa")).setExecutor(new StopEventCommand());
        Objects.requireNonNull(getCommand("kumar")).setExecutor(new MainCommand());
        Objects.requireNonNull(getCommand("kumaroyna")).setExecutor(new KumarOynaCommand());

        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
    }

    @Override
    public void onDisable() {
        savePlayerData();
    }

    private void loadConfigValues() {
        bannedItems = getConfig().getStringList("banned_items").stream()
                .map(Material::matchMaterial)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public String getMessage(String key) {
        return getConfig().getString("messages." + key, "Mesaj bulunamadı");
    }

    public String getGuiTitle(String key) {
        return getConfig().getString("gui_titles." + key, "Başlık bulunamadı");
    }

    public HashMap<UUID, List<ItemStack>> getPlayerWins() {
        return playerWins;
    }

    public HashMap<UUID, List<ItemStack>> getPlayerLosses() {
        return playerLosses;
    }

    public List<Material> getBannedItems() {
        return bannedItems;
    }

    private void savePlayerData() {
        File dataFile = new File(getDataFolder(), "player_data.yml");
        FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        for (UUID playerId : playerWins.keySet()) {
            List<ItemStack> wins = playerWins.get(playerId);
            dataConfig.set("wins." + playerId.toString(), wins);
        }
        for (UUID playerId : playerLosses.keySet()) {
            List<ItemStack> losses = playerLosses.get(playerId);
            dataConfig.set("losses." + playerId.toString(), losses);
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save player data", e);
        }
    }

    private void loadPlayerData() {
        File dataFile = new File(getDataFolder(), "player_data.yml");
        if (!dataFile.exists()) {
            return;
        }

        FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        loadSection(dataConfig, "wins", playerWins);
        loadSection(dataConfig, "losses", playerLosses);
    }

    private void loadSection(FileConfiguration dataConfig, String section, HashMap<UUID, List<ItemStack>> playerData) {
        ConfigurationSection configSection = dataConfig.getConfigurationSection(section);
        if (configSection != null) {
            for (String playerId : configSection.getKeys(false)) {
                List<?> rawList = configSection.getList(playerId);
                if (rawList != null && !rawList.isEmpty()) {
                    playerData.put(UUID.fromString(playerId), rawList.stream()
                            .filter(ItemStack.class::isInstance)
                            .map(ItemStack.class::cast)
                            .collect(Collectors.toList()));
                }
            }
        }
    }

    public class StartEventCommand implements CommandExecutor {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            if (sender.hasPermission("gambling.admin")) {
                eventActive = true;
                Component message = LegacyComponentSerializer.legacyAmpersand().deserialize(getMessage("event_started"));
                Bukkit.broadcast(message);
                return true;
            }
            return false;
        }
    }

    public class StopEventCommand implements CommandExecutor {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            if (sender.hasPermission("gambling.admin")) {
                eventActive = false;
                Component message = LegacyComponentSerializer.legacyAmpersand().deserialize(getMessage("event_stopped"));
                Bukkit.broadcast(message);
                return true;
            }
            return false;
        }
    }

    public class MainCommand implements CommandExecutor {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            if (eventActive) {
                if (sender instanceof Player player) {
                    MainGUI mainGUI = new MainGUI(player, playerWins, playerLosses, bannedItems);
                    mainGUI.open();
                }
                return true;
            } else {
                sender.sendMessage(getMessage("event_closed"));
                return false;
            }
        }
    }

    public class KumarOynaCommand implements CommandExecutor {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            if (eventActive) {
                if (sender instanceof Player player) {
                    KumarOynaGUI kumarOynaGUI = new KumarOynaGUI(player);
                    kumarOynaGUI.open();
                }
                return true;
            } else {
                sender.sendMessage(getMessage("event_closed"));
                return false;
            }
        }
    }
}