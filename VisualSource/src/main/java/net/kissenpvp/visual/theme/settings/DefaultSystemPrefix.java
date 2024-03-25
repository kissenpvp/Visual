package net.kissenpvp.visual.theme.settings;

import net.kissenpvp.core.api.config.options.OptionString;
import org.jetbrains.annotations.NotNull;

/**
 * Default system prefix setting for appearance customization in Kissen.
 *
 * <p>The {@code DefaultSystemPrefix} class extends {@link OptionString} and represents the default prefix
 * placed in front of system messages. It provides information such as the group, description, default value, and priority.</p>
 *
 * @see OptionString
 */
public class DefaultSystemPrefix extends OptionString
{
    @Override
    public @NotNull String getGroup()
    {
        return "appearance";
    }

    @Override
    public @NotNull String getDescription()
    {
        return "The default prefix placed in front of system messages.";
    }

    @Override
    public @NotNull String getDefault()
    {
        return "KissenPvP";
    }

    @Override
    public int getPriority()
    {
        return 1;
    }
}
