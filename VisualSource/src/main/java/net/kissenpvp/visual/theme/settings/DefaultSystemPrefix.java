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
