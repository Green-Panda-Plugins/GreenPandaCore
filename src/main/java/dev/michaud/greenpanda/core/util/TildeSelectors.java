package dev.michaud.greenpanda.core.util;

import dev.michaud.greenpanda.core.config.ServerProperties;
import dev.michaud.greenpanda.core.math.VectorMath;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Makes it easy to use tilde (~) and caret (^) selectors.
 */
public class TildeSelectors {

  /**
   * Gets a {@link Location} from the given tilde or caret selectors
   *
   * @param sender The sender of the command
   * @param args The selectors to parse
   * @return A location if args were valid, otherwise null
   */
  public static @Nullable Location getLocation(CommandSender sender, String[] args) {

    if (args == null || args.length != 3) {
      throw new IllegalArgumentException("Expected 3 arguments");
    }

    Location senderLocation;

    if (sender instanceof Entity entity) {
      senderLocation = entity.getLocation();
    } else if (sender instanceof BlockCommandSender cmdBlock) {
      senderLocation = cmdBlock.getBlock().getLocation().add(0.5, 0, 0.5);
    } else {
      senderLocation = ServerProperties.getDefaultWorldSpawn();
    }

    try {
      return parseCommandLocation(senderLocation, args);
    } catch (NumberFormatException | IndexOutOfBoundsException e) {
      return null;
    }

  }

  private static @Nullable Location parseCommandLocation(Location senderLocation, String @NotNull [] args) {

    String x = args[0];
    String y = args[1];
    String z = args[2];

    if (x == null || y == null || z == null) {
      return null;
    }

    if (x.startsWith("^") && y.startsWith("^") && z.startsWith("^")) {
      return getCaretLocation(senderLocation, x, y, z);
    } else {
      return getTildeOrAbsoluteLocation(senderLocation, x, y, z);
    }

  }

  /**
   * Finds the {@link Location} from tildes or absolute numbers (or a mixture of the two)
   *
   * @param senderLocation The location of the sender
   * @param x The x arg
   * @param y The y arg
   * @param z The z arg
   * @return The parsed {@link Location}
   */
  public static @NotNull Location getTildeOrAbsoluteLocation(@NotNull Location senderLocation, String x, String y, String z) {

    double xCoord = parseTilde(senderLocation.getX(), x);
    double yCoord = parseTilde(senderLocation.getY(), y);
    double zCoord = parseTilde(senderLocation.getZ(), z);

    return new Location(senderLocation.getWorld(), xCoord, yCoord, zCoord);

  }

  /**
   * Parses a single tilde. If the argument starts with ~, the result is relative to the original
   * number. Otherwise, the str is returned.
   *
   * @param original The origin the tilde is relative to
   * @param str The argument to parse
   * @return A double equivalent to {@code original + str} if str begins with {@code "~"},
   * otherwise just str.
   */
  public static double parseTilde(double original, @NotNull String str) {

    if (str.startsWith("~")) {
      return original + Double.parseDouble(str.substring(1));
    } else {
      return Double.parseDouble(str);
    }

  }

  /**
   * Finds the relative local location using caret notation.
   *
   * @param senderLocation The location of the sender of the command
   * @param x The x arg
   * @param y The y arg
   * @param z The z arg
   * @return A {@link Location}, relevant to the sender's location by the given arguments
   */
  public static @NotNull Location getCaretLocation(@NotNull Location senderLocation, String x, String y, String z) {

    float pitch = VectorMath.clampAngle(senderLocation.getPitch());
    float yaw = VectorMath.clampAngle(senderLocation.getYaw());

    Vector lookVec = VectorMath.getVectorForRotation(pitch, yaw);

    double xNum = (parseCaret(x) * lookVec.getX()) + senderLocation.getX();
    double yNum = (parseCaret(y) * lookVec.getY()) + senderLocation.getY();
    double zNum = (parseCaret(z) * lookVec.getZ()) + senderLocation.getZ();

    return new Location(senderLocation.getWorld(), xNum, yNum, zNum);

  }

  /**
   * Parses a single caret argument
   *
   * @param str The argument to parse
   * @return A double, equal to the number after the caret, or 0 if str = "^"
   */
  public static double parseCaret(@NotNull String str) {

    if (str.equals("^")) {
      return 0;
    } else {
      return Double.parseDouble(str.substring(1));
    }

  }

}