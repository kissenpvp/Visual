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

package net.kissenpvp.visual.theme.playersettings;

import net.kissenpvp.core.api.config.ConfigurationImplementation;
import net.kissenpvp.visual.api.theme.playersetting.DisabledUserColor;
import net.kissenpvp.visual.theme.settings.DefaultDisabledColor;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class KissenDisabledUserColor extends UserColorSetting implements DisabledUserColor {
    @Override
    public @NotNull String getKey() {
        return "disabledcolor";
    }

    @Override
    public @NotNull NamedTextColor getDefaultValue(@NotNull OfflinePlayer playerClient) {
        return Bukkit.getPulvinar().getImplementation(ConfigurationImplementation.class).getSetting(DefaultDisabledColor.class);
    }
}
