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

package net.kissenpvp.visual.api.rank;

import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.pulvinar.api.user.rank.Rank;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface VisualRank extends Rank {

    @NotNull
    TextColor getColor();

    void setColor(@Nullable TextColor textColor) throws EventCancelledException;

    void unsetColor() throws EventCancelledException;

    @NotNull
    Optional<Component> getPrefix();

    void setPrefix(@Nullable Component component) throws EventCancelledException;

    void unsetPrefix() throws EventCancelledException;

    @NotNull
    Optional<Component> getSuffix();

    void setSuffix(@Nullable Component component) throws EventCancelledException;

    void unsetSuffix();

}
