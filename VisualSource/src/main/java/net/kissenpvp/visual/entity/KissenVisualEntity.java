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

package net.kissenpvp.visual.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.visual.api.entity.VisualEntity;
import net.kissenpvp.visual.theme.DefaultTheme;
import net.kissenpvp.visual.api.theme.Theme;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

@AllArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class KissenVisualEntity<S extends ServerEntity> implements VisualEntity<S> {

    private S entity;
    @lombok.Getter private Theme theme;

    public KissenVisualEntity(@NotNull S serverEntity) {
        this(serverEntity, new DefaultTheme());
    }

    public @NotNull Component styledName() {
        Component prefix = getPrefixComponent().map(Component::appendSpace).orElseGet(Component::empty);

        Function<Component, Component> space = suffix -> Component.space().append(suffix);
        Component suffix = getSuffixComponent().map(space).orElseGet(Component::empty);
        return prefix.append(getEntity().displayName()).append(suffix);
    }

    public @NotNull Optional<Component> getPrefixComponent() {
        return Optional.empty();
    }

    public @NotNull Optional<Component> getSuffixComponent() {
        return Optional.empty();
    }
}
