package net.kissenpvp.visual;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class VisualManager {

    private final Map<UUID, Team> teams = new HashMap<>();
    private final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

    public void update(@NotNull Player player, @NotNull VisualData data) {
        setHeaderFooter(player, data.header(), data.footer());
        setTeam(player, data);
    }

    public void remove(@NotNull Player player) {
        Team team = teams.remove(player.getUniqueId());
        if (team != null) {
            team.removeEntry(player.getName());
            team.unregister();
        }
    }

    private void setHeaderFooter(@NotNull Player player, @NotNull Component header, @NotNull Component footer) {
        player.sendPlayerListHeader(header);
        player.sendPlayerListFooter(footer);
    }

    private void setTeam(@NotNull Player player, @NotNull VisualData data) {

        Team oldTeam = teams.remove(player.getUniqueId());
        if (oldTeam != null)
        {
            oldTeam.unregister();
        }

        String name = player.getName().substring(0, Math.min(10, player.getName().length()));
        String teamName = String.format("%03d_%s", data.priority(), name);

        Team team = board.getTeam(teamName);
        if (Objects.isNull(team))
        {
            team = board.registerNewTeam(teamName);
        }

        team.prefix(data.prefix());
        team.suffix(data.suffix());
        team.addEntry(player.getName());

        teams.put(player.getUniqueId(), team);
        player.setScoreboard(board);
    }
}
