package net.corwis.kissenpvp.rank;

import net.kissenpvp.api.database.Repository;
import net.kissenpvp.database.InternalRepository;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class VisualRankRepository extends InternalRepository<String, VisualRank> implements Repository<String, VisualRank> {

    public VisualRankRepository(@NotNull Connection connection) throws NullPointerException {
        // "CREATE TABLE IF NOT EXISTS ksvp_visual_rank (id VARCHAR(20) NOT NULL, prefix JSON NOT NULL, suffix JSON NULL DEFAULT NULL, COLOR INT NOT NULL), PRIMARY KEY (id), FOREIGN KEY (id) REFERENCES ksvp_rank(id);"

        super("ksvp_visual_rank", connection, "SELECT * FROM ksvp_visual_rank WHERE id = ?;", "SELECT * FROM ksvp_visual_rank;", "SELECT * FROM ksvp_visual_rank WHERE id IN (?);");
    }

    @Override
    public @NotNull VisualRank toEntity(@NotNull String s, @NotNull ResultSet resultSet) throws SQLException, NullPointerException {

        GsonComponentSerializer serializer = GsonComponentSerializer.gson();
        Component prefix = serializer.deserialize(resultSet.getString("prefix"));
        String suffixData = resultSet.getString("suffix");

        Optional<Component> suffix = Optional.empty();
        if(!resultSet.wasNull())
        {
            suffix = Optional.of(serializer.deserialize(suffixData));
        }


        TextColor color = TextColor.color(resultSet.getInt("color"));
        return new VisualRank(s, prefix, suffix, color);
    }

    @Override
    public @NotNull VisualRank toEntity(@NotNull ResultSet resultSet) throws SQLException, NullPointerException {
        return toEntity(resultSet.getString("id"), resultSet);
    }

    @Override
    public @NotNull CompletableFuture<Void> saveAll(@NotNull Iterable<VisualRank> iterable) throws NullPointerException {
        String sql = "INSERT INTO ksvp_visual_rank (id, prefix, suffix, color) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE prefix = ?, suffix = ?, color = ?;";
        return CompletableFuture.supplyAsync(() -> query(sql, (statement) -> {
            for(VisualRank rank : iterable)
            {
                statement.setString(1, rank.id());


                GsonComponentSerializer serializer = GsonComponentSerializer.gson();
                String prefix = serializer.serialize(rank.prefix());
                setDual(statement, 2, 4, Types.VARCHAR, prefix);
                String suffix = rank.suffix().map(serializer::serialize).orElse(null);
                setDual(statement, 3, 5, Types.VARCHAR, suffix);

                setDual(statement, 4, 6, Types.INTEGER, rank.chatColor().value());
                statement.addBatch();
            }
            statement.executeBatch();
            return null;
        }));
    }
}
