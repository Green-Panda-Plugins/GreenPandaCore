package dev.michaud.greenpanda.core.util;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Makes it easy to use target selectors (e.g. @a, @p, @r, @s)
 */
public class TargetSelectors {

  //TODO: Add support for parameters (like @e[type=panda])

  /**
   * Gets an {@link Entity} list from the given target selector.
   *
   * @param sender The sender of the command.
   * @param arg    The target selector.
   * @return A list of entities, null if the target selector is invalid or no entities were found.
   */
  @Nullable
  public static Entity[] getTargets(CommandSender sender, String arg) {

    Entity[] result = null;
    Location location = null;

    if (sender instanceof Entity entity) {
      location = entity.getLocation();
    } else if (sender instanceof BlockCommandSender cmdBlock) {
      location = cmdBlock.getBlock().getLocation().add(0.5, 0, 0.5);
    }

    if (arg.startsWith("@s")) {

      if (sender instanceof Entity entity) {
        result = new Entity[]{entity};
      }

    } else if (arg.startsWith("@a")) {

      if (location != null) {
        result = location.getWorld().getPlayers().toArray(new Player[0]);
      }

    } else if (arg.startsWith("@p")) {

      Player closest = closestPlayer(location);

      if (closest != null) {
        result = new Entity[]{closest};
      }

    } else if (arg.startsWith("@e")) {

      if (location != null) {
        result = location.getWorld().getEntities().toArray(new Entity[0]);
      }

    } else if (arg.startsWith("@r")) {

      if (location != null) {
        result = new Entity[]{randomPlayer(location.getWorld(), ThreadLocalRandom.current())};
      }

    }

    return result;
  }

  /**
   * Converts a list of entities to a list of players.
   *
   * @param entities The list of entities to convert.
   * @return A list of players, null if there are no players.
   */
  @Nullable
  public static Player[] playerFromEntities(Entity[] entities) {

    if (entities == null) {
      return null;
    }

    List<Player> players = Stream.of(entities).map(Entity::getUniqueId).map(Bukkit::getPlayer)
        .filter(Objects::nonNull).filter(Player::isValid).toList();

    if (players.isEmpty()) {
      return null;
    }

    return players.toArray(new Player[0]);
  }

  /**
   * Gets a random {@link Player} from the given {@link World}.
   *
   * @param world The world to get the player from.
   * @return A random player, null if there are no players.
   */
  @Contract("null, _ -> fail; _, null -> fail")
  public static Player randomPlayer(World world, Random random) {

    if (world == null || random == null) {
      throw new NullPointerException("World and Random cannot be null");
    }

    List<Player> players = world.getPlayers();
    return players.get(random.nextInt(players.size()));
  }

  /**
   * Gets the closest {@link Player} to the given {@link Location}.
   *
   * @param location The location to get the closest player from.
   * @return The closest player, null if there are no players.
   */
  public static Player closestPlayer(Location location) {

    if (location == null) {
      return null;
    }

    Player closest = null;
    double closestDistance = Double.MAX_VALUE;

    for (Player player : location.getWorld().getPlayers()) {
      double distance = player.getLocation().distanceSquared(location);
      if (distance < closestDistance) {
        closest = player;
        closestDistance = distance;
      }
    }
    return closest;
  }

}
