package net.corwis.kissenpvp.suffix;

import net.kissenpvp.api.database.PersistableEntity;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;

public record SuffixModal(@NonNull String id, @NonNull Component content) implements PersistableEntity<String> {


    public SuffixModal
    {
        if(id.length() > 20) { throw new InvalidSuffixNameException("Suffix name must not exceed 20 characters."); }
    }
}
