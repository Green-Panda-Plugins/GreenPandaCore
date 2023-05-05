package dev.michaud.greenpanda.core.block.data;

import dev.michaud.greenpanda.core.GreenPandaCore;
import dev.michaud.greenpanda.core.block.CustomBlockData;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Create, store, and manage custom blocks. Also allows you to store persistent data for any block.
 */
public class PersistentBlockData {

  public static final long CURRENT_VERSION_UID = 1;

  public static final NamespacedKey DATA_VERSION_KEY = new NamespacedKey(GreenPandaCore.getCore(),
      "version_uid");
  public static final NamespacedKey CUSTOM_BLOCKS_KEY = new NamespacedKey(GreenPandaCore.getCore(),
      "custom_blocks");
  public static final NamespacedKey BLOCK_TYPE_KEY = new NamespacedKey(GreenPandaCore.getCore(),
      "custom_block_type");

  /**
   * Add a custom block to the world
   *
   * @param block           The block that you want to make a custom block
   * @param customBlockData The custom block type to set
   */
  public static void addCustomBlock(@NotNull Block block, CustomBlockData customBlockData) {
    final int x = ChunkCoordinates.trueMod(block.getX(), 16);
    final int y = block.getY();
    final int z = ChunkCoordinates.trueMod(block.getZ(), 16);
    addCustomBlock(block.getChunk(), x, y, z, customBlockData);
  }

  /**
   * Add a custom block to the world
   *
   * @param chunk           The chunk the block is set in
   * @param x               The relative x coordinate of the block (from 0 to 15)
   * @param y               The y coordinate of the block
   * @param z               The relative z coordinate of the block (from 0 to 15)
   * @param customBlockData The custom block type to set
   */
  public static void addCustomBlock(@NotNull Chunk chunk, int x, int y, int z,
      @NotNull CustomBlockData customBlockData) {
    setPersistentData(chunk, x, y, z, BLOCK_TYPE_KEY, PersistentDataType.STRING,
        customBlockData.blockId());
  }

  /**
   * Removes all custom data from a block
   *
   * @param block The block data should be removed for
   */
  public static void removeCustomBlock(@NotNull Block block) {
    final Chunk chunk = block.getChunk();
    final int x = ChunkCoordinates.trueMod(block.getX(), 16);
    final int y = block.getY();
    final int z = ChunkCoordinates.trueMod(block.getZ(), 16);
    removePersistentData(chunk, x, y, z);
  }

  /**
   * Get the type of custom block at the given coordinates
   *
   * @param chunk The chunk to search
   * @param x     The relative x coordinate (from 0 to 15)
   * @param y     The y coordinate
   * @param z     The relative z coordinate (from 0 to 15)
   * @return The custom block id, or null if not a custom block
   */
  public static @Nullable String getCustomBlockType(@NotNull Chunk chunk, int x, int y, int z) {
    return getPersistentData(chunk, x, y, z, BLOCK_TYPE_KEY, PersistentDataType.STRING);
  }

  /**
   * Stores persistent data for this block in the Chunk's {@link PersistentDataContainer}. Can store
   * any data a PersistentDataContainer can.
   * <p>
   * NBT data is stored using a nested container, along with a VersionUID for backwards
   * compatibility. The layout of data can be visualized as such:
   * <pre>
   *  ChunkDataContainer: {
   *   version_uid: 1,
   *   custom_blocks: {
   *      7eaa: { custom_block_type: example, color: 2 },
   *      7eba: { custom_block_type: example_2, direction: NORTH }
   *   }
   * }
   * </pre>
   *
   * @param chunk The chunk to store the data in
   * @param x     The relative x coordinate of the block (from 0 to 15)
   * @param y     The y coordinate of the block (from world minHeight to world maxHeight)
   * @param z     The relative z coordinate of the block (from 0 to 15)
   * @param key   The key the value will be stored under
   * @param type  The type this tag uses
   * @param value The value to set
   * @param <T>   The generic java type of the tag value
   * @param <Z>   The generic type of the object to store
   */
  public static <T, Z> void setPersistentData(@NotNull Chunk chunk, int x, int y, int z,
      @NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type, @NotNull Z value) {

    final PersistentDataContainer chunkData = chunk.getPersistentDataContainer();
    final PersistentDataContainer blocksData = chunkData.getOrDefault(
        CUSTOM_BLOCKS_KEY, PersistentDataType.TAG_CONTAINER,
        chunkData.getAdapterContext().newPersistentDataContainer());

    //Version
    long version = getVersionUID(chunk, CURRENT_VERSION_UID);

    if (version > CURRENT_VERSION_UID) {
      throw new RuntimeException(String.format(
          "PersistentBlockData VersionUID for chunk at XZ: %d, %d is invalid: Version is '%s', I'm still on version %d! Are you using an old version of GreenPandaCore?",
          chunk.getX(), chunk.getZ(), version, CURRENT_VERSION_UID));
    }

    //Data
    final NamespacedKey blockKey = coordsToBlockKey(x, y, z);

    PersistentDataContainer blockContainer = blocksData.getOrDefault(blockKey,
        PersistentDataType.TAG_CONTAINER,
        blocksData.getAdapterContext().newPersistentDataContainer());

    blockContainer.set(key, type, value);
    blocksData.set(blockKey, PersistentDataType.TAG_CONTAINER, blockContainer);
    chunkData.set(CUSTOM_BLOCKS_KEY, PersistentDataType.TAG_CONTAINER, blocksData);
  }

