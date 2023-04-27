package dev.michaud.greenpanda.core.blocks;

import dev.michaud.greenpanda.core.GreenPandaCore;
import dev.michaud.greenpanda.core.blocks.data.PersistentBlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
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

    currentBlock.setType(Material.BARRIER);

//    currentBlock.setType(Material.NOTE_BLOCK);
//    final NoteBlock data = (NoteBlock) currentBlock.getBlockData();
//
//    data.setInstrument(getInstrument());
//    data.setNote(getNote());
//    data.setPowered(getPowered());
//
//    currentBlock.setBlockData(data);

//    PersistentBlockData.addCustomBlock(customId(), currentBlock);
//
//    Bukkit.getOnlinePlayers().forEach(p -> {
//      p.sendBlockChange(currentBlock.getLocation(), data);
//    });

    debug(currentBlock);

    return true;
  }

  static void debug(@NotNull Block block) {
    String customBlockType = PersistentBlockData.getPersistentData(block, PersistentBlockData.BLOCK_TYPE_KEY, PersistentDataType.STRING);

    GreenPandaCore.getCore().getLogger().info(String.format("Placed custom block at XYZ: %d / %d / %d! CustomBlockType is %s",
        block.getX(), block.getY(), block.getZ(),
        customBlockType == null ? "null" : customBlockType)
    );

  }

}