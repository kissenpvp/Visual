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
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.paper.api.networking.client.entity.PaperPlayerClient;
import net.kissenpvp.paper.api.user.rank.Rank;
import net.kissenpvp.visual.api.rank.VisualRank;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;

public record KissenVisualRank(@NotNull Rank rank) implements VisualRank {

    @NotNull
    private Savable dataSource() {
        return (Savable) rank();
    }

    @Override
    public @NotNull TextColor getColor() {
        return dataSource().get("color", TextColor.class).orElseGet(() -> getPrefix().map(component -> {
            TextColor namedTextColor = component.color();
            if (!component.children().isEmpty()) {
                namedTextColor = component.children().get(component.children().size() - 1).color();
            }
            return namedTextColor; // might be null
        }).orElse(NamedTextColor.WHITE));
    }

    @Override
    public void setColor(@Nullable TextColor textColor) {
        dataSource().set("color", textColor);
    }

    @Override
    public void unsetColor() {
        setColor(null);
    }

    @Override
    public @NotNull Optional<Component> getPrefix() {
        return dataSource().get("prefix", Component.class);
    }

    @Override
    public void setPrefix(@Nullable Component component) {
        dataSource().set("prefix", component);
    }

    @Override
    public void unsetPrefix() {
        setPrefix(null);
    }

    @Override
    public @NotNull Optional<Component> getSuffix() {
        return dataSource().get("suffix", Component.class);
    }

    @Override
    public void setSuffix(@Nullable Component component) {
        dataSource().set("suffix", component);
    }

    @Override
    public void unsetSuffix() {
        setSuffix(null);
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
        rank().setPriority(i);
    }

    @Override
    public int delete() throws BackendException {
        return rank().delete();
    }

    @Override
    public @NotNull @Unmodifiable Set<PaperPlayerClient> getPlayers() {
        return rank().getPlayers();
    }
}
