package net.corwis.kissenpvp;

import net.corwis.kissenpvp.chat.VisualListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

public final class Visual extends JavaPlugin {

    private final VisualConfig visualConfig;

    public Visual() {
        visualConfig = new VisualConfig();
    }

    public @NonNull VisualConfig config()
    {
        return visualConfig;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        visualConfig.loadConfig();

        // Events
        getServer().getPluginManager().registerEvents(new VisualListener(), this);
    }
}
