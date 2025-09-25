package net.corwis.kissenpvp.utils;

import io.papermc.paper.chat.ChatRenderer;
import net.corwis.kissenpvp.VisualData;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class VisualChatRenderer implements ChatRenderer {

    private final VisualManager visualManager;
    private final FileConfiguration cfg;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public VisualChatRenderer(VisualManager visualManager, FileConfiguration cfg) {
        this.visualManager = visualManager;
        this.cfg = cfg;
    }

    @Override
    public @NotNull Component render(@NotNull Player source,
                                     @NotNull Component sourceDisplayName,
                                     @NotNull Component message,
                                     @NotNull Audience viewer) {

        VisualData data = visualManager.get(source);
        Component prefix = (data != null && data.prefix() != null) ? data.prefix() : Component.empty();
        Component suffix = (data != null && data.suffix() != null) ? data.suffix() : Component.empty();

        Component splitter = mm.deserialize(cfg.getString("chat.splitter", "»"));
        Component name = mm.deserialize(cfg.getString("chat.name-color", "<aqua>"))
                .append(Component.text(source.getName()));
        Component msg = mm.deserialize(cfg.getString("chat.message-color", "<white>"))
                .append(message);

        Component line = Component.empty()
                .append(prefix)
                .append(name)
                .append(suffix)
                .appendSpace()
                .append(splitter)
                .appendSpace()
                .append(msg);

        if (viewer instanceof Player target && !target.equals(source)) {
            String mentionColor = cfg.getString("chat.mention-color", "<yellow>");
            boolean playSound = cfg.getBoolean("chat.mention-sound", true);

            Component altered = line.replaceText(builder -> {
                builder.match(target.getName());
                builder.replacement(mm.deserialize(mentionColor + "@" + target.getName()));
            });

            if (!altered.equals(line)) {
                line = altered;
                if (playSound) {
                    target.playSound(target, Sound.ENTITY_ARROW_HIT_PLAYER, .5f, 1f);
                }
            }
        }
        return line;
    }
}
