package dev.michaud.greenpanda.core.eventlistener;

import dev.michaud.greenpanda.core.gui.ItemMenuHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemMenuListener implements Listener {

  @EventHandler
  public void onMenuClick(@NotNull InventoryClickEvent event) {

    final Inventory inventory = event.getClickedInventory();
    final Player player = (Player) event.getWhoClicked();
    final ItemStack item = event.getCurrentItem();

    if (inventory == null) {
      return;
    }

    if (inventory.getHolder() instanceof ItemMenuHolder) {

      if (player.getItemOnCursor().getType().isEmpty()
          && item != null && !item.getType().isEmpty()) {
        player.setItemOnCursor(item);
      }

      event.setCancelled(true);
    }
  }

}