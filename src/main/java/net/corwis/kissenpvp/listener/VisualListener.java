package net.corwis.kissenpvp.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.corwis.kissenpvp.main.VisualDataFactory;
import net.corwis.kissenpvp.utils.VisualChatRenderer;
import net.corwis.kissenpvp.utils.VisualManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class VisualListener implements Listener {

    private final VisualManager visualManager;
    private final VisualDataFactory factory;

    public VisualListener(VisualManager manager, VisualDataFactory factory) {
        this.visualManager = manager;
        this.factory = factory;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        visualManager.update(event.getPlayer(), factory.fromConfig());
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (factory.getConfig().getBoolean("chat.enabled", true)) {
            event.renderer(new VisualChatRenderer(visualManager, factory.getConfig()));
        }
    }
}
