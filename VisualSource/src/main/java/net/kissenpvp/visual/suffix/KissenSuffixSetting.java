package net.kissenpvp.visual.suffix;

import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.time.TemporalObject;
import net.kissenpvp.core.api.user.playersettting.UserValue;
import net.kissenpvp.visual.Visual;
import net.kissenpvp.visual.api.suffix.Suffix;
import net.kissenpvp.visual.api.suffix.playersetting.SuffixSetting;
import net.kissenpvp.visual.entity.KissenVisualPlayer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KissenSuffixSetting implements SuffixSetting
{

    @Override
    public @NotNull String serialize(@NotNull String object)
    {
        return object;
    }

    @Override
    public @NotNull String deserialize(@NotNull String input)
    {
        return input;
    }

    @Override
    public @NotNull String getKey()
    {
        return "suffix";
    }

    @Override
    public @NotNull String getDefaultValue(@NotNull OfflinePlayer playerClient)
    {
        return "none";
    }

    @Override
    public @NotNull UserValue<String>[] getPossibleValues(@NotNull OfflinePlayer playerClient)
    {
        Visual visual = Visual.getPlugin(Visual.class);
        KissenVisualPlayer visualPlayer = (KissenVisualPlayer) visual.getEntity(playerClient);

        Stream<Suffix> suffixStream = visualPlayer.getSuffixes().stream().filter(TemporalObject::isValid);
        Function<Suffix, UserValue<String>> transform = suffix -> new UserValue<>(suffix.getName());
        Set<UserValue<String>> settings = suffixStream.map(transform).collect(Collectors.toSet());
        settings.add(new UserValue<>("none"));
        return settings.toArray(UserValue[]::new);
    }




}
