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

import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.annotations.ArgumentName;
import net.kissenpvp.core.api.command.annotations.CommandData;
import net.kissenpvp.core.api.command.exception.ArgumentMissingException;
import net.kissenpvp.core.api.time.TemporalObject;
import net.kissenpvp.core.api.time.TimeImplementation;
import net.kissenpvp.visual.api.entity.VisualPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SuffixCommand {

    @CommandData("suffix")
    public void suffix(@NotNull CommandPayload<CommandSender> commandPayload) {
        throw new ArgumentMissingException();
    }

    @CommandData("suffix.grant")
    public void grantSuffix(@NotNull CommandPayload<CommandSender> payload, @NotNull VisualPlayer player, @NotNull @ArgumentName("name") String name, @NotNull @ArgumentName("suffix") String[] suffix) {
        Runnable setSuffix = () -> {
            String mounted = String.join(" ", suffix).replace('&', '§');
            Component suffixComponent = LegacyComponentSerializer.legacySection().deserialize(mounted);

            TimeImplementation timeImplementation = Bukkit.getPulvinar().getImplementation(TimeImplementation.class);
            player.grantSuffix(name, suffixComponent);

            Component suffixName = Component.text(name);

            payload.getSender().sendMessage(Component.translatable("server.user.suffix.granted", suffixName, player.getParent().displayName()));
            if (player.getParent().isOnline()) {
                ((Player) player.getParent()).sendMessage(Component.translatable("server.user.suffix.received", suffixComponent));
            }
        };

        if (player.getSuffix(name).filter(TemporalObject::isValid).isPresent()) {
            payload.confirmRequest(setSuffix).send();
            return;
        }
        setSuffix.run();
    }

    @CommandData("suffix.revoke")
    public void revokeSuffix(@NotNull CommandPayload<CommandSender> payload, @NotNull VisualPlayer player, @NotNull @ArgumentName("name") String name) {
        Component[] arguments = {Component.text(name), player.getParent().displayName()};
        if (player.revokeSuffix(name)) {
            payload.getSender().sendMessage(Component.translatable("server.user.suffix.revoked", arguments));
            return;
        }
        payload.getSender().sendMessage(Component.translatable("server.user.suffix.not.found", arguments));
    }
}
