package net.corwis.kissenpvp.renderer;

import io.papermc.paper.chat.ChatRenderer;
import net.corwis.kissenpvp.Visual;
import net.corwis.kissenpvp.VisualConfig;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class VisualChatRenderer implements ChatRenderer {

    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        VisualConfig config = ((Visual) Visual.getProvidingPlugin(Visual.class)).config();
        Component out = message;

        if (config.mentionsEnabled() && viewer instanceof Player target && !Objects.equals(source, target)) {

            Component altered = out.replaceText(builder -> {
                builder.match(target.getName());
                builder.replacement(Component.text("@" + target.getName()).color(NamedTextColor.YELLOW));
            });

            if (!altered.equals(out)) {
                out = altered;
                if (config.mentionSoundEnabled())
                {
                    target.playSound(target, Sound.ENTITY_ARROW_HIT_PLAYER, .5f, 1f);
                }
            }
        }

        return out;
    }
}
