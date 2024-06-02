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

package net.kissenpvp.visual.api.entity;

import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.pulvinar.api.base.Context;
import net.kissenpvp.visual.api.suffix.Suffix;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public interface VisualPlayer extends VisualEntity<OfflinePlayer> {

    @NotNull OfflinePlayer getParent();

    @NotNull
    Set<Suffix> getSuffixes(@NotNull Context context);

    @NotNull
    Optional<Suffix> getSuffix(@NotNull String name, @NotNull Context context);

    @NotNull
    Suffix grantSuffix(@NotNull String name, @NotNull Component content, @NotNull Context context) throws EventCancelledException;

    @NotNull
    Suffix grantSuffix(@NotNull String name, @NotNull Component content, @Nullable AccurateDuration accurateDuration, @NotNull Context context) throws EventCancelledException;

    boolean revokeSuffix(@NotNull String name, @NotNull Context context) throws EventCancelledException;

    @NotNull
    Set<Suffix> getSuffixes();

    @NotNull
    Optional<Suffix> getSuffix(@NotNull String name);

    @NotNull
    Suffix grantSuffix(@NotNull String name, @NotNull Component content) throws EventCancelledException;

    @NotNull
    Suffix grantSuffix(@NotNull String name, @NotNull Component content, @Nullable AccurateDuration accurateDuration) throws EventCancelledException;

    boolean revokeSuffix(@NotNull String name);

    @NotNull
    Optional<Suffix> getSuffix();

}
