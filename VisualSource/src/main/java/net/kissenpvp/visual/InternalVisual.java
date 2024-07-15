/*
 * Copyright (C) 2024 KissenPvP
 *
 * This program is licensed under the Apache License, Version 2.0.
 *
 * This software may be redistributed and/or modified under the terms
 * of the Apache License as published by the Apache Software Foundation,
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the Apache
 * License, Version 2.0 for the specific language governing permissions
 * and limitations under the License.
 *
 * You should have received a copy of the Apache License, Version 2.0
 * along with this program. If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package net.kissenpvp.visual;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.AccessLevel;
import lombok.Getter;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.event.EventListener;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.pulvinar.api.networking.client.entity.PulvinarPlayerClient;
import net.kissenpvp.pulvinar.api.user.rank.Rank;
import net.kissenpvp.pulvinar.api.user.rank.event.AsyncRankExpiredEvent;
import net.kissenpvp.pulvinar.api.user.rank.event.PlayerRankEvent;
import net.kissenpvp.pulvinar.api.user.rank.event.RankEvent;
import net.kissenpvp.pulvinar.api.user.rank.event.RankGrantEvent;
import net.kissenpvp.visual.api.Visual;
import net.kissenpvp.visual.api.entity.VisualEntity;
import net.kissenpvp.visual.api.entity.VisualPlayer;
import net.kissenpvp.visual.api.entity.VisualPlayerParser;
import net.kissenpvp.visual.api.event.VisualChangeEvent;
import net.kissenpvp.visual.api.rank.VisualRank;
import net.kissenpvp.visual.api.rank.VisualRankParser;
import net.kissenpvp.visual.entity.KissenVisualEntity;
import net.kissenpvp.visual.entity.KissenVisualPlayer;
import net.kissenpvp.visual.playersettings.KissenPlayPingSound;
import net.kissenpvp.visual.playersettings.KissenShowPrefix;
import net.kissenpvp.visual.playersettings.KissenSystemPrefix;
import net.kissenpvp.visual.rank.KissenVisualRank;
import net.kissenpvp.visual.rank.KissenVisualRankFallBack;
import net.kissenpvp.visual.rank.RankCommand;
import net.kissenpvp.visual.renderer.KissenChatRenderer;
import net.kissenpvp.visual.renderer.KissenSystemMessageListener;
import net.kissenpvp.visual.renderer.KissenTabRender;
import net.kissenpvp.visual.suffix.KissenSuffixSetting;
import net.kissenpvp.visual.suffix.SuffixCommand;
import net.kissenpvp.visual.theme.DefaultTheme;
import net.kissenpvp.visual.theme.playersettings.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Main class for the Visual plugin in Kissen.
 *
 * <p>The {@code Visual} class extends {@link JavaPlugin} and handles the initialization and setup of various components
 * such as tab rendering, chat rendering, and event listeners. It also registers player and system settings,
 * translations,
 * and provides join and quit messages.</p>
 *
 * @see JavaPlugin
 */

public class InternalVisual extends JavaPlugin implements Visual
{

    @Getter(AccessLevel.PROTECTED) private KissenTabRender tabRender;
    @Getter private String defaultPrefix;

    /**
     * Triggers a visual change event for the specified player.
     * <p>
     * This static method creates a new {@link VisualChangeEvent} for the given {@link Player} and calls the event using
     * Bukkit's event management system. It is intended to be used internally to handle the visual updates for players
     * when their rank changes.
     *
     * @param player the player for whom the visual change event is triggered
     * @throws NullPointerException if {@code player} is null
     */
    private static void visualEvent(@NotNull Player player)
    {
        VisualChangeEvent visualEvent = new VisualChangeEvent(player);
        Bukkit.getPluginManager().callEvent(visualEvent);
    }

    /**
     * Creates an EventListener for PlayerRankEvent subclasses, which triggers a visual event for the player.
     *
     * <p>
     * This method returns an {@link EventListener} that listens for events of the specified class type {@code clazz}
     * that extend {@link PlayerRankEvent}. When such an event is detected, it checks if the event's player rank is
     * associated
     * with a {@link Player}. If the condition is met, it schedules a task to execute the
     * {@link #visualEvent(Player)} method
     * synchronously within the main thread using Bukkit's scheduler.
     * <p>
     * This method leverages generics to ensure type safety, allowing only subclasses of {@code PlayerRankEvent} to
     * be passed
     * to the listener. This pattern is useful for creating modular and reusable event handling mechanisms in Bukkit
     * plugins.
     *
     * @param <T>   the type of {@link PlayerRankEvent} this listener will handle
     * @param clazz the class object representing the type of event this listener should handle
     * @return an {@link EventListener} that handles events of the specified type
     * @throws NullPointerException if {@code clazz} is null
     */
    private <T extends PlayerRankEvent> @NotNull EventListener<T> playerRankEvent(@NotNull Class<T> clazz)
    {
        return (event) ->
        {
            if (clazz.isAssignableFrom(event.getClass()) && event.getPlayerRank().getPlayer() instanceof Player player)
            {
                Bukkit.getScheduler()
                      .runTask(InternalVisual.getPlugin(InternalVisual.class), () -> visualEvent(player));
            }
        };
    }

