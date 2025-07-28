package net.kissenpvp.visual;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VisualManager {
    private final Map<UUID, Team> teams = new HashMap<>();
    private final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

    public void update(Player player, VisualData data) {
        setHeaderFooter(player, data.header(), data.footer());
        setTeam(player, data.prefix(), data.suffix(), data.priority());
    }

    public void remove(Player player) {
        Team team = teams.remove(player.getUniqueId());
        if (team != null) {
            team.removeEntry(player.getName());
            team.unregister();
        }
    }

    private void setHeaderFooter(Player player, Component header, Component footer) {
        player.sendPlayerListHeader(header);
        player.sendPlayerListFooter(footer);
    }

    private void setTeam(Player player, Component prefix, Component suffix, int priority) {
        Team oldTeam = teams.remove(player.getUniqueId());
        if (oldTeam != null) oldTeam.unregister();

        String teamName = String.format("%03d_%s", priority, player.getName().substring(0, Math.min(10, player.getName().length())));
        Team team = board.getTeam(teamName);
        if (team == null) team = board.registerNewTeam(teamName);

        team.prefix(prefix);
        team.suffix(suffix);
        team.addEntry(player.getName());
        teams.put(player.getUniqueId(), team);
        player.setScoreboard(board);
    }
}
