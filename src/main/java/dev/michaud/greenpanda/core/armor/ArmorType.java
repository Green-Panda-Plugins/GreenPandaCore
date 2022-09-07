package dev.michaud.greenpanda.core.armor;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import javax.annotation.Nullable;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum ArmorType {

  HELMET,
  CHESTPLATE,
  LEGGINGS,
  BOOTS;

  @Contract(pure = true)
  public static ArmorType fromSlot(EquipmentSlot slot) {
    return switch (slot) {
      case HEAD -> HELMET;
      case CHEST -> CHESTPLATE;
      case LEGS -> LEGGINGS;
      case FEET -> BOOTS;
      default -> null;
    };
  }

  @Contract(pure = true)
  public static @NotNull ArmorType fromSlot(PlayerArmorChangeEvent.SlotType slot) {
    return switch (slot) {
      case HEAD -> HELMET;
      case CHEST -> CHESTPLATE;
      case LEGS -> LEGGINGS;
      case FEET -> BOOTS;
    };
  }

  @Contract(pure = true)
  public static ArmorType fromSlot(int slot) {
    return switch (slot) {
      case 5 -> HELMET;
      case 6 -> CHESTPLATE;
      case 7 -> LEGGINGS;
      case 8 -> BOOTS;
      default -> null;
    };
  }

  @Contract(value = "null -> null", pure = true)
  public static @Nullable ArmorType fromMaterial(@Nullable Material material) {

    if (material == null) {
      return null;
    }

    switch (material) {
      case NETHERITE_HELMET, DIAMOND_HELMET, GOLDEN_HELMET, IRON_HELMET, CHAINMAIL_HELMET, LEATHER_HELMET, CARVED_PUMPKIN, PLAYER_HEAD, SKELETON_SKULL, ZOMBIE_HEAD, CREEPER_HEAD, WITHER_SKELETON_SKULL, TURTLE_HELMET, DRAGON_HEAD -> {
        return HELMET;
      }
      case NETHERITE_CHESTPLATE, DIAMOND_CHESTPLATE, GOLDEN_CHESTPLATE, IRON_CHESTPLATE, CHAINMAIL_CHESTPLATE, LEATHER_CHESTPLATE, ELYTRA -> {
        return CHESTPLATE;
      }
      case NETHERITE_LEGGINGS, DIAMOND_LEGGINGS, GOLDEN_LEGGINGS, IRON_LEGGINGS, CHAINMAIL_LEGGINGS, LEATHER_LEGGINGS -> {
        return LEGGINGS;
      }
      case NETHERITE_BOOTS, DIAMOND_BOOTS, GOLDEN_BOOTS, IRON_BOOTS, CHAINMAIL_BOOTS, LEATHER_BOOTS -> {
        return BOOTS;
      }
      default -> {
        return null;
      }

    }

  }

}