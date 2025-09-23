package net.corwis.kissenpvp.theme;

import net.corwis.kissenpvp.api.theme.Theme;
import net.corwis.kissenpvp.api.theme.ThemeColor;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jspecify.annotations.NonNull;

import java.util.*;

public abstract class AbstractTheme implements Theme
{
    private static final int GENERAL = 112114105;
    private static final int HIGHLIGHT = 11510199;


    @Override public @NonNull Component style(@NonNull Component... component)
    {
        return Component.join(JoinConfiguration.noSeparators(), Arrays.stream(component).map(this::styleComponent).toList());
    }

    private @NonNull Component styleComponent(@NonNull Component component)
    {
        return styleComponent(component, color(ThemeColor.GENERAL));
    }

    private @NonNull Component styleComponent(@NonNull ComponentLike component, @NonNull TextColor fallBack)
    {
        TextComponent.Builder builder = Component.empty().append(component).toBuilder();
        return builder.mapChildrenDeep(buildableComponent -> styleComponent(buildableComponent, fallBack)).asComponent();
    }

    private @NonNull BuildableComponent<?, ?> styleComponent(@NonNull BuildableComponent<?, ?> buildableComponent, @NonNull TextColor fallBack)
    {
        if (buildableComponent instanceof TranslatableComponent translatableComponent)
        {
            return styleTranslatable(translatableComponent, fallBack);
        }

        TextColor textColor = Objects.requireNonNullElse(buildableComponent.color(), fallBack);
        return (BuildableComponent<?, ?>) buildableComponent.color(textColor);
    }

    @NonNull private TranslatableComponent styleTranslatable(@NonNull TranslatableComponent message, @NonNull TextColor fallBack)
    {
        List<Component> styledArguments = message.arguments().stream().map(argument ->
        {
            TextColor fallback = color(ThemeColor.HIGHLIGHT);
            return styleComponent(argument, fallback);
        }).toList();

        TextColor textColor = Objects.requireNonNullElse(message.color(), fallBack);
        return message.color(textColor).arguments(styledArguments.toArray(new Component[0]));
    }
}
