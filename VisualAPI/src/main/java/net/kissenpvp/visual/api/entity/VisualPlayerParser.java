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

import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.paper.api.command.ArgumentParser;
import net.kissenpvp.paper.api.command.parser.OfflinePlayerParser;
import net.kissenpvp.visual.api.Visual;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

public class VisualPlayerParser implements ArgumentParser<VisualPlayer> {

    private final Visual visual;
    private final OfflinePlayerParser parent;

    public VisualPlayerParser(@NotNull Visual visual) {
        this.visual = visual;
        this.parent = new OfflinePlayerParser();
    }

    protected @NotNull OfflinePlayerParser getParent() {
        return parent;
    }

    protected Visual getVisual() {
        return visual;
    }

    @Override
    public @NotNull String serialize(@NotNull VisualPlayer visualPlayer) {
        return Objects.requireNonNull(visualPlayer.getParent().getName());
    }

    @Override
    public @NotNull VisualPlayer deserialize(@NotNull String s) {
        return (VisualPlayer) getVisual().getEntity(getParent().deserialize(s));
    }

    @Override
    public @Nullable String argumentName() {
        return "player";
    }

    @Override
    public @NotNull Collection<String> tabCompletion(@NotNull CommandPayload<CommandSender> commandPayload) {
        return getParent().tabCompletion(commandPayload);
    }
}
