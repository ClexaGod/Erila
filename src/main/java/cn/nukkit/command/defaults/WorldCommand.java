package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author EngincanErgunGG
 */
public class WorldCommand extends VanillaCommand {

    public WorldCommand(String name) {
        super(name, "%nukkit.command.world.description", "/world <list/tp> <dünyaIsmi>");
        this.setPermission("nukkit.command.world");
        this.commandParameters.clear();
        this.commandParameters.put("tp",
                new CommandParameter[]{
                        CommandParameter.newEnum("tp", new String[]{"tp"}),
                        CommandParameter.newEnum("world", false, new String[]{"world"})
                });
        this.commandParameters.put("list",
                new CommandParameter[]{
                        CommandParameter.newEnum("list", new String[]{"list"})
                });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return false;
        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }
        if (!(sender instanceof Player player)) return false;

        switch (args[0].toLowerCase()) {
            case "tp":
                if (args.length < 2) {
                    player.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                    return false;
                }

                String worldName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                if (Server.getInstance().isLevelGenerated(worldName)) {
                    if (Server.getInstance().isLevelLoaded(worldName)) {
                        Level level = Server.getInstance().getLevelByName(worldName);
                        player.teleport(level.getSafeSpawn(true, 0));
                        player.sendMessage(TextFormat.GREEN + worldName + " adlı dünyaya ışınlandınız.");
                    } else {
                        if (Server.getInstance().loadLevel(worldName)) {
                            player.teleport(Server.getInstance().getLevelByName(worldName).getSafeSpawn(true, 0));
                            player.sendMessage(TextFormat.GREEN + worldName + " adlı dünyaya ışınlandınız.");
                        } else {
                            player.sendMessage(TextFormat.RED + "Dünyaya ışınlanma başarısız! Dünya yüklenemedi.");
                        }
                    }
                } else {
                    player.sendMessage(TextFormat.RED + "Böyle bir dünya mevcut değil!");
                }
                break;
            case "list":
                Set<String> levels = Server.getInstance().getLevels().values().stream().map(Level::getFolderName).sorted().collect(Collectors.toCollection(LinkedHashSet::new));
                int size = levels.size();
                player.sendMessage(TextFormat.GREEN + "Şuanda yüklü " + size + " dünya var:");
                player.sendMessage(String.join(", ", levels));
                break;
        }
        return true;
    }
}