package dev.michaud.greenpanda.core.commands;

import dev.michaud.greenpanda.core.mobs.MobCapacity;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpawnCategory;
import org.jetbrains.annotations.NotNull;

public class TestMobCap implements CommandExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
      @NotNull String label, @NotNull String[] args) {

    if (!(sender instanceof Player player)) {
      return true;
    }

    World world = player.getWorld();

    sender.sendMessage(
        Component.text("The global cap for monsters is ")
            .append(Component.text("[" + MobCapacity.getGlobalCap(world, SpawnCategory.MONSTER) + "]"))
            .append(Component.text(" and there are currently "))
            .append(Component.text("[" + MobCapacity.getGlobalCount(world, SpawnCategory.MONSTER) + "]"))
            .append(Component.text(" loaded monsters."))
    );

    sender.sendMessage(
        Component.text("The per-player cap for monsters is ")
            .append(Component.text("[" + MobCapacity.getPerPlayerCap(world, SpawnCategory.MONSTER) + "]"))
            .append(Component.text(" and you are currently loading "))
            .append(Component.text("[" + MobCapacity.getPerPlayerCount(player, SpawnCategory.MONSTER) + "]"))
            .append(Component.text(" monsters."))
    );

    return true;

  }
}