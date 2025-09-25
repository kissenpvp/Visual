package net.corwis.kissenpvp.main;

import net.corwis.kissenpvp.VisualData;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;

public class VisualDataFactory {

    private final MiniMessage mm;
    private final FileConfiguration cfg;

    public VisualDataFactory(MiniMessage mm, FileConfiguration cfg) {
        this.mm = mm;
        this.cfg = cfg;
    }

    public VisualData fromConfig() {
        String prefix = cfg.getString("visuals.prefix", "<gray>[<gradient:gray:dark_gray>Spieler</gradient>] ");
        String suffix = cfg.getString("visuals.suffix", "");
        int priority = cfg.getInt("visuals.priority", 255);
        String header = cfg.getString("visuals.header", "<green>Willkommen auf KissenPvP!");
        String footer = cfg.getString("visuals.footer", "<gray>Du bist <b>Spieler</b>.");

        return new VisualData(
                mm.deserialize(prefix),
                mm.deserialize(suffix),
                priority,
                mm.deserialize(header),
                mm.deserialize(footer)
        );
    }

    public FileConfiguration getConfig() {
        return cfg;
    }
}
