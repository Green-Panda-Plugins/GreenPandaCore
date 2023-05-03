package dev.michaud.greenpanda.core.eventlistener;

import dev.michaud.greenpanda.core.mobs.NaturalSpawnManager;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.jetbrains.annotations.NotNull;

public class ChunkPopulate implements Listener {

  @EventHandler
  private static void onChunkPopulate(@NotNull ChunkPopulateEvent event) {
    Chunk chunk = event.getChunk();
    NaturalSpawnManager.getAnimalValues().forEach(animal -> animal.onChunkPopulate(chunk));
  }

}