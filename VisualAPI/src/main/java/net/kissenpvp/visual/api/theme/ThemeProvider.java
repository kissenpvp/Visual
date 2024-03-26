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

package net.kissenpvp.visual.api.theme;

import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ThemeProvider {

    private static final TextColor PRIMARY = net.kyori.adventure.text.format.TextColor.color(112114105);
    private static final TextColor SECONDARY = net.kyori.adventure.text.format.TextColor.color(11510199);
    private static final TextColor GENERAL = net.kyori.adventure.text.format.TextColor.color(100101102);
    private static final TextColor ENABLED = net.kyori.adventure.text.format.TextColor.color(10111097);
    private static final TextColor DISABLED = net.kyori.adventure.text.format.TextColor.color(100105115);

    public static TextColor primary() {
        return PRIMARY;
    }

    public static TextColor secondary() {
        return SECONDARY;
    }

    public static TextColor general() {
        return GENERAL;
    }

    public static TextColor enabled() {
        return ENABLED;
    }

    public static TextColor disabled() {
        return DISABLED;
    }

    public static @NotNull TextColor @NotNull [] values()
    {
        //noinspection SuspiciousToArrayCall
        return Arrays.stream(ThemeProvider.class.getDeclaredFields()).filter(field -> field.getType().equals(TextColor.class)).map(field -> {
            try {
                return field.get(null);
            } catch (IllegalAccessException ignored) { return null; }
        }).filter(Objects::nonNull).toList().toArray(new TextColor[0]);
    }

    public static @NotNull Optional<TextColor> getColor(@NotNull Predicate<TextColor> predicate)
    {
        return Stream.of(values()).filter(predicate).findFirst();
    }

}
