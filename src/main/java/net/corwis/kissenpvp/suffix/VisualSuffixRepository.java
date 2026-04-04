package net.corwis.kissenpvp.suffix;

import com.google.common.base.Preconditions;
import net.corwis.kissenpvp.Visual;
import net.kissenpvp.api.database.KissenRepository;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class VisualSuffixRepository extends KissenRepository<String, SuffixModal>
{
    public VisualSuffixRepository(@NonNull DataSource dataSource) throws NullPointerException
    {
        super(dataSource);
    }

    @Override public @NonNull CompletableFuture<@NonNull Optional<SuffixModal>> find(@NonNull String id) throws NullPointerException
    {
        String sql = "SELECT player_id, content FROM ksvi_visual_suffix WHERE id = ?";
        return Bukkit.getPulvinar().databaseQueue().submit(() -> Objects.requireNonNull(query(sql, statement ->
        {
            statement.setString(1, id);

            try (ResultSet resultSet = statement.executeQuery())
            {
                if (!resultSet.next())
                {
                    return Optional.empty();
                }

                Component component = Visual.serializer().deserialize(resultSet.getString("content"));
                return Optional.of(new SuffixModal(id, component));
            }
        })));
    }

    @Override public @NonNull CompletableFuture<@NonNull Collection<SuffixModal>> findAll(@NonNull Iterable<String> iterable) throws NullPointerException
    {
        String placeholders = String.join(", ", Collections.nCopies(computeIterableSize(iterable), "?"));
        String sql = "SELECT id, player_id, content FROM ksvi_visual_suffix WHERE id IN (" + placeholders + ");";
        return Bukkit.getPulvinar().databaseQueue().submit(() -> Objects.requireNonNull(query(sql, statement ->
        {
            int index = 1;
            for (String entry : iterable)
            {
                statement.setString(index++, entry);
            }

            return collectResults(statement);
        })));
    }

    @Override public @NonNull CompletableFuture<@NonNull Collection<SuffixModal>> findAll()
    {
        String sql = "SELECT id, player_id, content FROM ksvi_visual_suffix;";
        return Bukkit.getPulvinar().databaseQueue().submit(() -> query(sql, this::collectResults));
    }

    @Override public @NonNull CompletableFuture<Boolean> has(@NonNull String id) throws NullPointerException
    {
        String sql = "SELECT id FROM ksvi_visual_suffix WHERE id = ?;";
        return Bukkit.getPulvinar().databaseQueue().submit(() -> query(sql, statement -> {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next();
            }
        }));
    }

    @Override public @NonNull CompletableFuture<Void> saveAll(@NonNull Iterable<SuffixModal> iterable) throws NullPointerException
    {
        String sql = "INSERT INTO ksvi_visual_suffix (id, content) VALUES (?, ?) ON DUPLICATE KEY UPDATE content = ?;";
        return Bukkit.getPulvinar().databaseQueue().submit(() -> query(sql, statement -> {

            for(SuffixModal suffix : iterable)
            {
                statement.setString(1, suffix.id());

                String content = Visual.serializer().serialize(suffix.content());
                statement.setString(2, content);
                statement.setString(3, content);
                statement.addBatch();
            }

            statement.executeBatch();
            return null;
        }));
    }

    public @NonNull CompletableFuture<Void> grantSuffix(@NonNull Player player, @NonNull String suffix) {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(suffix);

        Optional<SuffixModal> suffixModal = find(suffix).join();
        if(suffixModal.isEmpty())
        {
            throw new NullPointerException(String.format("Suffix %s could not be found.", suffix));
        }

        return grantSuffix(player, suffixModal.get());
    }

    public @NonNull CompletableFuture<Void> grantSuffix(@NonNull Player player, @NonNull SuffixModal suffix) {

        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(suffix);

        return Bukkit.getPulvinar().databaseQueue().submit(() -> {

            if(!has(suffix.id()).join()) { save(suffix).join(); } // TODO maybe optimize, as this is not run as often we might just ignore

            String query = "INSERT IGNORE INTO ksvi_visual_suffix_subscription (suffix_id, player_id) VALUES (?, ?);";
            return query(query, (statement) -> {
                statement.setString(1, suffix.id());
                statement.setString(2, String.valueOf(player.getUniqueId()));
                return null;
            });
        });
    }

    public @NonNull CompletableFuture<Void> detachSuffix(@NonNull Player player, @NonNull String suffix) {

        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(suffix);

        if(suffix.length() > 20)
        {
            throw new IllegalArgumentException("Suffix names do not exceed 20 characters.");
        }

        return Bukkit.getPulvinar().databaseQueue().submit(() -> {
            String query = "DELETE FROM ksvi_visual_suffix_subscription WHERE suffix_id = ? AND player_id = ?;;";
            return query(query, (statement) -> {
                statement.setString(1, suffix);
                statement.setString(2, String.valueOf(player.getUniqueId()));
                return null;
            });
        });
    }

    public @NonNull CompletableFuture<Collection<SuffixModal>> find(@NonNull Player player) {
        Preconditions.checkNotNull(player);

        return Bukkit.getPulvinar().databaseQueue().submit(() -> {

            String query = "SELECT s.id, s.content FROM ksvi_visual_suffix s JOIN ksvi_visual_suffix_subscription sub ON s.id = sub.suffix_id WHERE sub.player_id = ?;";
            return query(query, (statement) -> {

                statement.setString(1, String.valueOf(player.getUniqueId()));

                Collection<SuffixModal> suffixModals = new HashSet<>();
                try (ResultSet resultSet = statement.executeQuery())
                {
                    while(resultSet.next())
                    {
                        String name = resultSet.getString("s.id");
                        Component component = Visual.serializer().deserialize(resultSet.getString("s.content"));
                        suffixModals.add(new SuffixModal(name, component));
                    }
                }

                return suffixModals;
            });
        });
    }

    private @NonNull @UnmodifiableView Collection<SuffixModal> collectResults(@NonNull PreparedStatement statement) throws SQLException
    {
        Collection<SuffixModal> suffixes = new ArrayList<>();
        try(ResultSet resultSet = statement.executeQuery())
        {
            while(resultSet.next())
            {
                Component component = Visual.serializer().deserialize(resultSet.getString("content"));
                suffixes.add(new SuffixModal(resultSet.getString("id"), component));
            }
        }
        return Collections.unmodifiableCollection(suffixes);
    }
}
