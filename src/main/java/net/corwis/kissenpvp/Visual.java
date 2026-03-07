package net.corwis.kissenpvp;

import net.corwis.kissenpvp.renderer.VisualListener;
import net.corwis.kissenpvp.renderer.TabListManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import org.jetbrains.annotations.Contract;

import java.util.Optional;

public final class Visual extends JavaPlugin implements Listener {

    private static final GsonComponentSerializer serializer = GsonComponentSerializer.gson();
    private VisualConfig visualConfig;
    private TabListManager tabListManager;

    public @NonNull VisualConfig config()
    {
        return visualConfig;
    }

    public static GsonComponentSerializer serializer() {
        return serializer;
    }

    @Override
    public void onEnable() {
        visualConfig = new VisualConfig();
        tabListManager = new TabListManager();
        saveDefaultConfig();

        visualConfig.loadConfig();

        // Events
        getServer().getPluginManager().registerEvents(new VisualListener(), this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onJoin(@NonNull PlayerJoinEvent event)
    {
        tabListManager.decorate(visualConfig.header(), visualConfig.footer());
    }

    @Contract("_ -> new")
    public @NonNull VisualPlayer playerData(@NonNull Player player)
    {
        return new VisualPlayer(Component.text("Player").appendSpace(), Optional.empty(), 0);
    }

    public record VisualPlayer(@NonNull Component prefix, @NonNull Optional<Component> suffix, int priority)
    {}
}
