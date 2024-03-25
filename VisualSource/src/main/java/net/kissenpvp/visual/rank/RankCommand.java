package net.kissenpvp.visual.rank;

import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.annotations.ArgumentName;
import net.kissenpvp.core.api.command.annotations.CommandData;
import net.kissenpvp.core.api.command.exception.ArgumentMissingException;
import net.kissenpvp.core.api.user.rank.Rank;
import net.kissenpvp.paper.api.user.rank.PaperRank;
import net.kissenpvp.visual.Visual;
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
    public void rankPrefix(@NotNull CommandPayload<CommandSender> commandPayload, @NotNull PaperRank paperRank, @NotNull @ArgumentName("prefix") String[] prefix) {
        Component prefixComponent = LegacyComponentSerializer.legacySection().deserialize(translateComponent(prefix));
        translate(paperRank).setPrefix(prefixComponent);

        Component[] args = {Component.text(paperRank.getName()), prefixComponent};
        commandPayload.getSender().sendMessage(Component.translatable("server.rank.edit.prefix.set", args));
    }

    @CommandData(value = "rank.edit.prefix.remove")
    public void rankPrefix(@NotNull CommandPayload<CommandSender> commandPayload, @NotNull PaperRank paperRank) {
        translate(paperRank).unsetPrefix();

        Component[] args = {Component.text(paperRank.getName()), Component.text("none")};
        commandPayload.getSender().sendMessage(Component.translatable("server.rank.edit.prefix.set", args));
    }

    @CommandData(value = "rank.edit.suffix")
    public void rankSuffix(@NotNull CommandPayload<CommandSender> commandPayload, @NotNull PaperRank paperRank, @NotNull @ArgumentName("suffix") String[] suffix) {
        Component suffixComponent = LegacyComponentSerializer.legacySection().deserialize(translateComponent(suffix));
        translate(paperRank).setSuffix(suffixComponent);

        Component[] args = {Component.text(paperRank.getName()), suffixComponent};
        commandPayload.getSender().sendMessage(Component.translatable("server.rank.edit.suffix.set", args));
    }

    @CommandData(value = "rank.edit.suffix.remove")
    public void rankSuffix(@NotNull CommandPayload<CommandSender> commandPayload, @NotNull PaperRank paperRank) {
        translate(paperRank).unsetSuffix();

        Component[] args = {Component.text(paperRank.getName()), Component.text("none")};
        commandPayload.getSender().sendMessage(Component.translatable("server.rank.edit.suffix.set", args));
    }

    @CommandData(value = "rank.edit.chatcolor")
    public void rankRevoke(@NotNull CommandPayload<CommandSender> commandPayload, @NotNull PaperRank paperRank, @NotNull NamedTextColor namedTextColor) {
        translate(paperRank).setColor(namedTextColor);

        Component[] args = {Component.text(paperRank.getName()), Component.text(namedTextColor.toString())};
        commandPayload.getSender().sendMessage(Component.translatable("server.rank.edit.chatcolor.set", args));
    }

    private @NotNull KissenVisualRank translate(@NotNull Rank rank) {
        Visual visual = Visual.getPlugin(Visual.class);
        return visual.getRank(rank);
    }

}
