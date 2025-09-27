package net.corwis.kissenpvp;

import net.corwis.kissenpvp.theme.DefaultTheme;
import net.kissenpvp.api.event.SystemMessageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NonNull;

public class SystemMessageListener implements Listener
{
    @EventHandler
    public void onSystemMessage(@NonNull SystemMessageEvent event)
    {
        DefaultTheme defaultTheme = new DefaultTheme();
        event.setMessage(defaultTheme.style(event.getMessage()));
    }

}
