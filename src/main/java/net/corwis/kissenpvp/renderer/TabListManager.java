package net.corwis.kissenpvp.renderer;

import net.corwis.kissenpvp.Visual;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class TabListManager {

    public void decorate(@NonNull Component header, @NonNull Component footer)
    {
        Objects.requireNonNull(header, "header must not be null!");
        Objects.requireNonNull(footer, "footer must not be null!");

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        for(Player current : Bukkit.getOnlinePlayers())
        {
            Visual.VisualPlayer currentData = Visual.getPlugin(Visual.class).playerData(current);
            Team team = scoreboard.registerNewTeam(current.getUniqueId().toString());

            currentData.prefix().ifPresent(team::prefix);
            currentData.suffix().ifPresent(team::suffix);

            team.addEntity(current);
            current.setScoreboard(scoreboard);

            current.sendPlayerListHeader(header);
            current.sendPlayerListFooter(footer);
        }
    }
}

