package dev.michaud.greenpanda.core.blocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CustomBlock implements CustomBlockData {

  /**
   * Places the custom block at the given location
   *
   * @param location The location to place the block
   * @param player The player who placed the block (or null if no player was involved)
   * @param replace Whether to replace the current block at the given location if it is not air
   * @return If the block was placed or not
   */
  public boolean place(@NotNull Location location, @Nullable Player player, boolean replace) {

    final Block currentBlock = location.getBlock();

    if (!replace && !currentBlock.isEmpty()) {
      return false;
    }

    currentBlock.setType(Material.NOTE_BLOCK);
    NoteBlock data = (NoteBlock) currentBlock.getBlockData();

    data.setInstrument(getInstrument());
    data.setNote(getNote());
    data.setPowered(getPowered());

    currentBlock.setBlockData(data);

    return true;
  }

}