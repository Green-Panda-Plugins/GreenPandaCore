package dev.michaud.greenpanda.core.mobs;

import dev.michaud.greenpanda.core.math.RandomDistribution;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.SpawnCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Data about a natural spawn.
 */
public interface NaturalSpawnData {

  /**
   * The maximum amount of this mob to spawn in a group. Must be at least 1.
   */
  int getMaxPackSize();

  /**
   * How often to try to spawn this mob. Vanilla monsters try every tick, and most passive
   * mobs have 1 spawning cycle every 20 seconds (or 400 game ticks).
   *
   * @return The spawn cycle length in ticks.
   */
  int getSpawnCycleDuration();

  /**
   * A unique identifier that is added to the seed to ensure a different random distribution across
   * all mobs. Can be any number, but make sure that it is different for every mob you register.
   */
  long getSeedIdentifier();

  /**
   * The chance for this spawn attempt to continue. Must be a number from 0 to 100. 5 would mean a
   * 5% chance.
   *
   * @param biome The biome this spawn attempt is happening in.
   * @return The chance for this spawn attempt to continue.
   */
  double getSpawnChance(Biome biome);

  /**
   * What category this mob falls under. Used to make sure the mob follows mob capacity rules.
   *
   * @return The mob's spawn category.
   */
  SpawnCategory getCategory();

  /**
   * If this is a valid biome for this mob to spawn.
   *
   * @param biome The biome in question.
   * @return If the mob is allowed to spawn in this biome.
   */
  boolean isValidBiome(Biome biome);

  /**
   * If this is a valid block for this mob to spawn on.
   *
   * @param block The block in question.
   * @return If the mob is allowed to spawn on top of this block.
   */
  boolean isValidBlock(Block block);

  /**
   * Spawns the mob at the given location.
   *
   * @param location The location to spawn the mob.
   * @param random   The random class that was used in generation of this mob (can be used to
   *                 further randomize mob during spawning)
   */
  void spawnMob(Location location, Random random);

  /**
   * Called every spawn cycle and attempts to spawn a mob.
   *
   * @param location The location where the spawn attempt will be centered. This will almost always
   *                 be the location of a player.
   * @param random   The random class to pick numbers from.
   */
  void onSpawnCycle(Location location, Random random);

  /**
   * Starts a pack spawn. Generates a random pack size according to the min and max and attempts to
   * spawn them within a 9x9 radius from the previous attempt.
   *
   * @param startBlock The block to start the spawn attempt
   * @param random     The random class to pick numbers from
   */
  default void startPackSpawn(Block startBlock, @NotNull Random random) {

    Block block = startBlock;

    for (int i = 0; i < getMaxPackSize(); i++) {

      spawnMob(block.getLocation().add(0, 1, 0), random);

      block = chooseNewBlock(block, random);

      if (block == null) {
        return;
      }

    }

  }

  /**
   * Chooses a new block to spawn the mob on in a 9x9 radius from the previous block, with blocks
   * near the center being more likely to be chosen.
   *
   * @param origin The previous block
   * @param random The random class to pick numbers from
   * @return The new block to try to continue the spawn attempt. Null if not a valid block.
   */
  default @Nullable Block chooseNewBlock(Block origin, @NotNull Random random) {

    World world = origin.getWorld();

    int x = RandomDistribution.nextExponentialInt(random, 0, 4);
    int z = RandomDistribution.nextExponentialInt(random, 0, 4);

    if (random.nextBoolean()) {
      x = -x;
    }

    if (random.nextBoolean()) {
      z = -z;
    }

    Block newBlock = world.getHighestBlockAt(
        origin.getX() + x,
        origin.getZ() + z
    );

    if (isValidBlock(newBlock) && isValidBiome(newBlock.getBiome())) {
      return newBlock;
    } else {
      return null;
    }

  }

}