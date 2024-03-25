package net.kissenpvp.visual.playersettings;

import net.kissenpvp.core.api.config.ConfigurationImplementation;
import net.kissenpvp.core.api.permission.Permission;
import net.kissenpvp.core.api.user.playersettting.UserValue;
import net.kissenpvp.visual.api.playersetting.SystemPrefix;
import net.kissenpvp.visual.theme.settings.DefaultSystemPrefix;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class KissenSystemPrefix implements SystemPrefix {
    @Override
    public @NotNull String getKey() {
        return "prefix";
    }

    @Override
    public @NotNull String getDefaultValue(@NotNull OfflinePlayer playerClient) {
        return Bukkit.getKissen().getImplementation(ConfigurationImplementation.class).getSetting(DefaultSystemPrefix.class);
    }

    @Override
    public @NotNull UserValue<String>[] getPossibleValues(@NotNull OfflinePlayer playerClient) {
        return ((Player) playerClient).getPermissionList().stream().filter(Permission::isValid).flatMap(permission -> {
            String permissionName = permission.getName();
            String permissionRequired = "visual.setting.%s.".formatted(getKey());
            if (permissionName.startsWith(permissionRequired)) {
                String text = permissionName.substring(permissionRequired.length()).replace('.', ' ');
                UserValue<String> value = new UserValue<>(text, permissionName);
                return Stream.of(value);
            }
            return Stream.empty();
        }).toArray(UserValue[]::new);
    }

    @Override
    public @NotNull String serialize(@NotNull String object) {
        return object;
    }

    @Override
    public @NotNull String deserialize(@NotNull String input) {
        return input;
    }
}
