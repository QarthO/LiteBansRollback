package gg.quartzdev.mc.litebansrollback;

import java.util.List;
import java.util.UUID;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import litebans.api.Entry;
import litebans.api.Events;
import net.coreprotect.CoreProtect;

public final class LiteBansRollback extends JavaPlugin
{

    private static final int COLUMNS = 9;
    private static final int CANVAS_SIZE = 2001;


    @Override
    public void onLoad() {

        CommandAPICommand baseCommand = new CommandAPICommand("litebansrollback")
        .withAliases("lbrollback", "lbr")
        .withPermission("litebansrollback.commands")
        .executesPlayer((player, args) -> {
            player.sendRichMessage("<green>LiteBansRollback v" + getPluginMeta().getVersion());
        });

        baseCommand.register();
    }

    @Override
    public void onEnable()
    {
        Metrics metrics = new Metrics(this, 26864);
        CommandAPI.onEnable();
        registerEvents();
    }

    @Override
    public void onDisable()
    {
        CommandAPI.onDisable();
        // Plugin shutdown logic
    }

    public void registerEvents() {
        Events.get().register(new Events.Listener() {
            @Override
            public void entryAdded(Entry entry) {
                if (!entry.getType().equalsIgnoreCase("ban")) return;
                if (entry.getDuration() != -1) return;
                rollbackUsersBlocks(UUID.fromString(entry.getUuid()));
            }
        });
    }

    public void rollbackUsersBlocks(UUID uuid) {
        int time = -1;
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        String username = offlinePlayer.getName();
        List<String> usersToRollback = List.of(username);
        CoreProtect.getInstance().getAPI().performRollback(
            time, usersToRollback, null, null, null, null, -1, null
        );
        log("[LiteBansRollback] <green>Rollbacked <yellow>" + username + "'s</yellow> actions");
    }

    public void log(String message) {
        Bukkit.getConsoleSender().sendRichMessage(message);
    }

}
