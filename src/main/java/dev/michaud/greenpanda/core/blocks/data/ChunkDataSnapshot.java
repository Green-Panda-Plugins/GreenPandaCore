package dev.michaud.greenpanda.core.blocks.data;

import static dev.michaud.greenpanda.core.blocks.data.PersistentBlockData.BLOCK_TYPE_KEY;

import com.google.common.collect.ImmutableMap;
import dev.michaud.greenpanda.core.blocks.CustomBlock;
import dev.michaud.greenpanda.core.blocks.CustomBlockRegistry;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a static, thread-safe snapshot of a chunk and its contained custom blocks and persistent data
 */
public class ChunkDataSnapshot {

  public final int x;
  public final int z;
  public final long versionUID;
  public final ChunkSnapshot chunkSnapshot;
  public final Map<Integer, CustomBlockDataSnapshot<?>> customBlockMap;
  public final long captureTime;

  public ChunkDataSnapshot(@NotNull Chunk chunk) {
    this.captureTime = System.currentTimeMillis();
    this.x = chunk.getX();
    this.z = chunk.getZ();
    this.chunkSnapshot = chunk.getChunkSnapshot();
    this.versionUID = PersistentBlockData.getVersionUID(chunk);
    this.customBlockMap = ImmutableMap.copyOf(buildMap(chunk));

//    if (!customBlockMap.isEmpty()) {
//      GreenPandaCore.getCore().getLogger().info("New (non empty) chunk snapshot: " + this);
//    }
  }

  private static Map<Integer, CustomBlockDataSnapshot<?>> buildMap(Chunk chunk) {

    final PersistentDataContainer chunkDataContainer = chunk.getPersistentDataContainer();

    if (!chunkDataContainer.has(PersistentBlockData.CUSTOM_BLOCKS_KEY, PersistentDataType.TAG_CONTAINER)) {
      return new ConcurrentHashMap<>(0);
    }

    final PersistentDataContainer customBlocksContainer = chunkDataContainer.get(
        PersistentBlockData.CUSTOM_BLOCKS_KEY, PersistentDataType.TAG_CONTAINER);
    assert customBlocksContainer != null;

    final Map<Integer, CustomBlockDataSnapshot<?>> map = new ConcurrentHashMap<>(customBlocksContainer.getKeys().size());

    for (NamespacedKey key : customBlocksContainer.getKeys()) {
      final int packed;
      final CustomBlockDataSnapshot<?> snapshot;

      try {
        packed = Integer.parseInt(key.getKey(), 16);
        snapshot = getBlockSnapshot(key, customBlocksContainer, packed);
      } catch (NumberFormatException | IOException e) {
        continue;
      }

      if (snapshot != null) {
        map.put(packed, snapshot);
      }
    }

    return map;
  }

  private static @Nullable CustomBlockDataSnapshot<? extends CustomBlock> getBlockSnapshot(
      @NotNull NamespacedKey key, @NotNull PersistentDataContainer customBlocksContainer, int location)
      throws IOException {

    PersistentDataContainer container = customBlocksContainer.get(key,
        PersistentDataType.TAG_CONTAINER);

    if (container == null) {
      return null;
    }

    String customType = container.get(BLOCK_TYPE_KEY, PersistentDataType.STRING);
    CustomBlock instance = CustomBlockRegistry.findCustomBlock(customType);

    if (instance != null) {
      return new CustomBlockDataSnapshot<>(instance.getClass(), customType, location, container.serializeToBytes());
    }

    return null;
  }

  /**
   * Gets the stored block data at the given coordinates
   *
   * @param x The relative x coordinate (from 0 to 15)
   * @param y The y coordinate
   * @param z The relative z coordinate (from 0 to 15)
   * @return The custom block snapshot, or null if none exists
   */
  public @Nullable CustomBlockDataSnapshot<?> getDataAt(int x, int y, int z) {
    return getDataAt(ChunkCoordinates.toPackedInt(x, y, z));
  }

  public @Nullable CustomBlock getCustomBlockAt(int x, int y, int z) {

    CustomBlockDataSnapshot<?> data = getDataAt(x, y, z);

    if (data != null) {
      return data.getBlockInstance();
    }

    return null;
  }

  /**
   * Get the stored block data at the given index
   *
   * @param index The index (packed integer representing relative coordinates, see {@link ChunkCoordinates})
   * @return The custom block snapshot, or null if none exists
   */
  public @Nullable CustomBlockDataSnapshot<?> getDataAt(int index) {
    return customBlockMap.get(index);
  }

  /**
   * Get the X coordinate of this chunk
   * @return Chunk's X coordinate
   */
  public int getX() {
    return x;
  }

  /**
   * Get the Z coordinate of this chunk
   * @return Chunk's Z coordinate
   */
  public int getZ() {
    return z;
  }

  /**
   * Gets the version that this chunk's data was stored in
   *
   * @return Gets the version uid of this chunk
   */
  public long getVersionUID() {
    return versionUID;
  }

  /**
   * Get the {@link ChunkSnapshot} object, which has more information about the chunk
   *
   * @return The chunk snapshot
   */
  public ChunkSnapshot getChunkSnapshot() {
    return chunkSnapshot;
  }

  /**
   * Returns the time when this snapshot was captured (in milliseconds since midnight, January 1, 1970 UTC.)
   *
   * @return The capture time of this snapshot
   */
  public long getCaptureTime() {
    return captureTime;
  }

  @Override
  public String toString() {
    return "ChunkDataSnapshot{" +
        "x=" + x +
        ", z=" + z +
        ", versionUID=" + versionUID +
        ", chunkSnapshot=" + chunkSnapshot +
        ", customBlockMap=" + customBlockMap +
        ", captureTime=" + captureTime +
        '}';
  }
}