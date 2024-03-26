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

import net.kissenpvp.core.api.user.playersettting.UserValue;
import net.kissenpvp.visual.api.theme.playersetting.HighlightVariables;
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
