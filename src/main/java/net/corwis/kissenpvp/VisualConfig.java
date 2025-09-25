package net.corwis.kissenpvp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.jspecify.annotations.NonNull;

public class VisualConfig {

    private final FileConfiguration config;
    private Component header, footer;

    private boolean mentionsEnabled;
    private boolean mentionSoundEnabled;

    private boolean loaded;

    public VisualConfig() {
        config = Visual.getProvidingPlugin(Visual.class).getConfig();
        loaded = false;
    }

    public void loadConfig() {
        MiniMessage miniMessage = MiniMessage.miniMessage();

        String headerString = config.getString("visuals.header", "<green>Willkommen auf KissenPvP!");
        String footerString = config.getString("visuals.footerString", "<gray>Du bist <b>Spieler</b>.");

        header = miniMessage.deserialize(headerString);
        footer = miniMessage.deserialize(footerString);

        mentionsEnabled = config.getBoolean("visuals.mention-enabled", true);
        mentionSoundEnabled = config.getBoolean("visuals.mention-sound", true);

        loaded = true;
    }

    public @NonNull Component header() throws IllegalStateException
    {
        verify();

        return header;
    }

    public @NonNull Component footer() throws IllegalStateException
    {
        verify();

        return footer;
    }

    public boolean mentionsEnabled() throws IllegalStateException
    {
        verify();

        return mentionsEnabled;
    }

    public boolean mentionSoundEnabled() throws IllegalStateException
    {
        verify();

        return mentionSoundEnabled;
    }

    private void verify()
    {
        if(!loaded)
        {
            throw new IllegalStateException("Config not loaded yet!");
        }
    }
}