    @Override public void onEnable()
    {
        tabRender = new KissenTabRender();
        KissenChatRenderer kissenChatRenderer = new KissenChatRenderer();
        PluginManager pluginManager = getServer().getPluginManager();

        // listener
        EventListener<VisualChangeEvent> visualChangeEvent = (event) -> getTabRender().update();
        EventListener<AsyncChatEvent> chatEvent = (event) -> event.renderer(kissenChatRenderer);
        EventListener<PlayerJoinEvent> joinEvent = (event) ->
        {
            getTabRender().update();
            event.joinMessage(getMessage(true, event.getPlayer()));
        };
        EventListener<PlayerQuitEvent> quitEvent = (event) -> event.quitMessage(getMessage(false, event.getPlayer()));
        pluginManager.registerEvents(visualChangeEvent, this);
        pluginManager.registerEvents(chatEvent, this);
        pluginManager.registerEvents(joinEvent, this);
        pluginManager.registerEvents(quitEvent, this);
        pluginManager.registerEvents(new KissenSystemMessageListener(), this);

        // rank events
        EventListener<RankEvent> rankEvent = (event) -> callVisualChangeEvent(event.getRankTemplate());
        pluginManager.registerEvents(rankEvent, this);
        pluginManager.registerEvents(playerRankEvent(AsyncRankExpiredEvent.class), this);
        pluginManager.registerEvents(playerRankEvent(RankGrantEvent.class), this);

        // commands
        pluginManager.registerCommand(this, new SuffixCommand(), new RankCommand());
        pluginManager.registerParser(VisualPlayer.class, new VisualPlayerParser(this), this);
        pluginManager.registerParser(VisualRank.class, new VisualRankParser(this), this);

        // player settings
        pluginManager.registerPlayerSetting(new KissenPlayPingSound(), this);
        pluginManager.registerPlayerSetting(new KissenShowPrefix(), this);
        pluginManager.registerPlayerSetting(new KissenSystemPrefix(), this);
        pluginManager.registerPlayerSetting(new KissenPrimaryUserColor(), this);
        pluginManager.registerPlayerSetting(new KissenSecondaryUserColor(), this);
        pluginManager.registerPlayerSetting(new KissenGeneralUserColor(), this);
        pluginManager.registerPlayerSetting(new KissenEnabledUserColor(), this);
        pluginManager.registerPlayerSetting(new KissenDisabledUserColor(), this);
        pluginManager.registerPlayerSetting(new KissenHighlightVariables(), this);
        pluginManager.registerPlayerSetting(new KissenSuffixSetting(), this);

        File configFile = new File(getDataFolder(), "config.yml");
        if (getDataFolder().mkdirs() || !configFile.exists())
        {
            this.saveDefaultConfig();
        }

        this.defaultPrefix = getConfig().getString("system_prefix");
        DefaultTheme.setDefaultPrimaryColor(getColorValue("primary", NamedTextColor.YELLOW));
        DefaultTheme.setDefaultSecondaryColor(getColorValue("secondary", NamedTextColor.GOLD));
        DefaultTheme.setDefaultGeneralColor(getColorValue("general", NamedTextColor.GRAY));
        DefaultTheme.setDefaultEnabledColor(getColorValue("enabled", NamedTextColor.GREEN));
        DefaultTheme.setDefaultDisabledColor(getColorValue("disabled", NamedTextColor.RED));
        this.saveConfig();

        // localization
        String split = String.format(" %s ", "-".repeat(25));
        MessageFormat header = new MessageFormat("- {0} - \nOnline Players: {1}/{2}\n" + split + "\n");
        pluginManager.registerTranslation("visual.tab.header", header, this);

        MessageFormat footer = new MessageFormat("\n " + split + "\nYour Ping: {0}ms");
        pluginManager.registerTranslation("visual.tab.footer", footer, this);
    }

    @Override public void onDisable()
    {
        getTabRender().shutdown();
    }

    @Override public @NotNull VisualRank getRankData(@NotNull Rank rank)
    {
        if (!(rank instanceof Savable<?> savable))
        {
            return new KissenVisualRankFallBack(rank);
        }
        return new KissenVisualRank(rank);
    }

