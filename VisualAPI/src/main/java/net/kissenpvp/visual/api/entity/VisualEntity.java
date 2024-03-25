package net.kissenpvp.visual.api.entity;

import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

public interface VisualEntity<S extends ServerEntity> {

    @NotNull Component styledName();

    @NotNull Optional<Component> getPrefixComponent();

    @NotNull Optional<Component> getSuffixComponent();

}
