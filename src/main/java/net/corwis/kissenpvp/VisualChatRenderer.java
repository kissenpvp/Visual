package net.corwis.kissenpvp;

import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class VisualChatRenderer implements ChatRenderer {

    private final VisualManager visualManager;
    private final Visual plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public VisualChatRenderer(VisualManager visualManager, Visual plugin) {
        this.visualManager = visualManager;
        this.plugin = plugin;
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

        Component splitter = mm.deserialize(plugin.getConfig().getString("chat.splitter", "»"));
        Component name = mm.deserialize(plugin.getConfig().getString("chat.name-color", "<aqua>"))
                .append(Component.text(source.getName()));
        Component msg = mm.deserialize(plugin.getConfig().getString("chat.message-color", "<white>"))
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
            String mentionColor = plugin.getConfig().getString("chat.mention-color", "<yellow>");
            boolean playSound = plugin.getConfig().getBoolean("chat.mention-sound", true);

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
