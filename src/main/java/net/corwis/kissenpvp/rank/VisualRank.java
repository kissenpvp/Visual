package net.corwis.kissenpvp.rank;

import net.kissenpvp.api.database.PersistableEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public record VisualRank(@NotNull String id, Component prefix, Optional<Component> suffix, TextColor chatColor) implements PersistableEntity<String> {

    @Override
    public int signature() {
        return Objects.hash(prefix, suffix, chatColor);
    }

    @Override
    public boolean unsaved() {
        return false;
    }
}
