package dev.michaud.greenpanda.core.eventlistener;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.jetbrains.annotations.NotNull;

public class BlockUpdateEvent implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockUpdate(@NotNull BlockPhysicsEvent event) {

  }

  private void updateAndCheck(Block block) {


  }

}