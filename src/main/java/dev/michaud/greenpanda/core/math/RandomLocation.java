package dev.michaud.greenpanda.core.math;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class RandomLocation {

  private final Random random;

  public RandomLocation() {
    this.random = new Random();
  }

  public RandomLocation(Random random) {
    this.random = random;
  }

  /**
   * Generates a random number inside a circle
   *
   * @param radius  Radius of the circle
   * @param centerX Center of the circle on the X axis
   * @param centerY Center of the circle on the Y axis
   * @return A random location inside the circle
   */
  public Vector2 inCircle(float radius, float centerX, float centerY) {

    double r = radius * Math.sqrt(random.nextDouble());
    double theta = random.nextDouble() * 2 * Math.PI;

    float x = (float) (centerX + r * Math.cos(theta));
    float y = (float) (centerY + r * Math.sin(theta));

    return new Vector2(x, y);

  }

  /**
   * Generates a random number inside a circle
   *
   * @param around Location around which the circle will be generated
   * @param radius Radius of the circle
   * @return A random location inside the circle
   */
  public Location inCircle(Location around, float radius) {

    float centerX = (float) around.getX();
    float centerY = (float) around.getZ();

    Vector2 v = inCircle(radius, centerX, centerY);

    return new Location(around.getWorld(), v.x, around.getY(), v.y, around.getYaw(),
        around.getPitch());

  }

  /**
   * Generates a random number inside an annulus (ring)
   *
   * @param minRadius Minimum radius of the annulus
   * @param maxRadius Maximum radius of the annulus
   * @param centerX   Center of the annulus on the X axis
   * @param centerY   Center of the annulus on the Y axis
   * @return A random location inside the annulus
   */
  public Vector2 inAnnulus(float minRadius, float maxRadius, float centerX, float centerY) {

    double r = Math.sqrt(random.nextDouble() * (maxRadius * maxRadius - minRadius * minRadius)
        + minRadius * minRadius);
    double theta = random.nextDouble() * 2 * Math.PI;

    float x = (float) (centerX + r * Math.cos(theta));
    float y = (float) (centerY + r * Math.sin(theta));

    return new Vector2(x, y);

  }

  /**
   * Generates a random number inside an annulus (ring)
   *
   * @param around    Location around which the annulus will be generated
   * @param minRadius Minimum radius of the annulus
   * @param maxRadius Maximum radius of the annulus
   * @return A random location inside the annulus
   */
  public Location inAnnulus(@NotNull Location around, float minRadius, float maxRadius) {

    float centerX = (float) around.getX();
    float centerY = (float) around.getZ();

    Vector2 v = inAnnulus(minRadius, maxRadius, centerX, centerY);

    return new Location(around.getWorld(), v.x, around.getY(), v.y, around.getYaw(),
        around.getPitch());

  }

  /**
   * Gets a block at the given x and z coordinates with a random y value between the minimum world
   * height and the highest solid block.
   *
   * @param location The location to randomize a y coordinate for
   * @return A random block at the same x and z coordinates as the given location
   */
  public @NotNull Block getRandomBlockYAt(@NotNull Location location) {

    final int y;
    final int x = location.getBlockX();
    final int z = location.getBlockZ();
    final World world = location.getWorld();

    int max = world.getHighestBlockYAt(x, z);
    int min = world.getMinHeight();

    if (min >= max) {
      y = max;
    } else {
      y = random.nextInt(min, max + 1);
    }

    return world.getBlockAt(x, y, z);

  }

}