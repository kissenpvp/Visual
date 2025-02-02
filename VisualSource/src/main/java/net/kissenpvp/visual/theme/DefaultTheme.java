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

package net.kissenpvp.visual.theme;

import lombok.Getter;
import lombok.Setter;
import net.kissenpvp.visual.api.theme.Theme;
import net.kissenpvp.visual.api.theme.ThemeProvider;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public class DefaultTheme implements Theme {

    @Getter @Setter
    private static NamedTextColor defaultPrimaryColor, defaultSecondaryColor, defaultGeneralColor, defaultEnabledColor, defaultDisabledColor;


    @Override
    public @NotNull TextColor getPrimaryColor() {
        return defaultPrimaryColor;
    }

    @Override
    public @NotNull TextColor getSecondaryColor() {
        return defaultSecondaryColor;
    }

    @Override
    public @NotNull TextColor getGeneralColor() {
        return defaultGeneralColor;
    }

    @Override
    public @NotNull TextColor getEnabledColor() {
        return defaultEnabledColor;
    }

    @Override
    public @NotNull TextColor getDisabledColor() {
        return defaultDisabledColor;
    }

    /**
     * Replaces the colors of the provided components with personalized colors based on their current color values.
     * This method converts each component's color by mapping it to a personalized color obtained from the
     * ColorProviderImplementation.
     *
     * @param component the components to replace colors for.
     * @return a new Component with the replaced colors.
     * @throws NullPointerException if the component array or any of its elements are null.
     */
    @Override
    public @NotNull Component style(@NotNull Component... component) {
        return Component.join(JoinConfiguration.noSeparators(), Arrays.stream(component).map(this::transformComponent).toList());
    }

    /**
     * Converts a Component by replacing its color with a personalized color based on the current color value.
     * This method retrieves the personalized color by querying the ColorProviderImplementation.
     *
     * @param component the Component to convert.
     * @return the converted Component with the personalized color.
     * @throws NullPointerException if the component is null.
     */
    private @NotNull Component transformComponent(@NotNull Component component) {
        return transformComponent(component, getGeneralColor());
    }


    private @NotNull Component transformComponent(@NotNull ComponentLike component, @NotNull TextColor fallBack) {
        return Component.empty().append(component).toBuilder().mapChildrenDeep(buildableComponent -> transformSpecifiedComponent(buildableComponent, fallBack)).asComponent();
    }


    /**
     * Transforms a specified {@link BuildableComponent} by replacing its color with a personalized color based on
     * the current color value.
     *
     * @param buildableComponent the {@link BuildableComponent} to transform.
     * @return the transformed {@link BuildableComponent} with the personalized color.
     * @throws NullPointerException if the buildableComponent is null.
     */
    private @NotNull BuildableComponent<?, ?> transformSpecifiedComponent(@NotNull BuildableComponent<?, ?> buildableComponent, @NotNull TextColor fallBack) {
        if (buildableComponent instanceof TranslatableComponent translatableComponent) {

            return transformTranslatableComponent(translatableComponent, fallBack);
        }

        buildableComponent = legacyColorCodeResolver(buildableComponent);
        TextColor textColor = buildableComponent.color();
        if (textColor!=null) {
            return (BuildableComponent<?, ?>) buildableComponent.color(getPersonalColorByCode(textColor.value()));
        }

        return (BuildableComponent<?, ?>) buildableComponent.color(fallBack);
    }

    @NotNull
    private TranslatableComponent transformTranslatableComponent(@NotNull TranslatableComponent translatableComponent, @NotNull TextColor fallBack) {
        Function<TranslationArgument, Component> argumentMapper = argument -> transformComponent(argument, highlightColor());
        List<Component> transformedArgs = translatableComponent.arguments().stream().map(argumentMapper).toList();

        TextColor textColor = Objects.requireNonNullElse(translatableComponent.color(), fallBack);
        if (!textColor.equals(fallBack)) {
            textColor = getPersonalColorByCode(textColor.value());
        }
        return translatableComponent.color(textColor).arguments(transformedArgs.toArray(new Component[0]));
    }

    @NotNull
    private BuildableComponent<?, ?> legacyColorCodeResolver(@NotNull BuildableComponent<?, ?> buildableComponent) {
        Map<String, TextColor> replacements = new HashMap<>();
        String legacy = LegacyComponentSerializer.legacyAmpersand().serialize(buildableComponent);
        for (String textPassage : legacy.split("§")) {
            if (textPassage.length() > 1) {
                if (legacy.startsWith(textPassage) && !textPassage.startsWith("§")) {
                    continue;
                }
                switch (textPassage.substring(0, 1).toLowerCase()) {
                    case "p" -> replacements.put(textPassage.substring(1), getPrimaryColor());
                    case "s" -> replacements.put(textPassage.substring(1), getSecondaryColor());
                    case "t" -> replacements.put(textPassage.substring(1), getGeneralColor());
                    case "+" -> replacements.put(textPassage.substring(1), getEnabledColor());
                    case "-" -> replacements.put(textPassage.substring(1), getDisabledColor());
                }
            }
        }

        for (Map.Entry<String, TextColor> stringTextColorEntry : replacements.entrySet()) {
            buildableComponent = (BuildableComponent<?, ?>) buildableComponent.replaceText(config -> {
                config.matchLiteral(stringTextColorEntry.getKey());
                config.replacement(Component.text(stringTextColorEntry.getKey()).color(stringTextColorEntry.getValue()));
            });
        }

        return buildableComponent;
    }

    /**
     * Retrieves a personalized TextColor based on the given color value.
     * This method queries the ColorProviderImplementation to obtain the personalized TextColor.
     *
     * @param value the color value to retrieve the personalized TextColor for.
     * @return the personalized TextColor.
     * @throws NullPointerException if the ColorProviderImplementation is null.
     */
    private @NotNull TextColor getPersonalColorByCode(int value) {
        Map<TextColor, TextColor> colorMap = Map.of(ThemeProvider.primary(), getPrimaryColor(), ThemeProvider.secondary(), getSecondaryColor(), ThemeProvider.general(), getGeneralColor(), ThemeProvider.enabled(), getEnabledColor(), ThemeProvider.disabled(), getDisabledColor());
        TextColor color = TextColor.color(value);

        return Optional.ofNullable(colorMap.get(color)).orElse(color);
    }

    @org.jetbrains.annotations.Contract(pure = true)
    private @org.jetbrains.annotations.Nullable TextColor resolveColorValue(int color) {
        return null;
    }

    protected @NotNull TextColor highlightColor() {
        return getPrimaryColor();
    }
}
