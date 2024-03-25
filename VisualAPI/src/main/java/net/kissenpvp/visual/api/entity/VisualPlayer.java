package net.kissenpvp.visual.api.entity;

import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.paper.api.base.Context;
import net.kissenpvp.visual.api.suffix.Suffix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public interface VisualPlayer extends VisualEntity<Player> {

    @NotNull
    Set<Suffix> getSuffixes(@NotNull Context context);

    @NotNull
    Optional<Suffix> getSuffix(@NotNull String name, @NotNull Context context);

    @NotNull
    Suffix grantSuffix(@NotNull String name, @NotNull Component content, @NotNull Context context) throws EventCancelledException;

    @NotNull
    Suffix grantSuffix(@NotNull String name, @NotNull Component content, @Nullable AccurateDuration accurateDuration, @NotNull Context context) throws EventCancelledException;

    boolean revokeSuffix(@NotNull String name, @NotNull Context context) throws EventCancelledException;

    @NotNull
    Set<Suffix> getSuffixes();

    @NotNull
    Optional<Suffix> getSuffix(@NotNull String name);

    @NotNull
    Suffix grantSuffix(@NotNull String name, @NotNull Component content) throws EventCancelledException;

    @NotNull
    Suffix grantSuffix(@NotNull String name, @NotNull Component content, @Nullable AccurateDuration accurateDuration) throws EventCancelledException;

    boolean revokeSuffix(@NotNull String name);

    @NotNull
    Optional<Suffix> getSuffix();

}
