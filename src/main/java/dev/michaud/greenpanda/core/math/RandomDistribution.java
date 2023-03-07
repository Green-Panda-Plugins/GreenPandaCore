package dev.michaud.greenpanda.core.math;

import java.util.Random;
import org.jetbrains.annotations.NotNull;

public class RandomDistribution {

  /**
   * Generates a new integer value with an exponential distribution between min and max
   *
   * @param random Random object
   * @param min Minimum value
   * @param max Maximum value
   * @return A random integer value
   */
  public static int nextExponentialInt(@NotNull Random random, int min, int max) {
    return (int) nextExponentialDouble(random, min, max);
  }

  /**
   * Generates a new double value with an exponential distribution between min and max
   * @param random Random object
   * @param min Minimum value
   * @param max Maximum value
   * @return A random double value
   */
  public static double nextExponentialDouble(@NotNull Random random, double min, double max) {
    return (Math.pow(random.nextDouble(), 2) * (max - min) + min);
  }

}