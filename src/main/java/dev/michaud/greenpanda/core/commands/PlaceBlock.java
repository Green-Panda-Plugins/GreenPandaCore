package dev.michaud.greenpanda.core.commands;

import dev.michaud.greenpanda.core.blocks.TestBlock;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceBlock implements CommandExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
      @NotNull String label, @NotNull String[] args) {

    if (!(sender instanceof Player player)) {
      return true;
    }

    Location location = player.getLocation().add(1, 0, 0);
    TestBlock newBlock = new TestBlock();

    newBlock.place(location, player, true);

    return true;
  }

}