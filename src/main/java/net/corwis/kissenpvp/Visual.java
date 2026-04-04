package net.corwis.kissenpvp;

import net.corwis.kissenpvp.renderer.VisualMessageListener;
import net.corwis.kissenpvp.renderer.TabListManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public final class Visual extends JavaPlugin implements Listener {

    private static final GsonComponentSerializer serializer = GsonComponentSerializer.gson();
    private static final Logger log = LoggerFactory.getLogger(Visual.class);
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

        try {
            createTables();
        }
        catch (SQLException sqlException)
        {
            log.error("Could not create the required tables for the visual plugin.", sqlException);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        visualConfig = new VisualConfig();
        tabListManager = new TabListManager();
        saveDefaultConfig();

        visualConfig.loadConfig();

        // Events
        getServer().getPluginManager().registerEvents(new VisualMessageListener(), this);
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
        Optional<Component> prefix = Optional.of(Component.text("Player |", NamedTextColor.GRAY).appendSpace());
        return new VisualPlayer(prefix, Optional.empty(), 0);
    }

    private void createTables() throws SQLException
    {
        DataSource dataSource = Bukkit.getPulvinar().connectionProvider().dataSource().orElseThrow();
        Connection connection = dataSource.getConnection();
        // TODO get public table name

        String ksviRank = "CREATE TABLE IF NOT EXISTS ksvi_visual_rank (id VARCHAR(20) NOT NULL, prefix JSON NOT NULL, suffix JSON NULL DEFAULT NULL, color INT NOT NULL, PRIMARY KEY (id), FOREIGN KEY (id) REFERENCES kissenpvp.ksvp_rank(id));";
        try(PreparedStatement statement = connection.prepareStatement(ksviRank)) { statement.executeUpdate(); }

        String ksviSuffix = "CREATE TABLE IF NOT EXISTS ksvi_visual_suffix(id VARCHAR(20) NOT NULL, player_id UUID NOT NULL, content JSON NOT NULL, PRIMARY KEY(id, player_id), FOREIGN KEY (player_id) REFERENCES kissenpvp.ksvp_player(id));";
        try(PreparedStatement statement = connection.prepareStatement(ksviSuffix)) { statement.executeUpdate(); }

        String ksviSuffixSubscription = "CREATE TABLE IF NOT EXISTS ksvi_visual_suffix_subscription(suffix_id VARCHAR(20) NOT NULL, player_id UUID NOT NULL, PRIMARY KEY(suffix_id, player_id), FOREIGN KEY (suffix_id) REFERENCES ksvi_visual_suffix(id), FOREIGN KEY (player_id) REFERENCES kissenpvp.ksvp_player(id));";
        try(PreparedStatement statement = connection.prepareStatement(ksviSuffixSubscription)) { statement.executeUpdate(); }
    }

    public record VisualPlayer(@NonNull Optional<Component> prefix, @NonNull Optional<Component> suffix, int priority)
    {}
}
