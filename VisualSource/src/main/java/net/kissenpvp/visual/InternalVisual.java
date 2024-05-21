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
import net.kissenpvp.core.api.config.ConfigurationImplementation;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.event.EventListener;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.paper.api.networking.client.entity.PaperPlayerClient;
import net.kissenpvp.paper.api.user.rank.Rank;
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
import net.kissenpvp.visual.theme.playersettings.*;
import net.kissenpvp.visual.theme.settings.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;

/**
 * Main class for the Visual plugin in Kissen.
 *
 * <p>The {@code Visual} class extends {@link JavaPlugin} and handles the initialization and setup of various components
 * such as tab rendering, chat rendering, and event listeners. It also registers player and system settings, translations,
 * and provides join and quit messages.</p>
 *
 * @see JavaPlugin
 */
public class InternalVisual extends JavaPlugin implements Visual {

    @Override
    public void onEnable() {
        KissenTabRender kissenTabRender = new KissenTabRender();
        KissenChatRenderer kissenChatRenderer = new KissenChatRenderer();
        PluginManager pluginManager = getServer().getPluginManager();

        // listener
        EventListener<VisualChangeEvent> visualChangeEvent = (event) -> kissenTabRender.update();
        EventListener<AsyncChatEvent> chatEvent = (event) -> event.renderer(kissenChatRenderer);
        EventListener<PlayerJoinEvent> joinEvent = (event) -> event.joinMessage(getMessage(true, event.getPlayer()));
        EventListener<PlayerQuitEvent> quitEvent = (event) -> event.quitMessage(getMessage(false, event.getPlayer()));
        pluginManager.registerEvents(visualChangeEvent, this);
        pluginManager.registerEvents(chatEvent, this);
        pluginManager.registerEvents(joinEvent, this);
        pluginManager.registerEvents(quitEvent, this);
        pluginManager.registerEvents(new KissenSystemMessageListener(), this);

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

        // settings
        pluginManager.registerSetting(new DefaultSystemPrefix(), this);
        pluginManager.registerSetting(new DefaultPrimaryColor(), this);
        pluginManager.registerSetting(new DefaultSecondaryColor(), this);
        pluginManager.registerSetting(new GeneralColor(), this);
        pluginManager.registerSetting(new DefaultEnabledColor(), this);
        pluginManager.registerSetting(new DefaultDisabledColor(), this);

        // localization
        String split = " " + "-".repeat(20) + " ";
        pluginManager.registerTranslation("visual.tab.header", new MessageFormat("- {0} - \nOnline Players: {1}/{2}\n" + split + "\n"), this);
        pluginManager.registerTranslation("visual.tab.footer", new MessageFormat("\n " + split + "\nYour Ping: {0}ms"), this);
    }

    @Override
    public @NotNull VisualRank getRankData(@NotNull Rank rank) {
        if (!(rank instanceof Savable<?> savable)) {
            return new KissenVisualRankFallBack(rank);
        }
        return new KissenVisualRank(rank);
    }

    @Override
    public <T extends ServerEntity> @NotNull VisualEntity<T> getEntity(@NotNull T entity) {
        if (entity instanceof Player player) {
            return (VisualEntity<T>) player.getUser().getStorage().computeIfAbsent("visual_data", (key) -> new KissenVisualPlayer(player));
        }
        return new KissenVisualEntity<>(entity);
    }

    /**
     * Retrieves the prefix for a given {@link ServerEntity} with personalized content.
     *
     * <p>The {@code getPrefix} method generates a formatted prefix based on the primary and secondary accent colors from the
     * theme of the provided {@link ServerEntity}. The personalized content is obtained through the {@link #getPersonalisedPrefix(ServerEntity)}
     * method, and a gradient template is applied using the primary and secondary accent colors.</p>
     *
     * @param serverEntity the {@link ServerEntity} for which the prefix is generated
     * @return a {@link Component} representing the formatted prefix
     * @throws NullPointerException if the provided {@link ServerEntity} is `null`
     * @see #getPersonalisedPrefix(ServerEntity)
     */
    public @NotNull Component getPrefix(@NotNull ServerEntity serverEntity) {
        MiniMessage miniMessage = MiniMessage.miniMessage();

        VisualEntity<?> entity = getEntity(serverEntity);

        String primary = entity.getTheme().getPrimaryAccentColor().asHexString();
        String secondary = entity.getTheme().getSecondaryAccentColor().asHexString();
        String gradientTemplate = "<gradient:%s:%s>%s</gradient>";
        String prefix = gradientTemplate.formatted(primary, secondary, getPersonalisedPrefix(serverEntity));

        return MiniMessage.miniMessage().deserialize(prefix);
    }

    /**
     * Retrieves the personalized prefix for a given {@link ServerEntity}.
     *
     * <p>The {@code getPersonalisedPrefix} method checks if the provided {@link ServerEntity} is an instance of
     * {@link PlayerClient} and retrieves the user-specific prefix using the {@link KissenSystemPrefix} user setting.
     * If not a player, the default system prefix is obtained from the configuration.</p>
     *
     * @param serverEntity the {@link ServerEntity} for which the personalized prefix is retrieved
     * @return a {@link String} representing the personalized prefix
     * @throws NullPointerException if the provided {@link ServerEntity} is `null`
     * @see PlayerClient
     * @see KissenSystemPrefix
     * @see DefaultSystemPrefix
     */
    private @NotNull String getPersonalisedPrefix(@NotNull ServerEntity serverEntity) {
        if (serverEntity instanceof PaperPlayerClient player) {
            return player.getSetting(KissenSystemPrefix.class).getValue();
        }
        return Bukkit.getKissen().getImplementation(ConfigurationImplementation.class).getSetting(DefaultSystemPrefix.class);
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
    private @NotNull Component getMessage(boolean join, @NotNull Player player) {
        return Component.translatable("multiplayer.player." + (join ? "joined":"left"), getEntity(player).styledName());
    }
}
