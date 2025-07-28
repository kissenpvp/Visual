package net.kissenpvp.visual;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public record VisualData(@NotNull Component prefix, @NotNull Component suffix, int priority, @NotNull Component header, @NotNull Component footer) {}
