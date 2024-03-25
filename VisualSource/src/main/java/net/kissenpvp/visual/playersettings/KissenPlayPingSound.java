package net.kissenpvp.visual.playersettings;

import net.kissenpvp.core.api.user.playersettting.UserValue;
import net.kissenpvp.visual.api.playersetting.PlayPingSound;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class KissenPlayPingSound implements PlayPingSound {
    @Override
    public @NotNull String getKey() {
        return "notify";
    }

    @Override
    public @NotNull Boolean getDefaultValue(@NotNull OfflinePlayer playerClient) {
        return true;
    }

    @Override
    public @NotNull UserValue<Boolean>[] getPossibleValues(@NotNull OfflinePlayer playerClient) {
        return new UserValue[]{new UserValue<>(false), new UserValue<>(true),};
    }

    @Override
    public @NotNull String serialize(@NotNull Boolean sound) {
        return sound.toString();
    }

    @Override
    public @NotNull Boolean deserialize(@NotNull String s) {
        return Boolean.parseBoolean(s);
    }
}
