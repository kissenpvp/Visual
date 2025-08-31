package net.corwis.kissenpvp;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public final class Visual extends JavaPlugin implements Listener {

    private VisualManager visualManager;

    @Override
    public void onEnable() {
        this.visualManager = new VisualManager();
        getServer().getPluginManager().registerEvents(this, this);

        MiniMessage mm = MiniMessage.miniMessage();
        for (Player player : Bukkit.getOnlinePlayers()) {
            visualManager.update(player, new VisualData(
                    mm.deserialize("<gray>[<gradient:gray:dark_gray>Spieler</gradient>] "),
                    Component.empty(),
                    100,
                    mm.deserialize("<green>Willkommen auf KissenPvP!"),
                    mm.deserialize("<gray>Du bist <b>Spieler</b>.")
            ));
        }
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            visualManager.remove(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        MiniMessage mm = MiniMessage.miniMessage();
        visualManager.update(event.getPlayer(), new VisualData(
                mm.deserialize("<gray>[<gradient:gray:dark_gray>Spieler</gradient>] "),
                Component.empty(),
                100,
                mm.deserialize("<green>Willkommen auf KissenPvP!"),
                mm.deserialize("<gray>Du bist <b>Spieler</b>.")
        ));
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        // Renderer setzt Rank (Prefix) + Name + Suffix + Nachricht und verhindert Self-Pings
        event.renderer(new VisualChatRenderer(this.visualManager));
    }

    public VisualManager getVisualManager() {
        return visualManager;
    }

    @Override
    public @NotNull Path getDataPath() {
        return super.getDataPath();
    }
}
