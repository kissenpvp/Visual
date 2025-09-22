package net.corwis.kissenpvp.rank;

import net.corwis.kissenpvp.Visual;
import net.kissenpvp.api.database.KissenRepository;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class VisualRankRepository extends KissenRepository<String, VisualRank> {

    public VisualRankRepository(@NotNull DataSource dataSource) throws NullPointerException {
        super(dataSource);
    }

    @Contract(value = "_, _ -> new") private static @NotNull VisualRank toVisualRank(@NotNull String id, @NotNull ResultSet resultSet) throws SQLException, NullPointerException
    {
        Objects.requireNonNull(id, "id is null");
        Objects.requireNonNull(resultSet, "resultSet is null");

        Component prefix = Visual.serializer().deserialize(resultSet.getString("prefix"));
        String suffixData = resultSet.getString("suffix");

        Optional<Component> suffix = Optional.empty();
        if (!resultSet.wasNull())
        {
            suffix = Optional.of(Visual.serializer().deserialize(suffixData));
        }

        TextColor color = TextColor.color(resultSet.getInt("color"));
        return new VisualRank(id, prefix, suffix, color);
    }

    @Override public @NotNull CompletableFuture<@NotNull Optional<VisualRank>> find(@NotNull String id) throws NullPointerException
    {
        String sql = "SELECT prefix, suffix, color FROM ksvi_visual_rank WHERE id = ?;";
        return CompletableFuture.supplyAsync(() -> Objects.requireNonNull(query(sql, statement ->
        {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery())
            {
                if(!resultSet.next())
                {
                    return Optional.empty();
                }

                return Optional.of(toVisualRank(id, resultSet));
            }
        })));
    }

    @Override public @NotNull CompletableFuture<@UnmodifiableView Collection<VisualRank>> findAll(@NotNull Iterable<String> ids) throws NullPointerException
    {
        String placeholders = String.join(", ", Collections.nCopies(computeIterableSize(ids), "?"));
        String sql = "SELECT id, prefix, suffix, color FROM ksvi_visual_rank IN (" + placeholders +");";
        return CompletableFuture.supplyAsync(() -> query(sql, statement -> {

            int index = 1;
            for(String entry : ids)
            {
                statement.setString(index++, entry);
            }

            Collection<VisualRank> ranks = new HashSet<>();
            try (ResultSet resultSet = statement.executeQuery())
            {
                while(resultSet.next())
                {
                    ranks.add(toVisualRank(resultSet.getString("id"), resultSet));
                }
            }
            return Collections.unmodifiableCollection(ranks);
        }));
    }

    @Override public @NotNull CompletableFuture<@UnmodifiableView Collection<VisualRank>> findAll()
    {
        String sql = "SELECT id, prefix, suffix, color FROM ksvi_visual_rank;";
        return CompletableFuture.supplyAsync(() -> query(sql, statement -> {
            Collection<VisualRank> ranks = new HashSet<>();
            try (ResultSet resultSet = statement.executeQuery())
            {
                while(resultSet.next())
                {
                    ranks.add(toVisualRank(resultSet.getString("id"), resultSet));
                }
            }
            return Collections.unmodifiableCollection(ranks);
        }));
    }

    @Override public @NotNull CompletableFuture<Boolean> has(@NotNull String id) throws NullPointerException
    {
        String sql = "SELECT id FROM ksvi_visual_rank WHERE id = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, statement ->
        {
            statement.setString(1, id);
            try(ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next();
            }
        }));
    }

    @Override
    public @NotNull CompletableFuture<Void> saveAll(@NotNull Iterable<VisualRank> iterable) throws NullPointerException {
        String sql = "INSERT INTO ksvi_visual_rank (id, prefix, suffix, color) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE prefix = ?, suffix = ?, color = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, (statement) -> {
            for(VisualRank rank : iterable)
            {
                statement.setString(1, rank.id());

                GsonComponentSerializer serializer = GsonComponentSerializer.gson();
                String prefix = serializer.serialize(rank.prefix());
                statement.setString(2, prefix);
                statement.setString(4, prefix);

                String suffix = rank.suffix().map(serializer::serialize).orElse(null);
                statement.setString(3, suffix);
                statement.setString(5, suffix);

                statement.setInt(4, rank.chatColor().value());
                statement.setInt(6, rank.chatColor().value());

                statement.addBatch();
            }
            statement.executeBatch();
            return null;
        }));
    }
}
