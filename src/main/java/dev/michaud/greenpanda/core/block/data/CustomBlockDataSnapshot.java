package dev.michaud.greenpanda.core.block.data;

import dev.michaud.greenpanda.core.block.CustomBlock;
import dev.michaud.greenpanda.core.block.CustomBlockRegistry;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomBlockDataSnapshot<T extends CustomBlock> {

  final int location;
  final @NotNull Class<T> type;
  final @NotNull String typeIdentifier;
  final byte[] bytes;

  CustomBlockDataSnapshot(@NotNull Class<T> clazz, @NotNull String typeIdentifier, int location, byte[] bytes) {
    this.type = clazz;
    this.typeIdentifier = typeIdentifier;
    this.location = location;
    this.bytes = bytes;

//    GreenPandaCore.getCore().getLogger().info(String.format(
//        "New custom block snapshot! At %d, %d, %d", getX(), getY(), getZ()));
  }

  public @NotNull Class<T> getType() {
    return type;
  }

  public @NotNull String getTypeIdentifier() {
    return typeIdentifier;
  }

  public int getRawLocation() {
    return location;
  }

  public int[] getLocation() {
    return ChunkCoordinates.fromPackedInt(getRawLocation());
  }

  public int getX() {
    return getLocation()[0];
  }

  public int getY() {
    return getLocation()[1];
  }

  public int getZ() {
    return getLocation()[2];
  }

  public @Nullable CustomBlock getBlockInstance() {
    return CustomBlockRegistry.findFromSnapshot(this);
  }

  public byte[] getPersistentDataBytes() {
    return bytes;
  }

  @Override
  public String toString() {
    return "CustomBlockDataSnapshot{" +
        "type=" + type +
        ", typeIdentifier='" + typeIdentifier + '\'' +
        ", bytes=" + Arrays.toString(bytes) +
        '}';
  }
}