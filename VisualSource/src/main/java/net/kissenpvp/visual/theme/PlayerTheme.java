/*
 * Copyright (C) 2023 KissenPvP
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

package net.kissenpvp.visual.theme;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kissenpvp.visual.api.theme.HighlightVariables;
import net.kissenpvp.visual.theme.playersettings.*;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@AllArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class PlayerTheme extends DefaultTheme {

    private final Player player;

    @Override
    public @NotNull TextColor getPrimaryAccentColor() {
        return getPlayer().getSetting(KissenPrimaryUserColor.class).getValue();
    }


    @Override
    public @NotNull TextColor getSecondaryAccentColor() {
        return getPlayer().getSetting(KissenSecondaryUserColor.class).getValue();
    }

    @Override
    public @NotNull TextColor getGeneralColor() {
        return getPlayer().getSetting(KissenGeneralUserColor.class).getValue();
    }

    @Override
    public @NotNull TextColor getEnabledColor() {
        return getPlayer().getSetting(KissenEnabledUserColor.class).getValue();
    }

    @Override
    public @NotNull TextColor getDisabledColor() {
        return getPlayer().getSetting(KissenDisabledUserColor.class).getValue();
    }

    @Override
    protected @NotNull TextColor highlightColor() {
        if (getPlayer().getSetting(HighlightVariables.class).getValue()) {
            return super.highlightColor();
        }
        return getGeneralColor();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPrimaryAccentColor(), getSecondaryAccentColor(), getGeneralColor(), getDisabledColor(), getEnabledColor());
    }
}
