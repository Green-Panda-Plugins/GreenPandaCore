package dev.michaud.greenpanda.core.eventlistener;

import dev.michaud.greenpanda.core.event.CustomBlockPlaceEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class BlockPlace implements Listener {

  @EventHandler(ignoreCancelled = true)
  private void onBlockPlace(@NotNull CustomBlockPlaceEvent event) {
    event.getPlayer().sendMessage("Placed " + event.getCustomBlock().blockId() + "!");
  }

}