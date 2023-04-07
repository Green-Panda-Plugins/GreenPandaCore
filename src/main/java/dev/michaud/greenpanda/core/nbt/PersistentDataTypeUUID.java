package dev.michaud.greenpanda.core.nbt;

import java.nio.ByteBuffer;
import java.util.UUID;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class PersistentDataTypeUUID implements PersistentDataType<byte[], UUID> {

  @Override
  public @NotNull Class<byte[]> getPrimitiveType() {
    return byte[].class;
  }

  @Override
  public @NotNull Class<UUID> getComplexType() {
    return UUID.class;
  }

  @Override
  public byte @NotNull [] toPrimitive(@NotNull UUID complex,
      @NotNull PersistentDataAdapterContext context) {
    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
    bb.putLong(complex.getMostSignificantBits());
    bb.putLong(complex.getLeastSignificantBits());
    return bb.array();
  }

  @Override
  public @NotNull UUID fromPrimitive(byte @NotNull [] primitive,
      @NotNull PersistentDataAdapterContext context) {
    ByteBuffer bb = ByteBuffer.wrap(primitive);
    long mostSigBits = bb.getLong();
    long leastSigBits = bb.getLong();
    return new UUID(mostSigBits, leastSigBits);
  }

}