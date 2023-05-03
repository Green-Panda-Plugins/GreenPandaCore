package dev.michaud.greenpanda.core.eventlistener;

import dev.michaud.greenpanda.core.blocks.CustomBlock;
import dev.michaud.greenpanda.core.blocks.CustomBlockRegistry;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BlockPlace implements Listener {

  @EventHandler(ignoreCancelled = true)
  private void onBlockPlace(@NotNull BlockPlaceEvent event) {

    final Player player = event.getPlayer();
    final ItemStack itemStack = event.getItemInHand();
    final Location location = event.getBlock().getLocation();

    for (CustomBlock block : CustomBlockRegistry.getRegistered()) {

      if (block.isType(itemStack)) {
        block.place(location, player, true);
        event.setCancelled(true);
        return;
      }

    }

  }

}