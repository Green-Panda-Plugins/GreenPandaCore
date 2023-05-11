package dev.michaud.greenpanda.core.block;

import org.bukkit.SoundGroup;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents information about a custom block. This interface only holds information about the
 * block. To create a new block, extend {@link CustomBlock}.
 */
public interface CustomBlockData {

  /**
   * Gets the unique identifier for this item. This is used to keep track of the block and item, so
   * make sure it's not a duplicate.
   *
   * @return The unique identifier for this block's item.
   */
  @NotNull String blockId();

  /**
   * Gets the items this block will drop when broken with a given tool.
   *
   * @param tool The tool the block is broken with
   * @return A list of items the block drops
   */
  @NotNull ItemStack[] getDrops(ItemStack tool);

  /**
   * Get the {@link NoteblockState} that this block overrides. This must be unique among every
   * plugin that registers a custom block.
   *
   * @return The noteblock state
   */
  @NotNull NoteblockState getNoteblockState();

  /**
   * Gets the {@link SoundGroup} of this block. If null, no sounds will play.
   *
   * @return The sound group
   */
  @Nullable SoundGroup getSoundGroup();

  /**
   * Gets how long this block will take to break by a player
   *
   * @param tool The tool this item is broken with
   * @return The break time in seconds
   */
  double getBreakTime(ItemStack tool);

  /**
   * Gets if this tool is preferred to mine this block with. Preferred tools usually break the block
   * more quickly. If {@link CustomBlockData#requiresPreferredToolForDrops()} is true, then the
   * block will drop nothing unless mined by a preferred tool.
   *
   * @param tool The tool to check
   * @return True if this item is a preferred tool
   */
  boolean isPreferredTool(ItemStack tool);

  /**
   * If this block must be mined by a preferred tool in order to drop items. The preferred tool is
   * defined by {@link CustomBlockData#isPreferredTool(ItemStack)}.
   *
   * @return If this block requires a preferred tool to drop
   */
  boolean requiresPreferredToolForDrops();

  /**
   * Gets if the block is indestructible. False by default. Indestructible blocks cannot be mined or
   * otherwise normally broken, except by players in creative mode or with commands.
   *
   * @return True if the block is indestructible
   */
  default boolean isIndestructible() {
    return false;
  }
}