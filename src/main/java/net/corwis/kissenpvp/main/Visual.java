package net.corwis.kissenpvp.main;

import net.corwis.kissenpvp.listener.VisualListener;
import net.corwis.kissenpvp.utils.VisualManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Visual extends JavaPlugin {

    private VisualManager visualManager;
    private VisualDataFactory visualDataFactory;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        MiniMessage mm = MiniMessage.miniMessage();
        this.visualDataFactory = new VisualDataFactory(mm, getConfig());
        this.visualManager = new VisualManager();

        // Events
        getServer().getPluginManager().registerEvents(new VisualListener(this.visualManager, this.visualDataFactory), this);

        applyVisualsToOnlinePlayers();
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            visualManager.remove(player);
        }
    }

    public VisualManager getVisualManager() {
        return visualManager;
    }

    public VisualDataFactory getVisualDataFactory() {
        return visualDataFactory;
    }

    public void applyVisualsToOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            visualManager.update(player, visualDataFactory.fromConfig());
        }
    }
}
