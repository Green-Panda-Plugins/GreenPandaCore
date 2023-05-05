package dev.michaud.greenpanda.core.eventlistener;

import dev.michaud.greenpanda.core.block.data.PersistentBlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class BlockBreak implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  private void onBlockBreak(@NotNull BlockBreakEvent event) {
    //We don't need to check if this is a custom block first, because if it's not then nothing
    //will change anyway
    PersistentBlockData.removeCustomBlock(event.getBlock());
  }

}