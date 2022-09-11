package dev.michaud.greenpanda.core.eventlistener;

import dev.michaud.greenpanda.core.item.ItemRegistry;
import dev.michaud.greenpanda.core.item.RecipeUnlockable;
import java.util.Collection;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Listens for when a player gets an item in order to unlock recipes.
 * @see RecipeUnlockable
 */
public class PlayerGetItemListener implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  private void onPlayerPickupItem(@NotNull EntityPickupItemEvent event) {

    if (!(event.getEntity() instanceof HumanEntity player)) {
      return;
    }

    ItemStack eventItem = event.getItem().getItemStack();
    Collection<RecipeUnlockable> customItems = ItemRegistry.getValues(RecipeUnlockable.class);

    for (RecipeUnlockable unlockable : customItems) {

      if (checkItem(player, eventItem.getType(), unlockable)) {
        return;
      }

    }

  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  private void onPlayerInventoryEvent(@NotNull InventoryClickEvent event) {

    HumanEntity player = event.getWhoClicked();

    ItemStack currentItem = event.getCurrentItem();
    Collection<RecipeUnlockable> customItems = ItemRegistry.getValues(RecipeUnlockable.class);

    for (RecipeUnlockable unlockable : customItems) {

      if (currentItem == null) {
        break;
      }

      if (checkItem(player, currentItem.getType(), unlockable)) {
        return;
      }

    }

    for (RecipeUnlockable unlockable : customItems) {

      if (checkInventory(player, unlockable)) {
        return;
      }

    }

  }

  private static boolean checkInventory(@NotNull HumanEntity player,
      @NotNull RecipeUnlockable unlockable) {

    for (ItemStack item : player.getInventory().getContents()) {

      if (item == null) {
        continue;
      }

      if (checkItem(player, item.getType(), unlockable)) {
        return true;
      }

    }

    return false;

  }


  private static boolean checkItem(HumanEntity player, Material eventMaterial,
      @NotNull RecipeUnlockable unlockable) {

    if (eventMaterial == unlockable.recipeRequirement()) {
      player.discoverRecipe(unlockable.namespacedKey());
      return true;
    }

    return false;

  }

}