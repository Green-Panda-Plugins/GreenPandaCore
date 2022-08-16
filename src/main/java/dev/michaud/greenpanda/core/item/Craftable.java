package dev.michaud.greenpanda.core.item;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

public interface Craftable extends CustomItem {

  /**
   * Gets the namespace key for the recipe.
   *
   * @return The recipe namespace key for this item;
   */
  @NotNull NamespacedKey namespacedKey();

  /**
   * Gets the recipe for crafting this item.
   *
   * @return The recipe for this item.
   */
  @NotNull Recipe recipe();

}
