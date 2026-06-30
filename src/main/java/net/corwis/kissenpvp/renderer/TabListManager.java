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

    /**
     * Converts a non-negative priority value into an alphabetic representation
     * using a base-26 alphabet ({@code A-Z}).
     *
     * <p>The conversion maps values as follows:
     * <pre>
     * 0  -> A
     * 1  -> B
     * 25 -> Z
     * 26 -> BA
     * 27 -> BB
     * </pre>
     *
     * <p>The resulting string is ordered similarly to a positional number system,
     * where each "digit" is represented by an uppercase letter from {@code A} to
     * {@code Z}.
     *
     * @param priority the priority value to convert; must be non-negative
     * @return the alphabetic representation of the given priority
     * @throws IllegalArgumentException if {@code priority} is negative
     */
    private static @NonNull String priorityToAlphabetic(int priority)
    {
        if (priority < 0) {
            throw new IllegalArgumentException("Priority must be >= 0");
        }

        StringBuilder result = new StringBuilder();

        do {
            int remainder = priority % 26;
            result.append((char) ('A' + remainder));
            priority /= 26;
        } while (priority > 0);

        return result.reverse().toString();
    }
}

