package net.corwis.kissenpvp;

import net.kyori.adventure.text.Component;

public record VisualData(
        Component prefix,
        Component suffix,
        int priority,
        Component header,
        Component footer
) { }
