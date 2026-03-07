package net.corwis.kissenpvp.rank;

import net.kissenpvp.api.database.PersistableEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;

public record VisualRank(@NonNull String id, @NonNull Component prefix, @NonNull Optional<Component> suffix, @NonNull TextColor chatColor) implements PersistableEntity<String> {

}
