package dev.michaud.greenpanda.core.block.data;

import org.bukkit.Location;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ChunkCoordinates {

  public static final byte X_BIT_POS = 0;
  public static final byte X_BIT_SIZE = 4;
  public static final byte Z_BIT_POS = 4;
  public static final byte Z_BIT_SIZE = 4;
  public static final byte Y_SIGN_BIT_POS = 8;
  public static final byte Y_BIT_POS = 9;
  public static final byte Y_BIT_SIZE = 23;

  /**
   * The constant maximum value a Y coordinate can have, 2<sup>23</sup>-1
   */
  public static final int MAX_Y_VALUE = 0x7FFFFF;

  /**
   * The constant minimum value a Y coordinate can have, -2<sup>23</sup>+1 (negative
   * {@link ChunkCoordinates#MAX_Y_VALUE})
   */
  public static final int MIN_Y_VALUE = -0x7FFFFF;

  /**
   * Converts a location to a packed 32-bit integer
   *
   * @param location The location
   * @return A packed integer representing the location's X, Y and Z (All other data is lost)
   */
  public static int toPackedInt(@NotNull Location location) {
    return toPackedInt(
        trueMod(location.getBlockX(), 16),
        location.getBlockY(),
        trueMod(location.getBlockZ(), 16));
  }

  /**
   * Converts x, y, and z chunk coordinates to a packed 32-bit integer
   *
   * @param x The x coordinate, from 0-15
   * @param y The y coordinate, from {@link ChunkCoordinates#MIN_Y_VALUE} to
   *          {@link ChunkCoordinates#MAX_Y_VALUE}
   * @param z The z coordinate, from 0-15
   * @return A packed integer representing all three values
   */
  public static int toPackedInt(int x, int y, int z) {

    if (x < 0 || x > 15) {
      throw new IllegalArgumentException(
          "X value " + x + " is out of range! Must be 0 <= x <= 15.");
    } else if (z < 0 || z > 15) {
      throw new IllegalArgumentException(
          "Z value " + z + " is out of range! Must be 0 <= z <= 15.");
    } else if (y < MIN_Y_VALUE || y > MAX_Y_VALUE) {
      throw new IllegalArgumentException(
          "Y value " + y + " is out of range! Must be " + MIN_Y_VALUE + " <= y <= " + MAX_Y_VALUE);
    }

    int xPacked = (x & ((1 << X_BIT_SIZE) - 1)) << X_BIT_POS;
    int zPacked = (z & ((1 << Z_BIT_SIZE) - 1)) << Z_BIT_POS;
    int yPacked = (Math.abs(y) & ((1 << Y_BIT_SIZE) - 1)) << Y_BIT_POS;
    int ySignPacked = (y >= 0 ? 0 : 1) << Y_SIGN_BIT_POS;

    return xPacked | zPacked | yPacked | ySignPacked;
  }

  /**
   * Converts packed coordinates back into X, Y, and Z values
   *
   * @param packedCoords The integer to unpack
   * @return An integer array of <code>[x, y, z]</code>
   */
  @Contract(value = "_ -> new", pure = true)
  public static int @NotNull [] fromPackedInt(int packedCoords) {

    int x = (packedCoords >> X_BIT_POS) & ((1 << X_BIT_SIZE) - 1);
    int z = (packedCoords >> Z_BIT_POS) & ((1 << Z_BIT_SIZE) - 1);
    int y = (packedCoords >> Y_BIT_POS) & ((1 << Y_BIT_SIZE) - 1);
    int ySign = (packedCoords >> Y_SIGN_BIT_POS) & 1;

    y = ySign == 0 ? y : -y;

    return new int[]{x, y, z};
  }

  @Contract(pure = true)
  public static int trueMod(int a, int b) {
    int result = a % b;
    return result < 0 ? result + b : result;
  }
}