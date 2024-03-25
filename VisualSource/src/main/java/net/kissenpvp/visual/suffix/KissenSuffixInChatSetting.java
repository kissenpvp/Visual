package net.kissenpvp.visual.suffix;

import net.kissenpvp.core.api.user.playersettting.UserValue;
import net.kissenpvp.visual.api.suffix.playersetting.SuffixInChatSetting;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class KissenSuffixInChatSetting implements SuffixInChatSetting
{

    @Override
    public @NotNull String serialize(@NotNull Boolean object)
    {
        return object.toString();
    }

    @Override
    public @NotNull Boolean deserialize(@NotNull String input)
    {
        return Boolean.parseBoolean(input);
    }

    @Override
    public @NotNull String getKey()
    {
        return "suffixinchat";
    }

    @Override
    public @NotNull Boolean getDefaultValue(@NotNull OfflinePlayer playerClient)
    {
        return true;
    }

    @Override
    public @NotNull UserValue<Boolean>[] getPossibleValues(@NotNull OfflinePlayer playerClient)
    {
        return new UserValue[] {new UserValue<>(true), new UserValue<>(false)};
    }
}