  /**
   * Deletes all persistent data of a block
   *
   * @param chunk The chunk to remove the data from
   * @param x     The relative x coordinate of the block (from 0 to 15)
   * @param y     The y coordinate of the block (from world minHeight to world maxHeight)
   * @param z     The relative z coordinate of the block (from 0 to 15)
   */
  public static void removePersistentData(@NotNull Chunk chunk, int x, int y, int z) {
    final PersistentDataContainer chunkDataContainer = chunk.getPersistentDataContainer();

    if (!chunkDataContainer.has(CUSTOM_BLOCKS_KEY, PersistentDataType.TAG_CONTAINER)) {
      return;
    }

    final PersistentDataContainer customBlocksContainer = chunkDataContainer.get(CUSTOM_BLOCKS_KEY,
        PersistentDataType.TAG_CONTAINER);
    final NamespacedKey blockKey = coordsToBlockKey(x, y, z);

    if (customBlocksContainer == null || !customBlocksContainer.has(blockKey)) {
      return;
    }

    customBlocksContainer.remove(blockKey);
    chunkDataContainer.set(CUSTOM_BLOCKS_KEY, PersistentDataType.TAG_CONTAINER,
        customBlocksContainer);
  }

  /**
   * Gets persistent data for this block from the Chunk's {@link PersistentDataContainer}.
   *
   * @param chunk The chunk to search
   * @param x     The relative x coordinate of the block (from 0-15)
   * @param y     The y coordinate of the block
   * @param z     The relative z coordinate of the block (from 0-15)
   * @param key   The key to look up
   * @param type  The type the value must have and will be cast to
   * @param <T>   The generic type of the stored primitive
   * @param <Z>   The generic type of the created complex object
   * @return The obtained value, or null if no value was stored under the given key
   * @see PersistentBlockData#setPersistentData(Chunk, int, int, int, NamespacedKey,
   * PersistentDataType, Object)
   */
  @Nullable
  public static <T, Z> Z getPersistentData(@NotNull Chunk chunk, int x, int y, int z,
      @NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type) {

    final PersistentDataContainer chunkData = chunk.getPersistentDataContainer();

    if (!chunkData.has(CUSTOM_BLOCKS_KEY, PersistentDataType.TAG_CONTAINER)) {
      return null;
    }

    final PersistentDataContainer blocksData = chunkData.get(CUSTOM_BLOCKS_KEY,
        PersistentDataType.TAG_CONTAINER);
    final NamespacedKey blockKey = coordsToBlockKey(x, y, z);

    if (blocksData == null || !blocksData.has(blockKey, PersistentDataType.TAG_CONTAINER)) {
      return null;
    }

    final PersistentDataContainer blockContainer = blocksData.get(blockKey,
        PersistentDataType.TAG_CONTAINER);

    if (blockContainer == null || !blockContainer.has(key, type)) {
      return null;
    }

    return blockContainer.get(key, type);
  }

  /**
   * Gets a chunk's stored VersionUID. The version number is incremented with every major change to
   * the block data system, allowing for backwards compatibility. If no version number is found,
   * then {@link PersistentBlockData#CURRENT_VERSION_UID} is returned and stored on the chunk.
   *
   * @param chunk The chunk to get version number of
   * @return
   */
  public static long getVersionUID(@NotNull Chunk chunk) {
    return getVersionUID(chunk, CURRENT_VERSION_UID);
  }

  /**
   * Gets the VersionUID of the given chunk. The version number is incremented with every major
   * change to the block data system, allowing for backwards compatibility. If no version number is
   * found, then the default value is returned and stored on the chunk.
   *
   * @param chunk        The chunk to get version number of
   * @param defaultValue The default value, if this chunk has no version number (usually
   *                     {@link PersistentBlockData#CURRENT_VERSION_UID})
   * @return The version number of the chunk, or the default value if none is found
   */
  public static long getVersionUID(@NotNull Chunk chunk, long defaultValue) {

    final PersistentDataContainer chunkDataContainer = chunk.getPersistentDataContainer();

    Long version;
    if (chunkDataContainer.has(DATA_VERSION_KEY, PersistentDataType.LONG)) {
      version = chunkDataContainer.get(DATA_VERSION_KEY, PersistentDataType.LONG);
      assert version != null;
    } else {
      version = defaultValue;
      chunkDataContainer.set(DATA_VERSION_KEY, PersistentDataType.LONG, version);
    }

    return version;
  }

  public static @NotNull NamespacedKey coordsToBlockKey(int x, int y, int z) {
    final int packedCoords = ChunkCoordinates.toPackedInt(x, y, z);
    final String hex = Integer.toHexString(packedCoords);
    return new NamespacedKey(GreenPandaCore.getCore(), hex);
  }

}