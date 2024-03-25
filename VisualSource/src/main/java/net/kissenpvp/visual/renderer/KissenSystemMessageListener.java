package net.kissenpvp.visual.renderer;

import net.kissenpvp.core.api.event.EventClass;
import net.kissenpvp.core.api.event.EventListener;
import net.kissenpvp.core.api.message.event.SystemMessageEvent;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.visual.entity.KissenVisualEntity;
import net.kissenpvp.visual.Visual;
import net.kissenpvp.visual.playersettings.KissenShowPrefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Event listener implementation for handling system messages in Kissen.
 *
 * <p>The {@code KissenSystemMessageListener} class implements the {@link EventListener} interface for handling
 * {@link SystemMessageEvent}s. It overrides the {@link EventListener#call(EventClass)}} method to customize
 * the behavior of system messages. The class utilizes the {@link #hidePrefix(SystemMessageEvent, Player, ServerEntity)}
 * and {@link Visual#getPrefix(ServerEntity)} methods to determine prefix visibility and generate the prefix, respectively.</p>
 *
 * @see EventListener
 * @see SystemMessageEvent
 * @see #hidePrefix(SystemMessageEvent, Player, ServerEntity)
 * @see Visual#getPrefix(ServerEntity)
 */
public class KissenSystemMessageListener implements EventListener<SystemMessageEvent> {

    private static @NotNull KissenVisualEntity<?> getVisualData(ServerEntity receiver) {
        return Visual.getPlugin(Visual.class).getEntity(receiver);
    }

    @Override
    public void call(@NotNull SystemMessageEvent event) {

        ServerEntity receiver = event.getReceiver();
        if (event.getReceiver() instanceof Player player && hidePrefix(event, player, receiver)) {
            return;
        }

        Component prefix = Visual.getPlugin(Visual.class).getPrefix(receiver).appendSpace().append(Component.text("»")).appendSpace();
        event.setComponent(getVisualData(receiver).getTheme().style(prefix.append(event.getComponent())));
    }

    /**
     * Determines whether to hide the prefix in a system message for a given player and server entity.
     *
     * <p>The {@code hidePrefix} method checks if the user setting for showing prefixes is enabled for the specified player
     * and ensures that the system message does not contain a newline character. If the prefix should be hidden, it styles the
     * system message component using the styling from the receiver's theme.</p>
     *
     * @param systemMessageEvent the {@link SystemMessageEvent} containing the system message and event details
     * @param player             the {@link Player} for whom the prefix visibility is determined
     * @param receiver           the {@link ServerEntity} receiving the system message
     * @return {@code true} if the prefix should be hidden, {@code false} otherwise
     * @throws NullPointerException if any of the parameters are `null`
     * @see KissenShowPrefix
     */
    private boolean hidePrefix(@NotNull SystemMessageEvent systemMessageEvent, @NotNull Player player, @NotNull ServerEntity receiver) {
        boolean showPrefix = player.getSetting(KissenShowPrefix.class).getValue() && !systemMessageEvent.getComponent().contains(Component.newline());
        if (!showPrefix) {
            systemMessageEvent.setComponent(getVisualData(receiver).getTheme().style(systemMessageEvent.getComponent()));
            return true;
        }
        return false;
    }
}
