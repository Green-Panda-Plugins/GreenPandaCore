package dev.michaud.greenpanda.core.blocks;

import dev.michaud.greenpanda.core.GreenPandaCore;
import dev.michaud.greenpanda.core.blocks.data.PersistentBlockData;
import dev.michaud.greenpanda.core.item.CustomItem;
import dev.michaud.greenpanda.core.nbt.PersistentDataTypeBoolean;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a custom block
 */
public abstract class CustomBlock implements CustomBlockData, CustomItem {

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

    try {
      PersistentBlockData.addCustomBlock(currentBlock, this);
    } catch (Exception e) {
      GreenPandaCore.getCore().getLogger().severe("An error occurred while trying to place custom block: " + e);
      return false;
    }

    currentBlock.setType(Material.BARRIER);

    return true;
  }

  public @NotNull Instrument getInstrument() {
    return getNoteblockState().getInstrument();
  }

  public @NotNull Note getNote() {
    return getNoteblockState().getNote();
  }

  public int getNoteId() {
    return getNoteblockState().getNoteId();
  }

  public boolean getPowered() {
    return getNoteblockState().isPowered();
  }

  /**
   * Gets the unique identifier for this block's item. The same as
   * {@link CustomBlockData#blockId() blockId()} by default.
   *
   * @return The unique identifier for the item
   */
  @Override
  public @NotNull String customId() {
    return blockId();
  }

  /**
   * Gets the base material of this block's item, {@link Material#NOTE_BLOCK NOTE_BLOCK} by default. Doesn't
   * affect the placed block, just the item in the inventory. Must be a block
   * ({@link Material#isBlock()} returns true), otherwise the block will be unable to be placed.
   *
   * @return The base material of the item
   */
  @Override
  public @NotNull Material baseMaterial() {
    return Material.NOTE_BLOCK;
  }

  /**
   * Creates a new {@link ItemStack} for this block's held item.
   *
   * @return A new item stack for this block
   */
  @Override
  public @NotNull ItemStack makeItem() {

    ItemStack item = CustomItem.super.makeItem();
    ItemMeta meta = item.getItemMeta();
    PersistentDataContainer container = meta.getPersistentDataContainer();

    NamespacedKey key = new NamespacedKey(getOwnerPlugin(), "custom_block");

    container.set(key, new PersistentDataTypeBoolean(), true);
    item.setItemMeta(meta);

    return item;
  }

  /**
   * Returns true if this item represents a custom block
   *
   * @param item The item to check
   * @return True if this item represents a custom block
   */
  boolean isBlockItem(ItemStack item) {
    if (item == null || item.getType() != baseMaterial()) {
      return false;
    }

    PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
    NamespacedKey key = new NamespacedKey(getOwnerPlugin(), "custom_block");

    return dataContainer.getOrDefault(key, new PersistentDataTypeBoolean(), false);
  }

  /**
   * Gets the custom model data for this block's item. 0 is no custom model data. By default, set to
   * be the ordinal number of {@link CustomBlockData#getNoteblockState() getNoteblockState()}
   *
   * @return The custom model data of the item
   */
  @Override
  public int customModelData() {
    return getNoteblockState().ordinal();
  }

}