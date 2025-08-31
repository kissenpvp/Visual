package net.corwis.kissenpvp;

import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class VisualChatRenderer implements ChatRenderer {

    private final VisualManager visualManager;

    public VisualChatRenderer(VisualManager visualManager) {
        this.visualManager = visualManager;
    }

    @Override
    @Contract(value = "_, _, _, _ -> new")
    public @NotNull Component render(@NotNull Player source,
                                     @NotNull Component sourceDisplayName,
                                     @NotNull Component message,
                                     @NotNull Audience viewer) {

        VisualData data = visualManager.get(source);
        Component prefix = (data != null && data.prefix() != null) ? data.prefix() : Component.empty();
        Component suffix = (data != null && data.suffix() != null) ? data.suffix() : Component.empty();

        final Component splitter = Component.text("»", NamedTextColor.DARK_GRAY);
        final Component name = sourceDisplayName.colorIfAbsent(NamedTextColor.AQUA);

        Component line = Component.empty()
                .append(prefix)
                .append(name)
                .append(suffix)
                .appendSpace()
                .append(splitter)
                .appendSpace()
                .append(message.colorIfAbsent(NamedTextColor.WHITE));

        if (viewer instanceof Player target) {
            if (!target.getUniqueId().equals(source.getUniqueId())) {
                Component altered = line.replaceText(builder -> {
                    builder.match("@" + target.getName());
                    builder.replacement(Component.text("@" + target.getName()).color(NamedTextColor.YELLOW));
                });

                if (!altered.equals(line)) {
                    line = altered;
                    target.playSound(target, Sound.ENTITY_ARROW_HIT_PLAYER, .5f, 1f);
                }
            }
        }

        return line;
    }
}