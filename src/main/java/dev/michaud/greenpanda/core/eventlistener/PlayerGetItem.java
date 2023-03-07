package dev.michaud.greenpanda.core.eventlistener;

import dev.michaud.greenpanda.core.event.PlayerGetItemEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerGetItem implements Listener {

  @EventHandler
  public void onPlayerGetItem(@NotNull PlayerGetItemEvent event) {

    Player player = event.getPlayer();
    ItemStack item = event.getItem();

    if (item == null || item.getType().isEmpty()) {
      return;
    }

  }

  private void searchForRecipeToGive() {

  }

}