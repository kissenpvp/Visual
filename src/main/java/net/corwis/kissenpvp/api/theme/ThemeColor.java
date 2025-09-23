package net.corwis.kissenpvp.api.theme;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

public enum ThemeColor
{

    GENERAL('p'),
    HIGHLIGHT('h');

    private final char legacyColorCode;

    public static @NonNull ThemeColor byLegacyColorCode(char legacyColorCode)
    {
        for (ThemeColor color : values())
        {
            if (Objects.equals(color.legacyColorCode, legacyColorCode))
            {
                return color;
            }
        }
        throw new IllegalArgumentException("No color with legacy color code " + legacyColorCode);
    }

    ThemeColor(char legacyColorCode)
    {
        this.legacyColorCode = legacyColorCode;
    }

    public char legacyColorCode()
    {
        return legacyColorCode;
    }
}