    @Override public <T extends ServerEntity> @NotNull VisualEntity<T> getEntity(@NotNull T entity)
    {
        if (entity instanceof Player player)
        {
            Map<String, Object> storage = player.getUser().getStorage();
            return (VisualEntity<T>) storage.computeIfAbsent("visual_data", (key) -> new KissenVisualPlayer(player));
        }
        return new KissenVisualEntity<>(entity);
    }

    /**
     * Triggers a visual change event for all online players with the specified rank.
     *
     * <p>This method iterates over all online players and filters them based on the given {@link Rank}.
     * For each player whose rank matches the specified rank, a visual change event is triggered by calling
     * the {@link InternalVisual#visualEvent(Player)} method.</p>
     *
     * <p>The filtering is done using a {@link Predicate} that checks if the player's rank source is equal
     * to the specified rank. This ensures that only players with the exact matching rank will have their
     * visual event triggered.</p>
     *
     * @param rank the rank to filter players by
     * @throws NullPointerException if {@code rank} is null
     */
    public void callVisualChangeEvent(@NotNull Rank rank)
    {
        Predicate<Player> hasRank = player -> Objects.equals(player.getRank().getSource(), rank);
        Bukkit.getOnlinePlayers().stream().filter(hasRank).forEach(InternalVisual::visualEvent);
    }

    /**
     * Retrieves the prefix for a given {@link ServerEntity} with personalized content.
     *
     * <p>The {@code getPrefix} method generates a formatted prefix based on the primary and secondary accent colors
     * from the
     * theme of the provided {@link ServerEntity}. The personalized content is obtained through the
     * {@link #getPersonalisedPrefix(ServerEntity)}
     * method, and a gradient template is applied using the primary and secondary accent colors.</p>
     *
     * @param serverEntity the {@link ServerEntity} for which the prefix is generated
     * @return a {@link Component} representing the formatted prefix
     * @throws NullPointerException if the provided {@link ServerEntity} is `null`
     * @see #getPersonalisedPrefix(ServerEntity)
     */
    public @NotNull Component getPrefix(@NotNull ServerEntity serverEntity)
    {
        MiniMessage miniMessage = MiniMessage.miniMessage();

        VisualEntity<?> entity = getEntity(serverEntity);

        String primary = entity.getTheme().getPrimaryColor().asHexString();
        String secondary = entity.getTheme().getSecondaryColor().asHexString();
        String gradientTemplate = "<gradient:%s:%s>%s</gradient>";
        String prefix = gradientTemplate.formatted(primary, secondary, getPersonalisedPrefix(serverEntity));

        return MiniMessage.miniMessage().deserialize(prefix);
    }

    /**
     * Retrieves the personalized prefix for a given {@link ServerEntity}.
     *
     * <p>The {@code getPersonalisedPrefix} method checks if the provided {@link ServerEntity} is an instance of
     * {@link PlayerClient} and retrieves the user-specific prefix using the {@link KissenSystemPrefix} user
     * setting.
     * If not a player, the default system prefix is obtained from the configuration.</p>
     *
     * @param serverEntity the {@link ServerEntity} for which the personalized prefix is retrieved
     * @return a {@link String} representing the personalized prefix
     * @throws NullPointerException if the provided {@link ServerEntity} is `null`
     * @see PlayerClient
     * @see KissenSystemPrefix
     * @see #getDefaultPrefix()
     */
    private @NotNull String getPersonalisedPrefix(@NotNull ServerEntity serverEntity)
    {
        if (serverEntity instanceof PulvinarPlayerClient player)
        {
            return player.getSetting(KissenSystemPrefix.class).getValue();
        }
        return getDefaultPrefix();
    }

    /**
     * Generates a formatted join or quit message for a player.
     *
     * <p>The {@code getMessage} method creates a formatted join or quit message using the player's styled rank name
     * and the appropriate translation key. The translation key is based on whether the player joined or left.</p>
     *
     * @param join   {@code true} if the player joined, {@code false} if the player left
     * @param player the {@link Player} for whom the message is generated
     * @return a {@link Component} representing the formatted join or quit message
     * @throws NullPointerException if the player is `null`
     * @see Player
     * @see Component
     */
    private @NotNull Component getMessage(boolean join, @NotNull Player player)
    {
        return Component.translatable("multiplayer.player." + (join ? "joined":"left"), getEntity(player).styledName());
    }

    private @NotNull NamedTextColor getColorValue(@NotNull String key, @NotNull NamedTextColor defaultColor)
    {
        String memoryKey = String.format("appearance.%s", key);

        if (getConfig().contains(memoryKey))
        {
            String value = getConfig().getString(String.format("appearance.%s", key));
            NamedTextColor color = NamedTextColor.NAMES.value(value);
            if (Objects.nonNull(color))
            {
                return color;
            }
        }
        getConfig().set(memoryKey, defaultColor);
        return defaultColor;
    }
}
