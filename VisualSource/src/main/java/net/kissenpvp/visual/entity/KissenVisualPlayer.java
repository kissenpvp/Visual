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

import net.kissenpvp.core.api.database.DataWriter;
import net.kissenpvp.core.api.event.EventCancelledException;
import net.kissenpvp.core.api.time.AccurateDuration;
import net.kissenpvp.core.api.time.TemporalObject;
import net.kissenpvp.core.api.user.User;
import net.kissenpvp.paper.api.base.Context;
import net.kissenpvp.visual.InternalVisual;
import net.kissenpvp.visual.api.entity.VisualPlayer;
import net.kissenpvp.visual.api.suffix.Suffix;
import net.kissenpvp.visual.suffix.*;
import net.kissenpvp.visual.theme.DefaultTheme;
import net.kissenpvp.visual.theme.PlayerTheme;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KissenVisualPlayer extends KissenVisualEntity<OfflinePlayer> implements VisualPlayer {

    public KissenVisualPlayer(@NotNull OfflinePlayer serverEntity) {
        super(serverEntity, serverEntity instanceof Player player ? new PlayerTheme(player):new DefaultTheme()); //TODO actual theme of offline player
    }

    @Override
    public @NotNull OfflinePlayer getParent() {
        return getEntity();
    }

    @Override
    public @NotNull Set<Suffix> getSuffixes(@NotNull Context context) {
        User user = getEntity().getUser(context);
        Stream<SuffixNode> suffixData = user.getListNotNull("suffix_list", SuffixNode.class).stream();
        return suffixData.map(transformSuffix(context)).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @NotNull Optional<Suffix> getSuffix(@NotNull String name, @NotNull Context context) {
        return getSuffixes(context).stream().filter(suffix -> Objects.equals(name, suffix.getName())).findFirst();
    }

    @Override
    public @NotNull Suffix grantSuffix(@NotNull String name, @NotNull Component content, @NotNull Context context) throws EventCancelledException {
        return grantSuffix(name, content, null, context);
    }

    @Override
    public @NotNull Suffix grantSuffix(@NotNull String name, @NotNull Component content, @Nullable AccurateDuration accurateDuration, @NotNull Context context) throws EventCancelledException {
        if (name.equals("rank") || name.equals("none")) {
            throw new EventCancelledException();
        }
        User user = getEntity().getUser(context);

        SuffixNode suffixNode = new SuffixNode(name, content, accurateDuration);
        user.getListNotNull("suffix_list", SuffixNode.class).replaceOrInsert(suffixNode);

        return transformSuffix(context).apply(suffixNode);
    }

    @Override
    public boolean revokeSuffix(@NotNull String name, @NotNull Context context) throws EventCancelledException {
        return getSuffix(name, context).filter(TemporalObject::isValid).map(suffix -> {
            suffix.setEnd(Instant.now());
            return true;
        }).orElse(false);
    }

    @Override
    public @NotNull Set<Suffix> getSuffixes() {
        return Stream.concat(getSuffixes(Context.LOCAL).stream(), getSuffixes(Context.GLOBAL).stream()).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @NotNull Optional<Suffix> getSuffix(@NotNull String name) {
        return getSuffix(name, Context.LOCAL).or(() -> getSuffix(name, Context.GLOBAL));
    }

    @Override
    public @NotNull Suffix grantSuffix(@NotNull String name, @NotNull Component content) throws EventCancelledException {
        return grantSuffix(name, content, Context.LOCAL);
    }

    @Override
    public @NotNull Suffix grantSuffix(@NotNull String name, @NotNull Component content, @Nullable AccurateDuration accurateDuration) throws EventCancelledException {
        return grantSuffix(name, content, accurateDuration, Context.LOCAL);
    }

    @Override
    public boolean revokeSuffix(@NotNull String name) {
        if (revokeSuffix(name, Context.LOCAL)) {
            return true;
        }

        return revokeSuffix(name, Context.GLOBAL);
    }

    @Override
    public @NotNull Optional<Suffix> getSuffix() {
        String setting = getEntity().getSetting(KissenSuffixSetting.class).getValue();
        if (setting.equals("none")) {
            return Optional.empty();
        }
        return getSuffix(setting).filter(TemporalObject::isValid);
    }

    @Override
    public @NotNull Optional<Component> getPrefixComponent() {
        InternalVisual internalVisual = InternalVisual.getPlugin(InternalVisual.class);
        return internalVisual.getRankData(getEntity().getRank().getSource()).getPrefix();
    }

    @Override
    public @NotNull Optional<Component> getSuffixComponent() {
        return getSuffix().map(Suffix::getContent);
    }

    private @NotNull Function<SuffixNode, KissenSuffix> transformSuffix(@NotNull Context context) {
        return suffix -> new KissenSuffix(suffix, suffixDataWriter(getEntity().getUser(context)));
    }

    protected @NotNull DataWriter<SuffixNode> suffixDataWriter(@NotNull User user) {
        return (suffix) -> user.getListNotNull("suffix_list", SuffixNode.class).replaceOrInsert(suffix);
    }
}
