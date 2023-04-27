package dev.michaud.greenpanda.core.blocks.data;

import dev.michaud.greenpanda.core.GreenPandaCore;
import java.util.Arrays;
import org.bukkit.Chunk;
import org.bukkit.Location;
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

  public static final int CURRENT_DATA_VERSION = 1;

  public static final NamespacedKey CUSTOM_BLOCKS_KEY = new NamespacedKey(GreenPandaCore.getCore(),
      "custom_blocks");
  public static final NamespacedKey BLOCK_PALETTE_KEY = new NamespacedKey(GreenPandaCore.getCore(),
      "block_palette");
  public static final NamespacedKey BLOCK_TYPE_KEY = new NamespacedKey(GreenPandaCore.getCore(),
      "custom_block_type");

  public static void addCustomBlock(@NotNull String customId, @NotNull Block block) {

    final Location location = block.getLocation();

    setPersistentData(block, BLOCK_TYPE_KEY, PersistentDataType.STRING, customId);

    //Palette
    final Chunk chunk = location.getChunk();
    final PersistentDataContainer chunkDataContainer = chunk.getPersistentDataContainer();
    final PersistentDataContainer customBlocksContainer = chunkDataContainer.getOrDefault(
        CUSTOM_BLOCKS_KEY, PersistentDataType.TAG_CONTAINER,
        chunkDataContainer.getAdapterContext().newPersistentDataContainer());
    final PersistentDataContainer blockPaletteContainer = customBlocksContainer.getOrDefault
        (BLOCK_PALETTE_KEY, PersistentDataType.TAG_CONTAINER,
            customBlocksContainer.getAdapterContext().newPersistentDataContainer());

    final NamespacedKey blockTypeKey = new NamespacedKey(GreenPandaCore.getCore(), customId);

    int[] arr = blockPaletteContainer.getOrDefault(blockTypeKey, PersistentDataType.INTEGER_ARRAY, new int[0]);
    arr = Arrays.copyOf(arr, arr.length + 1);

    //Get packed coords
    int blockX = location.getBlockX() % 16;
    int blockY = location.getBlockY();
    int blockZ = location.getBlockZ() % 16;
    blockX = blockX < 0 ? blockX + 16 : blockX;
    blockZ = blockZ < 0 ? blockZ + 16 : blockZ;

    final int packedCoords = ChunkCoordinates.toPackedInt(blockX, blockY, blockZ);

    arr[arr.length - 1] = packedCoords;

    blockPaletteContainer.set(blockTypeKey, PersistentDataType.INTEGER_ARRAY, arr);
    customBlocksContainer.set(BLOCK_PALETTE_KEY, PersistentDataType.TAG_CONTAINER, blockPaletteContainer);
    chunkDataContainer.set(CUSTOM_BLOCKS_KEY, PersistentDataType.TAG_CONTAINER, customBlocksContainer);
  }

  /**
   * Same as:
   * {@link PersistentBlockData#setPersistentData(Location, NamespacedKey,
   * PersistentDataType, Object)}
   */
  public static <T, Z> void setPersistentData(@NotNull Block block, @NotNull NamespacedKey key,
      @NotNull PersistentDataType<T, Z> type, @NotNull Z value) {
    setPersistentData(block.getLocation(), key, type, value);
  }

  /**
   * Same as:
   * {@link PersistentBlockData#getPersistentData(Location, NamespacedKey,
   * PersistentDataType)}
   */
  public static <T, Z> Z getPersistentData(@NotNull Block block, @NotNull NamespacedKey key,
      @NotNull PersistentDataType<T, Z> type) {
    return getPersistentData(block.getLocation(), key, type);
  }

  /**
   * Stores persistent data for this block in the Chunk's {@link PersistentDataContainer}.
   *
   * @param location The location of the block to store data for
   * @param key      The key the value will be stored under
   * @param type     The type this tag uses
   * @param value    The value to set
   * @param <T>      The generic java type of the tag value
   * @param <Z>      The generic type of the object to store
   */
  public static <T, Z> void setPersistentData(@NotNull Location location,
      @NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type, @NotNull Z value) {

    final Chunk chunk = location.getChunk();
    final PersistentDataContainer chunkDataContainer = chunk.getPersistentDataContainer();
    final PersistentDataContainer customBlocksContainer = chunkDataContainer.getOrDefault(
        CUSTOM_BLOCKS_KEY, PersistentDataType.TAG_CONTAINER,
        chunkDataContainer.getAdapterContext().newPersistentDataContainer());

    //Version
    final NamespacedKey versionKey = new NamespacedKey(GreenPandaCore.getCore(), "version_uid");
    int version = customBlocksContainer.getOrDefault(versionKey, PersistentDataType.INTEGER,
        CURRENT_DATA_VERSION);

    if (version > CURRENT_DATA_VERSION) {
      throw new RuntimeException(String.format(
          "PersistentBlockData VersionUID for chunk at XZ: %d, %d is invalid: Version is '%s', I'm still on version %d! Are you using an old version of GreenPandaCore?",
          chunk.getX(), chunk.getZ(), version, CURRENT_DATA_VERSION));
    }

    customBlocksContainer.set(versionKey, PersistentDataType.INTEGER, version);

    //Data
    int blockX = location.getBlockX() % 16;
    int blockY = location.getBlockY();
    int blockZ = location.getBlockZ() % 16;

    //Java's modulo sucks big ass, so we have to account for negatives
    blockX = blockX < 0 ? blockX + 16 : blockX;
    blockZ = blockZ < 0 ? blockZ + 16 : blockZ;

    final int packedCoords = ChunkCoordinates.toPackedInt(blockX, blockY, blockZ);
    final String hex = Integer.toHexString(packedCoords);
    final NamespacedKey blockKey = new NamespacedKey(GreenPandaCore.getCore(), hex);

    GreenPandaCore.getCore().getLogger().info(
        String.format("Block chunk location is %d / %d / %d. Packed integer is %d. Hex value is %s",
            blockX, blockY, blockZ, packedCoords, hex));

    PersistentDataContainer blockContainer = customBlocksContainer.getOrDefault(blockKey,
        PersistentDataType.TAG_CONTAINER,
        customBlocksContainer.getAdapterContext().newPersistentDataContainer());

    blockContainer.set(key, type, value);
    customBlocksContainer.set(blockKey, PersistentDataType.TAG_CONTAINER, blockContainer);
    chunkDataContainer.set(CUSTOM_BLOCKS_KEY, PersistentDataType.TAG_CONTAINER,
        customBlocksContainer);
  }

  /**
   * Gets persistent data for this block from the Chunk's {@link PersistentDataContainer}.
   *
   * @param location The location of the block to get data from
   * @param key      The key to look up
   * @param type     The type the value must have and will be cast to
   * @param <T>      The generic type of the stored primitive
   * @param <Z>      The generic type of the created complex object
   * @return The obtained value, or null if no value was stored under the given key
   */
  @Nullable
  public static <T, Z> Z getPersistentData(@NotNull Location location, @NotNull NamespacedKey key,
      @NotNull PersistentDataType<T, Z> type) {

    final Chunk chunk = location.getChunk();
    final PersistentDataContainer chunkDataContainer = chunk.getPersistentDataContainer();

    if (!chunkDataContainer.has(CUSTOM_BLOCKS_KEY, PersistentDataType.TAG_CONTAINER)) {
      GreenPandaCore.getCore().getLogger().info("Chunk has no custom block data!");
      return null;
    }

    final PersistentDataContainer customBlocksContainer = chunkDataContainer.get(CUSTOM_BLOCKS_KEY,
        PersistentDataType.TAG_CONTAINER);

    final int packedCoords = ChunkCoordinates.toPackedInt(location);

    final String hex = Integer.toHexString(packedCoords);
    final NamespacedKey blockKey = new NamespacedKey(GreenPandaCore.getCore(), hex);

    if (customBlocksContainer == null || !customBlocksContainer.has(blockKey)) {
      GreenPandaCore.getCore().getLogger()
          .info("Custom block container is null or doesn't have specified block");
      return null;
    }

    final PersistentDataContainer blockContainer = customBlocksContainer.get(blockKey,
        PersistentDataType.TAG_CONTAINER);

    if (blockContainer == null || !blockContainer.has(key, type)) {
      GreenPandaCore.getCore().getLogger()
          .info("Block's data container is null or doesn't have specified key");
      return null;
    }

    return blockContainer.get(key, type);
  }

}