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

package net.kissenpvp.visual.rank;

import net.kissenpvp.core.api.database.meta.BackendException;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.pulvinar.api.user.rank.Rank;
import net.kissenpvp.visual.api.rank.VisualRank;
import net.kissenpvp.visual.api.theme.ThemeProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;

public record KissenVisualRankFallBack(@NotNull Rank rank) implements VisualRank {

    @Override
    public @NotNull TextColor getColor() {
        return NamedTextColor.GRAY;
    }

    @Override
    public void setColor(@Nullable TextColor textColor) {
        throw new EventCancelledException(new UnsupportedOperationException());
    }

    @Override
    public void unsetColor() {
        throw new EventCancelledException(new UnsupportedOperationException());
    }

    @Override
    public @NotNull Optional<Component> getPrefix() {
        Component name = Component.translatable("merchant.level.3", ThemeProvider.general());
        return Optional.of(name.appendSpace().append(Component.text("|", ThemeProvider.general())));
    }

    @Override
    public void setPrefix(@Nullable Component component) {
        throw new EventCancelledException(new UnsupportedOperationException());
    }

    @Override
    public void unsetPrefix() {
        throw new EventCancelledException(new UnsupportedOperationException());
    }

    @Override
    public @NotNull Optional<Component> getSuffix() {
        return Optional.empty();
    }

    @Override
    public void setSuffix(@Nullable Component component) {
        throw new EventCancelledException(new UnsupportedOperationException());
    }

    @Override
    public void unsetSuffix() {
        throw new EventCancelledException(new UnsupportedOperationException());
    }

    @Override
    public @NotNull String getName() {
        return rank().getName();
    }

    @Override
    public int getPriority() {
        return rank().getPriority();
    }

    @Override
    public void setPriority(int i) throws EventCancelledException {
        throw new EventCancelledException(new UnsupportedOperationException());
    }

    @Override
    public int delete() throws BackendException {
        throw new EventCancelledException(new UnsupportedOperationException());
    }

    @Override
    public @NotNull @Unmodifiable Set<OfflinePlayer> getPlayers() {
        return rank().getPlayers();
    }
}
