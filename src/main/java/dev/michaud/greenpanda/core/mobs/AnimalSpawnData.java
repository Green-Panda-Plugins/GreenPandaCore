package dev.michaud.greenpanda.core.mobs;

import dev.michaud.greenpanda.core.math.ChunkRandom;
import dev.michaud.greenpanda.core.math.RandomLocation;
import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * Data about an animal spawn. Mobs registered as an animal will spawn naturally on chunk
 * generation, as well as according to their spawn cycle.
 * <p>
 * Randomness for animal spawning is derived from the world seed, which means that worlds with the
 * same seed always generate chunks with the same animals in the same places.
 */
public abstract class AnimalSpawnData implements NaturalSpawnData {

  /**
   * Called at the very end of chunk generation. Gets a random block in the chunk and, if valid,
   * starts a pack spawn.
   *
   * @param chunk The chunk pertaining to the event
   */
  public void onChunkPopulate(@NotNull Chunk chunk) {

    World world = chunk.getWorld();
    Random random = ChunkRandom.get(chunk, getSeedIdentifier());

    int randomX = random.nextInt(16);
    int randomZ = random.nextInt(16);

    Location randomLoc = chunk.getBlock(randomX, 0, randomZ).getLocation();
    Block block = world.getHighestBlockAt(randomLoc);
    Biome biome = block.getBiome();

    if (random.nextInt(100) < getSpawnChance(biome)
        && isValidBlock(block)
        && isValidBiome(biome)) {
      startPackSpawn(block, random);
    }

  }

  @Override
  public void onSpawnCycle(@NotNull Location centerLocation, Random random) {

    final World world = centerLocation.getWorld();
    final RandomLocation randLoc = new RandomLocation(random);
    final Location randomPos = randLoc.inAnnulus(centerLocation, 25, 128);
    final Block block = world.getHighestBlockAt(randomPos);
    final Biome biome = block.getBiome();

    if (!MobCapacity.canFit(getCategory(), block.getLocation())) {
      return;
    }

    if (random.nextInt(100) < getSpawnChance(biome)
        && isValidBlock(block)
        && isValidBiome(biome)) {
      startPackSpawn(block, random);
    }

  }

}