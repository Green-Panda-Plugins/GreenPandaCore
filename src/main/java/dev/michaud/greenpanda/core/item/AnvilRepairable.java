package dev.michaud.greenpanda.core.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an item that can be repaired in an anvil
 */
public interface AnvilRepairable extends CustomItem {

  /**
   * If this item stack can be used in the anvil to repair this item
   *
   * @param item The item
   * @return True if the item can be repaired with this item
   */
  boolean validRepairMaterial(ItemStack item);


  /**
   * Get the data of the given repair.
   *
   * @param firstItem  The item being repaired.
   * @param secondItem The item being used to repair.
   * @param renameText The name to be applied to the repaired item. An empty string if no name
   *                   should be applied.
   * @return An instance of {@link AnvilRepairData} with the appropriate data.
   */
  @Nullable
  AnvilRepairData anvilRepair(ItemStack firstItem, ItemStack secondItem, String renameText);

}