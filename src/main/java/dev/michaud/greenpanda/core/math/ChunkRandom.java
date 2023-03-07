package dev.michaud.greenpanda.core.math;

import java.util.Random;
import org.bukkit.Chunk;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ChunkRandom {

  /**
   * Generates a new Random based on the world seed and chunk coordinates
   *
   * @param chunk           Chunk to generate the random for
   * @param uniqueFeatureId Some number to make sure the random is unique for each feature
   * @return Random class based on the given chunk
   */
  @Contract(pure = true, value = "_, _ -> new")
  public static @NotNull Random get(@NotNull Chunk chunk, final long uniqueFeatureId) {
    long seed = chunk.getWorld().getSeed() + uniqueFeatureId;
    return getRandom(chunk.getX(), chunk.getZ(), seed);
  }

  /**
   * Generates a new Random based on the world seed and chunk coordinates
   *
   * @param x               Chunk x position
   * @param z               Chunk z position
   * @param worldSeed       The world seed
   * @param uniqueFeatureId Some number to make sure the random is unique for each feature
   * @return Random class based on the given chunk coordinate
   */
  @Contract(pure = true, value = "_, _, _, _ -> new")
  public static @NotNull Random get(final int x, final int z, final long worldSeed,
      final long uniqueFeatureId) {
    long seed = worldSeed + uniqueFeatureId;
    return getRandom(x, z, seed);
  }

  /**
   * Generates a new Random based on the world seed and chunk coordinates
   *
   * @param seed worldSeed + someNumber (change for each feature)
   * @param x    Chunk x position
   * @param z    Chunk z position
   * @return Random class based on the given chunk
   */
  @Contract(pure = true, value = "_, _, _ -> new")
  protected static @NotNull Random getRandom(final int x, final int z, final long seed) {

    final Random random = new Random(seed);

    long long1 = random.nextLong();
    long long2 = random.nextLong();
    long newSeed = (long) x * long1 ^ (long) z * long2 ^ seed;
    random.setSeed(newSeed);

    return random;

  }

}