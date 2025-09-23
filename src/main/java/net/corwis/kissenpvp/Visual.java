package net.corwis.kissenpvp;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

public final class Visual extends JavaPlugin implements Listener {

    private static final GsonComponentSerializer serializer = GsonComponentSerializer.gson();
    private VisualManager visualManager;
    private MiniMessage mm;

    public static GsonComponentSerializer serializer() {
        return serializer;
    }

    @Override
    public void onEnable() {
/*        Supplier<IllegalStateException> noConnection = ()  -> new IllegalStateException("Pulvinar connection provider is null");
        DataSource dataSource = Bukkit.getPulvinar().connectionProvider().dataSource().orElseThrow(noConnection);

        VisualRankRepository visualRankRepository = new VisualRankRepository(dataSource);
        VisualSuffixRepository visualSuffixRepository = new VisualSuffixRepository(dataSource);*/

        getServer().getPluginManager().registerEvents(new SystemMessageListener(), this);

        this.mm = MiniMessage.miniMessage();
        saveDefaultConfig();

        this.visualManager = new VisualManager();
        getServer().getPluginManager().registerEvents(this, this);

        applyVisualsToOnlinePlayers();

    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            visualManager.remove(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        visualManager.update(event.getPlayer(), buildVisualDataFromConfig(getConfig()));
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (getConfig().getBoolean("chat.enabled", true)) {
            event.renderer(new VisualChatRenderer(this.visualManager, this));
        }
    }

    public VisualManager getVisualManager() {
        return visualManager;
    }

    public @NonNull Component appendPrefix(@NonNull Component component)
    {
        return prefix.replaceText(builder -> {
            builder.match("%message%");
            builder.replacement(component);
        });
    }

    private Component prefix;

    public VisualData buildVisualDataFromConfig(FileConfiguration cfg) {
        String prefix = cfg.getString("visuals.prefix", "<gray>[<gradient:gray:dark_gray>Spieler</gradient>] ");
        String suffix = cfg.getString("visuals.suffix", "");
        int prio = cfg.getInt("visuals.priority", 100);
        String header = cfg.getString("visuals.header", "<green>Willkommen auf KissenPvP!");
        String footer = cfg.getString("visuals.footer", "<gray>Du bist <b>Spieler</b>.");

        String systemFormat = cfg.getString("chat.system-message-format", "<gray>[<gradient:green:dark_green>System</gradient>] %message%");
        this.prefix = mm.deserialize(systemFormat);

        return new VisualData(
                mm.deserialize(prefix),
                mm.deserialize(suffix),
                prio,
                mm.deserialize(header),
                mm.deserialize(footer)
        );
    }

    public void applyVisualsToOnlinePlayers() {
        FileConfiguration cfg = getConfig();
        for (Player player : Bukkit.getOnlinePlayers()) {
            visualManager.update(player, buildVisualDataFromConfig(cfg));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("visual")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("visual.reload")) {
                    reloadConfig();
                    applyVisualsToOnlinePlayers();

                    sender.sendMessage(MiniMessage.miniMessage().deserialize(
                            getConfig().getString("messages.reload-success",
                                    "<gradient:#FF5E5E:#FFE300>ᴠɪꜱᴜᴀʟ</gradient> <gray>Konfiguration neu geladen.</gray>")
                    ));
                } else {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(
                            getConfig().getString("messages.no-permission",
                                    "<gradient:#FF5E5E:#FFE300>ᴠɪꜱᴜᴀʟ</gradient> <red>Keine Berechtigung.</red>")
                    ));
                }
                return true;
            }
        }
        return false;
    }
}
