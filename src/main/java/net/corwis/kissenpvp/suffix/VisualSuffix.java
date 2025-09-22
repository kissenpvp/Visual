package net.corwis.kissenpvp.suffix;

import net.kissenpvp.api.database.PersistableEntity;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.UUID;

public record VisualSuffix(@NonNull String id, @NonNull UUID playerId, @NonNull Component content) implements PersistableEntity<String>
{
    @Override
    public int signature()
    {
        return Objects.hash(content);
    }

    @Override
    public boolean unsaved()
    {
        return false;
    }
}
