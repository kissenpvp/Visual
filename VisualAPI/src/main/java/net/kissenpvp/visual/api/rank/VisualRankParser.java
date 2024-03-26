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

import net.kissenpvp.paper.api.command.ArgumentParser;
import net.kissenpvp.paper.api.user.rank.RankParser;
import net.kissenpvp.visual.api.Visual;
import org.jetbrains.annotations.NotNull;

public class VisualRankParser implements ArgumentParser<VisualRank> {

    private final Visual visual;
    private final RankParser parent;

    public VisualRankParser(@NotNull Visual visual) {
        this.visual = visual;
        this.parent = new RankParser();
    }

    protected @NotNull Visual getVisual() {
        return visual;
    }

    protected @NotNull RankParser getParent() {
        return parent;
    }

    @Override
    public @NotNull String serialize(@NotNull VisualRank visualRank) {
        return visualRank.getName();
    }

    @Override
    public @NotNull VisualRank deserialize(@NotNull String input) {

        return getVisual().getRankData(getParent().deserialize(input));
    }
}
