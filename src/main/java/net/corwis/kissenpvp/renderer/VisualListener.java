package net.corwis.kissenpvp.renderer;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NonNull;

public class VisualListener implements Listener {

    private final static ChatRenderer RENDERER = new VisualChatRenderer();

    @EventHandler
    public void onChat(@NonNull AsyncChatEvent event) { event.renderer(RENDERER); }
}
