package dev.michaud.greenpanda.core.eventlistener;

import dev.michaud.greenpanda.core.event.PlayerGetItemEvent;
import dev.michaud.greenpanda.core.event.PlayerGetItemEvent.PlayerGetType;
import dev.michaud.greenpanda.core.item.RecipeUnlockable;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Listens for when a player gets an item in order to unlock recipes.
 *
 * @see RecipeUnlockable
 */
public class PlayerGetItemListener implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  private void onPlayerPickupItem(@NotNull EntityPickupItemEvent event) {

    if (!(event.getEntity() instanceof HumanEntity)) {
      return;
    }

    final Player player = (Player) event.getEntity();
    final ItemStack eventItem = event.getItem().getItemStack();
    final PlayerGetItemEvent newEvent = new PlayerGetItemEvent(player, eventItem,
        PlayerGetType.ITEM_PICKUP);

    Bukkit.getPluginManager().callEvent(newEvent);

    if (newEvent.isCancelled()) {
      event.setCancelled(true);
    }

    /*  Collection<RecipeUnlockable> customItems = ItemRegistry.getValues(RecipeUnlockable.class);

    for (RecipeUnlockable unlockable : customItems) {

      if (checkItem(player, eventItem.getType(), unlockable)) {
        return;
      }

    }*/

  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  private void onPlayerInventoryEvent(@NotNull InventoryClickEvent event) {

    final Player player = (Player) event.getWhoClicked();
    final Inventory clickedInventory = event.getClickedInventory();
    ItemStack item = event.getCurrentItem();

    if (item == null || clickedInventory == null) {
      return;
    }

    boolean playerInventory = clickedInventory.getType() == InventoryType.PLAYER;

    boolean validEvent = switch (event.getAction()) {
      case HOTBAR_SWAP, HOTBAR_MOVE_AND_READD, MOVE_TO_OTHER_INVENTORY -> !playerInventory;
      case PLACE_ALL, PLACE_ONE, PLACE_SOME, SWAP_WITH_CURSOR -> {
        item = event.getCursor();
        yield playerInventory;
      }
      default -> false;
    };

    if (!validEvent || item == null || item.getType().isEmpty()) {
      return;
    }

    final PlayerGetItemEvent newEvent = new PlayerGetItemEvent(player, item,
        PlayerGetType.FROM_CONTAINER);

    Bukkit.getPluginManager().callEvent(newEvent);

    if (newEvent.isCancelled()) {
      event.setCancelled(true);
    }

    //Collection<RecipeUnlockable> customItems = ItemRegistry.getValues(RecipeUnlockable.class);

/*    for (RecipeUnlockable unlockable : customItems) {

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

    }*/

  }

/*
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
*/

}