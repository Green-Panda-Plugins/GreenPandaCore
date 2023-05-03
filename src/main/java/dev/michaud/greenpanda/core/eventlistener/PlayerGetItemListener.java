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
 * Listens for when a player gets an item, to pass to {@link PlayerGetItemEvent}.
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

  }

}