package dev.michaud.greenpanda.core.item;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a craftable custom item that can be unlocked by obtaining a given {@link Material}.
 */
public interface RecipeUnlockable extends Craftable {

  /**
   * Gets the {@link Material} that is required to unlock the recipe for this item.
   *
   * @return The required material.
   */
  @NotNull Material recipeRequirement();

}