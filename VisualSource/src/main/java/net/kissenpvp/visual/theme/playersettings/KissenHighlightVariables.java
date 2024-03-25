package net.kissenpvp.visual.theme.playersettings;

import net.kissenpvp.core.api.user.playersettting.UserValue;
import net.kissenpvp.visual.api.theme.HighlightVariables;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class KissenHighlightVariables implements HighlightVariables
{
    public @NotNull String serialize(@NotNull Boolean object)
    {
        return object.toString();
    }

    public @NotNull Boolean deserialize(@NotNull String input)
    {
        return Boolean.parseBoolean(input);
    }

    public @NotNull String getKey()
    {
        return "highlight";
    }

    public @NotNull Boolean getDefaultValue(@NotNull OfflinePlayer playerClient)
    {
        return true;
    }

    public @NotNull UserValue<Boolean>[] getPossibleValues(@NotNull OfflinePlayer playerClient)
    {
        return new UserValue[] {new UserValue<>(true), new UserValue<>(false)};
    }
}
