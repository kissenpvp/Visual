package net.corwis.kissenpvp.api.theme;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jspecify.annotations.NonNull;

public interface Theme
{

    @NonNull TextColor color(@NonNull ThemeColor color);

    @NonNull Component style(Component... component);

}
