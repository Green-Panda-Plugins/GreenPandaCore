package dev.michaud.greenpanda.core.nbt;

import dev.michaud.greenpanda.core.GreenPandaCore;
import java.nio.ByteBuffer;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class PersistentDataTypeLocation implements PersistentDataType<byte[], Location> {

  @Override
  public @NotNull Class<byte[]> getPrimitiveType() {
    return byte[].class;
  }

  @Override
  public @NotNull Class<Location> getComplexType() {
    return Location.class;
  }

  @Override
  public byte @NotNull [] toPrimitive(@NotNull Location complex,
      @NotNull PersistentDataAdapterContext context) {

    ByteBuffer bb = ByteBuffer.allocate(48);
    bb.putDouble(complex.getX());
    bb.putDouble(complex.getY());
    bb.putDouble(complex.getZ());
    bb.putFloat(complex.getYaw());
    bb.putFloat(complex.getPitch());
    bb.putLong(complex.getWorld().getUID().getMostSignificantBits());
    bb.putLong(complex.getWorld().getUID().getLeastSignificantBits());
    return bb.array();

  }

  @Override
  public @NotNull Location fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {

    ByteBuffer bb = ByteBuffer.wrap(primitive);
    double x = bb.getDouble();
    double y = bb.getDouble();
    double z = bb.getDouble();
    float yaw = bb.getFloat();
    float pitch = bb.getFloat();
    long msb = bb.getLong();
    long lsb = bb.getLong();

    UUID worldUUID = new UUID(msb, lsb);
    World world = GreenPandaCore.getCore().getServer().getWorld(worldUUID);

    return new Location(world, x, y, z, yaw, pitch);

  }

}