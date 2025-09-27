package net.corwis.kissenpvp.suffix;

import net.corwis.kissenpvp.Visual;
import net.kissenpvp.api.database.KissenRepository;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class VisualSuffixRepository extends KissenRepository<String, VisualSuffix>
{
    public VisualSuffixRepository(@NonNull DataSource dataSource) throws NullPointerException
    {
        super(dataSource);
    }

    @Override public @NonNull CompletableFuture<@NonNull Optional<VisualSuffix>> find(@NonNull String id) throws NullPointerException
    {
        String sql = "SELECT player_id, content FROM ksvi_visual_suffix WHERE id = ?";
        return CompletableFuture.supplyAsync(() -> Objects.requireNonNull(query(sql, statement ->
        {
            statement.setString(1, id);

            try (ResultSet resultSet = statement.executeQuery())
            {
                if (!resultSet.next())
                {
                    return Optional.empty();
                }

                Component component = Visual.serializer().deserialize(resultSet.getString("content"));
                return Optional.of(new VisualSuffix(id, UUID.fromString(resultSet.getString("player_id")), component));
            }
        })));
    }

    @Override public @NonNull CompletableFuture<@NonNull Collection<VisualSuffix>> findAll(@NonNull Iterable<String> iterable) throws NullPointerException
    {
        String placeholders = String.join(", ", Collections.nCopies(computeIterableSize(iterable), "?"));
        String sql = "SELECT id, player_id, content FROM ksvi_visual_suffix WHERE id IN (" + placeholders + ");";
        return CompletableFuture.supplyAsync(() -> Objects.requireNonNull(query(sql, statement ->
        {
            int index = 1;
            for (String entry : iterable)
            {
                statement.setString(index++, entry);
            }

            return collectResults(statement);
        })));
    }

    @Override public @NonNull CompletableFuture<@NonNull Collection<VisualSuffix>> findAll()
    {
        String sql = "SELECT id, player_id, content FROM ksvi_visual_suffix;";
        return CompletableFuture.supplyAsync(() -> query(sql, this::collectResults));
    }

    @Override public @NonNull CompletableFuture<Boolean> has(@NonNull String id) throws NullPointerException
    {
        String sql = "SELECT id FROM ksvi_visual_suffix WHERE id = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, statement -> {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next();
            }
        }));
    }

    @Override public @NonNull CompletableFuture<Void> saveAll(@NonNull Iterable<VisualSuffix> iterable) throws NullPointerException
    {
        String sql = "INSERT INTO ksvi_visual_suffix (id, player_id, content) VALUES (?, ?) ON DUPLICATE KEY UPDATE content = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, statement -> {
            for(VisualSuffix suffix : iterable)
            {
                statement.setString(1, suffix.id());
                statement.setString(2, String.valueOf(suffix.playerId()));

                String content = Visual.serializer().serialize(suffix.content());
                statement.setString(3, content);
                statement.setString(4, content);
                statement.addBatch();
            }

            statement.executeBatch();
            return null;
        }));
    }

    private @NonNull Collection<VisualSuffix> collectResults(@NonNull PreparedStatement statement) throws SQLException
    {
        Collection<VisualSuffix> suffixes = new ArrayList<>();
        try(ResultSet resultSet = statement.executeQuery())
        {
            while(resultSet.next())
            {
                Component component = Visual.serializer().deserialize(resultSet.getString("content"));
                UUID playerId = UUID.fromString(resultSet.getString("player_id"));
                suffixes.add(new VisualSuffix(resultSet.getString("id"), playerId, component));
            }
        }
        return Collections.unmodifiableCollection(suffixes);
    }
}
