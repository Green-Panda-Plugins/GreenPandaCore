package dev.michaud.greenpanda.core.commands;

import dev.michaud.greenpanda.core.item.CustomItem;
import dev.michaud.greenpanda.core.item.ItemRegistry;
import dev.michaud.greenpanda.core.util.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Custom command to give a custom item to a player. Uses the {@link ItemRegistry} to get items.
 */
public class GiveItem implements TabExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
      @NotNull String label, @NotNull String[] args) {

    if (args.length < 2) {
      return false;
    }

    Player[] targets = Target.playerFromEntities(Target.getTargets(sender, args[0]));

    if (targets == null) {
      Player target = Bukkit.getPlayer(args[0]);
      if (target == null) {
        return false;
      }
      targets = new Player[]{target};
    }

    String itemName = String.join(" ", Arrays.asList(args).subList(1, args.length));
    CustomItem item = ItemRegistry.findCustomItem(itemName);

    if (item == null) {
      return false;
    }

    for (Player p : targets) {
      p.getInventory().addItem(item.makeItem());
    }

    return true;
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
      @NotNull Command command, @NotNull String label, @NotNull String[] args) {

    if (args.length == 1) {
      List<String> list = new ArrayList<>();
      list.add("@a");
      list.add("@p");
      list.add("@r");
      list.add("@s");

      for (Player player : Bukkit.getServer().getOnlinePlayers()) {
        list.add(player.getName());
      }

      return list;
    }

    if (args.length == 2) {
      List<String> list = new ArrayList<>();
      ItemRegistry.getMap().forEach((k, v) -> list.add(k));

      return list;
    }

    return null;
  }
}
