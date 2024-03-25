package net.kissenpvp.visual.api.rank;

import net.kissenpvp.paper.api.user.rank.PaperRank;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface VisualRank extends PaperRank {

    @NotNull
    TextColor getColor();

    void setColor(@Nullable TextColor textColor);

    void unsetColor();

    @NotNull
    Optional<Component> getPrefix();

    void setPrefix(@Nullable Component component);

    void unsetPrefix();

    @NotNull
    Optional<Component> getSuffix();

    void setSuffix(@Nullable Component component);

    void unsetSuffix();

}
