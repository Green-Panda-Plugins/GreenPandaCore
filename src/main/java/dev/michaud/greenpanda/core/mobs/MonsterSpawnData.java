package dev.michaud.greenpanda.core.mobs;

import dev.michaud.greenpanda.core.math.RandomLocation;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * Data about a monster spawn. Mobs registered as a monster will only spawn under a light level of 0.
 */
public abstract class MonsterSpawnData implements NaturalSpawnData {

  final int lightLevel = 0;

  public boolean checkLightLevel(@NotNull Block block) {
    return block.getLightLevel() <= lightLevel;
  }

  @Override
  public void onSpawnCycle(@NotNull Location center, Random random) {

    final RandomLocation rand = new RandomLocation(random);
    final Location location = rand.inAnnulus(center, 25, 128);
    final Block block = rand.getRandomBlockYAt(location);
    final Biome biome = block.getBiome();

    if (!MobCapacity.canFit(getCategory(), block.getLocation())) {
      return;
    }

    if (random.nextInt(100) < getSpawnChance(biome)
        && isValidBlock(block)
        && isValidBiome(biome)
        && checkLightLevel(block)) {
      startPackSpawn(block, random);
    }

  }

}