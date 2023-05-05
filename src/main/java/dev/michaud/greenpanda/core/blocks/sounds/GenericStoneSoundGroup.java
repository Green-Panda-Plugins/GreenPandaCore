package dev.michaud.greenpanda.core.blocks.sounds;

import org.bukkit.Sound;
import org.bukkit.SoundGroup;
import org.jetbrains.annotations.NotNull;

public class GenericStoneSoundGroup implements SoundGroup {

  @Override
  public float getVolume() {
    return 1;
  }

  @Override
  public float getPitch() {
    return 1;
  }

  @Override
  public @NotNull Sound getBreakSound() {
    return Sound.BLOCK_STONE_BREAK;
  }

  @Override
  public @NotNull Sound getStepSound() {
    return Sound.BLOCK_STONE_STEP;
  }

  @Override
  public @NotNull Sound getPlaceSound() {
    return Sound.BLOCK_STONE_PLACE;
  }

  @Override
  public @NotNull Sound getHitSound() {
    return Sound.BLOCK_STONE_HIT;
  }

  @Override
  public @NotNull Sound getFallSound() {
    return Sound.BLOCK_STONE_FALL;
  }
}