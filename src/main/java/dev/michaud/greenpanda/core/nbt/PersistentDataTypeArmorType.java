package dev.michaud.greenpanda.core.nbt;

import dev.michaud.greenpanda.core.armor.ArmorType;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class PersistentDataTypeArmorType implements PersistentDataType<String, ArmorType> {

  @Override
  public @NotNull Class<String> getPrimitiveType() {
    return String.class;
  }

  @Override
  public @NotNull Class<ArmorType> getComplexType() {
    return ArmorType.class;
  }

  @Override
  public @NotNull String toPrimitive(@NotNull ArmorType complex,
      @NotNull PersistentDataAdapterContext context) {
    return complex.name();
  }

  @Override
  public @NotNull ArmorType fromPrimitive(@NotNull String primitive,
      @NotNull PersistentDataAdapterContext context) {
    return ArmorType.valueOf(primitive);
  }
}