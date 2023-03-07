package dev.michaud.greenpanda.core.nbt;

import dev.michaud.greenpanda.core.math.Vector2;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class PersistentDataTypeVector2 implements PersistentDataType<Float[], Vector2> {

  @Override
  public @NotNull Class<Float[]> getPrimitiveType() {
    return Float[].class;
  }

  @Override
  public @NotNull Class<Vector2> getComplexType() {
    return Vector2.class;
  }

  @Override
  public Float @NotNull [] toPrimitive(@NotNull Vector2 complex, @NotNull PersistentDataAdapterContext context) {
    return new Float[] {complex.x, complex.y};
  }

  @Override
  public @NotNull Vector2 fromPrimitive(Float @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
    return new Vector2(primitive[0], primitive[1]);
  }

}