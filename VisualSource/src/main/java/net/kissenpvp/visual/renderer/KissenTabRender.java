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

package net.kissenpvp.visual.renderer;

import net.kissenpvp.visual.InternalVisual;
import net.kissenpvp.visual.api.entity.VisualEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Class for custom rendering of player tab headers and footers in Kissen.
 *
 * <p>The {@code KissenTabRender} class initializes the rendering of player tab headers and footers
 * using MiniMessage for formatting and updating every second for all online players.</p>
 *
 * @see MiniMessage
 * @see Component
 * @see TranslatableComponent
 */
public class KissenTabRender {

    /**
     * Constructs a new instance of KissenTabRender and initializes the tab rendering.
     *
     * <p>The constructor uses MiniMessage for message formatting and sets up a scheduler task to update
     * player tab headers and footers for all online players every second.</p>
     */
    public KissenTabRender() {
        final MiniMessage miniMessage = MiniMessage.miniMessage();
        final TranslatableComponent header = Component.translatable("visual.tab.header");
        final TranslatableComponent footer = Component.translatable("visual.tab.footer");
        final InternalVisual internalVisual = InternalVisual.getPlugin(InternalVisual.class);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(InternalVisual.getPlugin(InternalVisual.class), () -> Bukkit.getOnlinePlayers().forEach(player -> {
            VisualEntity<?> visualPlayer = internalVisual.getEntity(player);
            String primary = visualPlayer.getTheme().getPrimaryAccentColor().asHexString();
            String secondary = visualPlayer.getTheme().getSecondaryAccentColor().asHexString();

            Component prefix = internalVisual.getPrefix(player);

            Component[] headerArguments = {prefix, Component.text(Bukkit.getOnlinePlayers().size()), Component.text(Bukkit.getMaxPlayers())};
            Component[] footerArguments = {Component.text(player.getPing())};

            player.sendPlayerListHeader(visualPlayer.getTheme().style(header.arguments(headerArguments)));
            player.sendPlayerListFooter(visualPlayer.getTheme().style(footer.arguments(footerArguments)));
        }), 0, 20);
    }

    public void update() {
        setTab();
    }

    private void setTab() {
        InternalVisual internalVisual = InternalVisual.getPlugin(InternalVisual.class);
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        for (Player current : Bukkit.getOnlinePlayers()) {
            prepareTeam(internalVisual.getEntity(current), generateTeam(current, scoreboard)).addPlayer(current);
            current.setScoreboard(scoreboard);
        }
    }

    /**
     * Generates a team based on the specified player and scoreboard.
     *
     * <p>The {@code generateTeam} method creates a team with a unique identifier based on the player's rank priority.</p>
     *
     * @param current    the {@link Player} for whom the team is generated
     * @param scoreboard the {@link Scoreboard} to which the team belongs
     * @return the generated {@link Team}
     * @throws NullPointerException if either the player or scoreboard is `null`
     * @see Player
     * @see Team
     * @see Scoreboard
     */
    @Contract(value = "_, _ -> new", mutates = "param2")
    private @NotNull Team generateTeam(@NotNull Player current, @NotNull Scoreboard scoreboard) {
        int priority = current.getRank().getSource().getPriority();
        return scoreboard.registerNewTeam(priority + current.getUniqueId().toString());
    }

    /**
     * Initializes the specified team with customized prefix, suffix, and color based on the player's rank and settings.
     *
     * <p>The {@code initializeTeam} method sets the prefix, suffix, and color for the team based on the player's rank and settings.</p>
     *
     * @param current the {@link Player} for whom the team is initialized
     * @param team    the {@link Team} to be initialized
     * @return the initialized {@link Team}
     * @throws NullPointerException if either the player or team is `null`
     * @see Player
     * @see Team
     */
    @Contract(value = "_, _ -> param2", mutates = "param2")
    private @NotNull Team prepareTeam(@NotNull VisualEntity<?> current, @NotNull Team team) {
        Component space = Component.space();
        current.getPrefixComponent().map(Component::appendSpace).ifPresent(team::prefix);
        current.getSuffixComponent().map(suffix -> Component.space().append(suffix)).ifPresent(team::suffix);
        team.color(NamedTextColor.nearestTo(current.getNameColor()));
        return team;
    }

    /**
     * Retrieves the last color in a given component.
     *
     * <p>The {@code getLastColor} method extracts the last color in the component, considering nested components.
     * It is used to determine the nearest named text color for styling purposes.</p>
     *
     * @param component the {@link Component} from which to retrieve the last color
     * @return an {@link Optional} containing the nearest named text color, or empty if no color is found
     * @throws NullPointerException if the component is `null`
     * @see Component
     * @see NamedTextColor
     */
    private @NotNull Optional<NamedTextColor> getLastColor(@NotNull Component component) {
        TextColor namedTextColor = component.color();
        if (!component.children().isEmpty()) {
            namedTextColor = component.children().get(component.children().size() - 1).color();
        }
        return Optional.ofNullable(namedTextColor).map(NamedTextColor::nearestTo);
    }
}
