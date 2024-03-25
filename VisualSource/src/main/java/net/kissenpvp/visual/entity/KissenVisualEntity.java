package net.kissenpvp.visual.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.visual.theme.DefaultTheme;
import net.kissenpvp.visual.theme.Theme;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

@AllArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class KissenVisualEntity<S extends ServerEntity> {

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
