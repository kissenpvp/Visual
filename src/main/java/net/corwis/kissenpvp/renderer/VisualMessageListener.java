package net.corwis.kissenpvp.renderer;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.corwis.kissenpvp.Visual;
import net.corwis.kissenpvp.VisualConfig;
import net.corwis.kissenpvp.theme.DefaultTheme;
import net.kissenpvp.api.event.SystemMessageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NonNull;

public class VisualMessageListener implements Listener
{
    private final static ChatRenderer RENDERER = new VisualChatRenderer();

    private static VisualConfig visualConfig;
    static {
        visualConfig = ((Visual) Visual.getProvidingPlugin(Visual.class)).config();
    }

    @EventHandler
    public void onSystemMessage(@NonNull SystemMessageEvent event)
    {
        DefaultTheme defaultTheme = new DefaultTheme();
        event.setMessage(visualConfig.systemPrefix().append(defaultTheme.style(event.getMessage())));
    }

    @EventHandler
    public void onChat(@NonNull AsyncChatEvent event) { event.renderer(RENDERER); }
}
