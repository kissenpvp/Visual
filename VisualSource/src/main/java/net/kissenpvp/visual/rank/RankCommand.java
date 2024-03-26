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

import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.annotations.ArgumentName;
import net.kissenpvp.core.api.command.annotations.CommandData;
import net.kissenpvp.core.api.command.exception.ArgumentMissingException;
import net.kissenpvp.visual.api.rank.VisualRank;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class RankCommand {

    private static @NotNull String translateComponent(@NotNull String[] component) {
        String text = String.join(" ", component).replace('&', '§');
        if (text.replaceAll("§.", "").isBlank()) {
            throw new ArgumentMissingException();
        }
        return text;
    }

    @CommandData(value = "rank.edit.prefix")
    public void rankPrefix(@NotNull CommandPayload<CommandSender> commandPayload, @NotNull VisualRank paperRank, @NotNull @ArgumentName("prefix") String[] prefix) {
        Component prefixComponent = LegacyComponentSerializer.legacySection().deserialize(translateComponent(prefix));
        paperRank.setPrefix(prefixComponent);

        Component[] args = {Component.text(paperRank.getName()), prefixComponent};
        commandPayload.getSender().sendMessage(Component.translatable("server.rank.edit.prefix.set", args));
    }

    @CommandData(value = "rank.edit.prefix.remove")
    public void rankPrefix(@NotNull CommandPayload<CommandSender> commandPayload, @NotNull VisualRank paperRank) {
        paperRank.unsetPrefix();

        Component[] args = {Component.text(paperRank.getName()), Component.text("none")};
        commandPayload.getSender().sendMessage(Component.translatable("server.rank.edit.prefix.set", args));
    }

    @CommandData(value = "rank.edit.suffix")
    public void rankSuffix(@NotNull CommandPayload<CommandSender> commandPayload, @NotNull VisualRank paperRank, @NotNull @ArgumentName("suffix") String[] suffix) {
        Component suffixComponent = LegacyComponentSerializer.legacySection().deserialize(translateComponent(suffix));
        paperRank.setSuffix(suffixComponent);

        Component[] args = {Component.text(paperRank.getName()), suffixComponent};
        commandPayload.getSender().sendMessage(Component.translatable("server.rank.edit.suffix.set", args));
    }

    @CommandData(value = "rank.edit.suffix.remove")
    public void rankSuffix(@NotNull CommandPayload<CommandSender> commandPayload, @NotNull VisualRank paperRank) {
        paperRank.unsetSuffix();

        Component[] args = {Component.text(paperRank.getName()), Component.text("none")};
        commandPayload.getSender().sendMessage(Component.translatable("server.rank.edit.suffix.set", args));
    }

    @CommandData(value = "rank.edit.chatcolor")
    public void rankRevoke(@NotNull CommandPayload<CommandSender> commandPayload, @NotNull VisualRank paperRank, @NotNull NamedTextColor namedTextColor) {
        paperRank.setColor(namedTextColor);

        Component[] args = {Component.text(paperRank.getName()), Component.text(namedTextColor.toString())};
        commandPayload.getSender().sendMessage(Component.translatable("server.rank.edit.chatcolor.set", args));
    }


}
