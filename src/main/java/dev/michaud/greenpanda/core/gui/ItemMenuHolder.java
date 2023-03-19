package dev.michaud.greenpanda.core.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Empty holder used to query if a player is interacting with a custom GUI.
 */
public class ItemMenuHolder implements InventoryHolder {

  /**
   * Doesn't have an inventory so this should never be called. Could be left blank but throws an
   * exception to be null safe.
   */
  @Override
  public @NotNull Inventory getInventory() {
    throw new RuntimeException(
        "Calling getInventory() on an empty holder! This holder doesn't have any inventory associated with it");
  }
}