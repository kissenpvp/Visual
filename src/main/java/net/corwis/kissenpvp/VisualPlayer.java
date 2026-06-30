package net.corwis.kissenpvp;

import net.corwis.kissenpvp.suffix.SuffixModal;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record VisualPlayer(@NonNull UUID uniqueId, @NonNull Optional<Component> prefix, @NonNull List<SuffixModal> suffix)
{
}
