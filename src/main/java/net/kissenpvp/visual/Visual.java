package net.kissenpvp.visual;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Visual extends JavaPlugin {
    private VisualManager visualManager;

    @Override
    public void onEnable() {
        visualManager = new VisualManager();
        MiniMessage mm = MiniMessage.miniMessage();

        for (Player player : Bukkit.getOnlinePlayers()) {
            visualManager.update(player, new VisualData(
                    mm.deserialize("<gray>[<gradient:gray:dark_gray>Spieler</gradient>] "),
                    mm.deserialize(""),
                    100,
                    mm.deserialize("<green>Willkommen auf KissenPvP!"),
                    mm.deserialize("<gray>Du bist <b>Spieler</b>. lol")
            ));
        }
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            visualManager.remove(player);
        }
    }

    public VisualManager getVisualManager() {
        return visualManager;
    }
}
