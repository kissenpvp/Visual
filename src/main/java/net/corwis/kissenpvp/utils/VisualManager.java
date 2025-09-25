package net.corwis.kissenpvp.utils;

import net.corwis.kissenpvp.VisualData;
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
    private final Map<UUID, VisualData> data = new HashMap<>();
    private final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

    public void update(Player player, VisualData visualData) {
        if (player == null || visualData == null) return;
        data.put(player.getUniqueId(), visualData);
        setHeaderFooter(player, visualData.header(), visualData.footer());
        setTeam(player, visualData.prefix(), visualData.suffix(), visualData.priority());
    }

    public void remove(Player player) {
        if (player == null) return;
        player.sendPlayerListHeader(Component.empty());
        player.sendPlayerListFooter(Component.empty());
        Team team = teams.remove(player.getUniqueId());
        if (team != null) {
            team.removeEntry(player.getName());
            team.unregister();
        }
        data.remove(player.getUniqueId());
    }

    public VisualData get(Player player) {
        return (player == null) ? null : data.get(player.getUniqueId());
    }

    private void setHeaderFooter(Player player, Component header, Component footer) {
        player.sendPlayerListHeader(header == null ? Component.empty() : header);
        player.sendPlayerListFooter(footer == null ? Component.empty() : footer);
    }

    private void setTeam(Player player, Component prefix, Component suffix, int priority) {
        Team oldTeam = teams.remove(player.getUniqueId());
        if (oldTeam != null) {
            oldTeam.removeEntry(player.getName());
            oldTeam.unregister();
        }
        String base = player.getName();
        String shortName = base.substring(0, Math.min(10, base.length()));
        String teamName = String.format("%03d_%s", Math.max(0, Math.min(999, priority)), shortName);
        Team team = board.getTeam(teamName);
        if (team == null) {
            team = board.registerNewTeam(teamName);
        }
        if (prefix != null) team.prefix(prefix);
        if (suffix != null) team.suffix(suffix);
        team.addEntry(player.getName());
        teams.put(player.getUniqueId(), team);
        player.setScoreboard(board);
    }
}
