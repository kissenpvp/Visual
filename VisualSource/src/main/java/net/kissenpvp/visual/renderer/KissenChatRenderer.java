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

import io.papermc.paper.chat.ChatRenderer;
import net.kissenpvp.visual.InternalVisual;
import net.kissenpvp.visual.api.rank.VisualRank;
import net.kissenpvp.visual.playersettings.KissenPlayPingSound;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of the ChatRenderer interface for custom rendering in Kissen.
 *
 * <p>The {@code KissenChatRenderer} class implements the {@link ChatRenderer} interface,
 * providing custom rendering for chat messages in Kissen.</p>
 *
 * @see ChatRenderer
 */
public class KissenChatRenderer implements ChatRenderer {

    @Override
    @Contract(value = "_, _, _, _ -> new")
    public @NotNull Component render(@NotNull Player player, @NotNull Component component, @NotNull Component message, @NotNull Audience audience) {
        InternalVisual internalVisual = InternalVisual.getPlugin(InternalVisual.class);
        VisualRank visualRank = internalVisual.getRankData(player.getRank().getSource());

        Component splitter = Component.text("»").color(NamedTextColor.GRAY);
        Component chatPrefix = internalVisual.getEntity(player).styledName().appendSpace().append(splitter).appendSpace();
        TextColor chatColor = visualRank.getColor();
        return chatPrefix.append(pingPlayer(player, message.color(chatColor)));
    }

    /**
     * Pings online players mentioned in the message and plays a ping sound if configured.
     *
     * <p>The {@code pingPlayer} method iterates through online players, alters the message to mention each player,
     * and plays a ping sound for players with the {@link KissenPlayPingSound} setting enabled.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Player player = // Obtain a Player instance
     * Component message = // Obtain a Component instance
     * Component pingedMessage = pingPlayer(player, message);
     * // Use the pinged message as needed
     * }
     * </pre>
     *
     * @param player  the {@link Player} sending the original message
     * @param message the original message component
     * @return a {@link Component} with mentions and ping sounds applied
     * @throws NullPointerException if either parameter is `null`
     * @see Player
     * @see Component
     * @see KissenPlayPingSound
     */
    @Contract(value = "_, _ -> param2 ", mutates = "param2")
    private @NotNull Component pingPlayer(@NotNull Player player, @NotNull Component message) {
        for (Player current : Bukkit.getOnlinePlayers()) {
            Component altered = alterComponent(message, current);
            if (!altered.equals(message)) {
                message = altered;
                if (!current.getSetting(KissenPlayPingSound.class).getValue()) {
                    continue;
                }
                current.playSound(current, Sound.ENTITY_ARROW_HIT_PLAYER, .5f, 1f);
            }
        }
        return message;
    }

    /**
     * Alters the message to mention a specific player by adding an '@' symbol and changing the text color to yellow.
     *
     * <p>The {@code alterComponent} method replaces occurrences of a player's name in the message with a formatted mention,
     * appending an '@' symbol and changing the text color to yellow.</p>
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * {@code
     * Player current = // Obtain a Player instance
     * Component message = // Obtain a Component instance
     * Component alteredMessage = alterComponent(message, current);
     * // Use the altered message as needed
     * }
     * </pre>
     *
     * @param message the original message component
     * @param current the {@link Player} to be mentioned in the message
     * @return a {@link Component} with mentions altered
     * @throws NullPointerException if either parameter is `null`
     * @see Player
     * @see Component
     */
    @Contract(value = "_, _ -> new")
    private @NotNull Component alterComponent(@NotNull Component message, @NotNull Player current) {
        return message.replaceText(builder -> {
            builder.match(current.getName());
            Component name = Component.text("@" + current.getName());
            builder.replacement(name.color(NamedTextColor.YELLOW));
        });
    }
}
