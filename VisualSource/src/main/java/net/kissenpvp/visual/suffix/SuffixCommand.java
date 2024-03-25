package net.kissenpvp.visual.suffix;

import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.annotations.ArgumentName;
import net.kissenpvp.core.api.command.annotations.CommandData;
import net.kissenpvp.core.api.command.exception.ArgumentMissingException;
import net.kissenpvp.core.api.time.TemporalObject;
import net.kissenpvp.core.api.time.TimeImplementation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SuffixCommand {

    @CommandData("suffix")
    public void suffix(@NotNull CommandPayload<CommandSender> commandPayload) {
        throw new ArgumentMissingException();
    }

    @CommandData("suffix.grant")
    public void grantSuffix(@NotNull CommandPayload<CommandSender> payload, @NotNull OfflinePlayer offlinePlayer, @NotNull @ArgumentName("name") String name, @NotNull @ArgumentName("suffix") String[] suffix) {
        Runnable setSuffix = () -> {
            String mounted = String.join(" ", suffix).replace('&', '§');
            Component suffixComponent = LegacyComponentSerializer.legacySection().deserialize(mounted);

            TimeImplementation timeImplementation = Bukkit.getKissen().getImplementation(TimeImplementation.class);
            offlinePlayer.grantSuffix(name, suffixComponent);

            Component suffixName = Component.text(name);

            payload.getSender().sendMessage(Component.translatable("server.user.suffix.granted", suffixName, offlinePlayer.displayName()));
            if (offlinePlayer.isOnline()) {
                ((Player) offlinePlayer).sendMessage(Component.translatable("server.user.suffix.received", suffixComponent));
            }
        };

        if (offlinePlayer.getSuffix(name).filter(TemporalObject::isValid).isPresent()) {
            payload.confirmRequest(setSuffix).send();
            return;
        }
        setSuffix.run();
    }

    @CommandData("suffix.revoke")
    public void revokeSuffix(@NotNull CommandPayload<CommandSender> payload, @NotNull @ArgumentName("player") OfflinePlayer offlinePlayer, @NotNull @ArgumentName("name") String name) {
        Component[] arguments = {Component.text(name), offlinePlayer.displayName()};
        if (offlinePlayer.revokeSuffix(name)) {
            payload.getSender().sendMessage(Component.translatable("server.user.suffix.revoked", arguments));
            return;
        }
        payload.getSender().sendMessage(Component.translatable("server.user.suffix.not.found", arguments));
    }
}
