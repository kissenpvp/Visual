package net.corwis.kissenpvp.theme;

import net.corwis.kissenpvp.api.theme.ThemeColor;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jspecify.annotations.NonNull;

public class DefaultTheme extends AbstractTheme
{
    @Override
    public @NonNull TextColor color(@NonNull ThemeColor color)
    {
        return switch (color) {
            case GENERAL -> NamedTextColor.GRAY;
            case HIGHLIGHT -> NamedTextColor.YELLOW;
        };
    }
}
