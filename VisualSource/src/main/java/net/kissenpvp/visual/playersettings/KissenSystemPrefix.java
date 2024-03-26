/*
 * Copyright (C) 2024 KissenPvP
 *
 * This program is licensed under the Apache License, Version 2.0.
 *
 * This software may be redistributed and/or modified under the terms
 * of the Apache License as published by the Apache Software Foundation,
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the Apache
 * License, Version 2.0 for the specific language governing permissions
 * and limitations under the License.
 *
 * You should have received a copy of the Apache License, Version 2.0
 * along with this program. If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package net.kissenpvp.visual.playersettings;

import net.kissenpvp.core.api.config.ConfigurationImplementation;
import net.kissenpvp.core.api.user.playersettting.UserValue;
import net.kissenpvp.paper.api.permission.Permission;
import net.kissenpvp.visual.api.playersetting.SystemPrefix;
import net.kissenpvp.visual.theme.settings.DefaultSystemPrefix;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
    public @NotNull UserValue<String>[] getPossibleValues(@NotNull OfflinePlayer player) {
        return player.getPermissionList().stream().filter(Permission::isValid).flatMap(permission -> {
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
