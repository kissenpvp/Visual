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

package net.kissenpvp.visual.suffix;

import net.kissenpvp.core.api.time.TemporalObject;
import net.kissenpvp.core.api.user.playersettting.UserValue;
import net.kissenpvp.visual.InternalVisual;
import net.kissenpvp.visual.api.event.VisualChangeEvent;
import net.kissenpvp.visual.api.suffix.Suffix;
import net.kissenpvp.visual.api.suffix.playersetting.SuffixSetting;
import net.kissenpvp.visual.entity.KissenVisualPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KissenSuffixSetting implements SuffixSetting
{

    @Override
    public @NotNull String serialize(@NotNull String object)
    {
        return object;
    }

    @Override
    public @NotNull String deserialize(@NotNull String input)
    {
        return input;
    }

    @Override
    public @NotNull String getKey()
    {
        return "suffix";
    }

    @Override
    public @NotNull String getDefaultValue(@NotNull OfflinePlayer playerClient)
    {
        return "none";
    }

    @Override
    public @NotNull UserValue<String>[] getPossibleValues(@NotNull OfflinePlayer playerClient)
    {
        InternalVisual internalVisual = InternalVisual.getPlugin(InternalVisual.class);
        KissenVisualPlayer visualPlayer = (KissenVisualPlayer) internalVisual.getEntity(playerClient);

        Stream<Suffix> suffixStream = visualPlayer.getSuffixes().stream().filter(TemporalObject::isValid);
        Function<Suffix, UserValue<String>> transform = suffix -> new UserValue<>(suffix.getName());
        Set<UserValue<String>> settings = suffixStream.map(transform).collect(Collectors.toSet());
        settings.add(new UserValue<>("none"));
        return settings.toArray(UserValue[]::new);
    }

    private static void visualUpdate(@NotNull OfflinePlayer player) {
        if (player instanceof Player actualPlayer) {
            Bukkit.getPluginManager().callEvent(new VisualChangeEvent(actualPlayer));
        }
    }

    @Override
    public void setValue(@NotNull OfflinePlayer player, @Nullable String value) {
        visualUpdate(player);
    }

    @Override
    public void reset(@NotNull OfflinePlayer player) {
        visualUpdate(player);
    }
}
