package dev.michaud.greenpanda.core.armor;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import dev.michaud.greenpanda.core.GreenPandaCore;
import dev.michaud.greenpanda.core.nbt.PersistentDataTypeArmorType;
import javax.annotation.Nullable;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a type of armor
 */
public enum ArmorType {

  HELMET,
  CHESTPLATE,
  LEGGINGS,
  BOOTS;

  public static final NamespacedKey ARMOR_TYPE_OVERRIDE_KEY = new NamespacedKey(
      GreenPandaCore.getCore(), "armor_type_override");

  /**
   * Gets the armor type for a given {@link EquipmentSlot}.
   *
   * @param slot The equipment slot
   * @return The equivalent armor type, or null if slot equals {@link EquipmentSlot#HAND} or
   * {@link EquipmentSlot#OFF_HAND}
   */
  @Contract(pure = true)
  public static ArmorType fromSlot(@NotNull EquipmentSlot slot) {
    return switch (slot) {
      case HEAD -> HELMET;
      case CHEST -> CHESTPLATE;
      case LEGS -> LEGGINGS;
      case FEET -> BOOTS;
      default -> null;
    };
  }

  /**
   * Converts slot from {@link PlayerArmorChangeEvent.SlotType} to ArmorType.
   *
   * @param slot The slot to get
   * @return The equivalent armor type
   */
  @Contract(pure = true)
  public static @NotNull ArmorType fromSlot(@NotNull PlayerArmorChangeEvent.SlotType slot) {
    return switch (slot) {
      case HEAD -> HELMET;
      case CHEST -> CHESTPLATE;
      case LEGS -> LEGGINGS;
      case FEET -> BOOTS;
    };
  }

  /**
   * Gets the armor type from the slot, or null if the slot isn't an armor slot. Uses the spigot
   * ids, equivalent to:
   * <p>
   * 39 = HELMET, 38 = CHESTPLATE, 37 = LEGGINGS, 36 = BOOTS.
   * </p>
   *
   * @param slot The slot id
   * @return The armor type of the given slot, or null if invalid
   */
  @Contract(pure = true)
  public static ArmorType fromSlot(int slot) {
    return switch (slot) {
      case 39 -> HELMET;
      case 38 -> CHESTPLATE;
      case 37 -> LEGGINGS;
      case 36 -> BOOTS;
      default -> null;
    };
  }

  /**
   * Gets the armor type of the given material. If the material is null, empty, or not armor, then
   * returns null.
   *
   * @param material The material to check
   * @return The armor type of the material, or null if not armor
   */
  @Contract(value = "null -> null", pure = true)
  public static @Nullable ArmorType fromMaterial(@Nullable Material material) {

    if (material == null || material.isEmpty()) {
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

  /**
   * Gets the relevant armor type from the given item. If the value has been set by
   * {@link ArmorType#setArmorTypeOverride(ItemStack, ArmorType)}, then it returns the stored value.
   * Otherwise, the returned value is equivalent to {@link ArmorType#fromMaterial(Material)} on the
   * item's material.
   *
   * @param item The item to get the armor type for
   * @return The armor type, or null if not armor
   */
  @Contract("null -> null")
  public static ArmorType fromItemStack(ItemStack item) {

    if (item == null || item.getType().isEmpty()) {
      return null;
    }

    ItemMeta meta = item.getItemMeta();
    PersistentDataContainer container = meta.getPersistentDataContainer();

    if (container.has(ARMOR_TYPE_OVERRIDE_KEY)) {
      return container.get(ARMOR_TYPE_OVERRIDE_KEY, new PersistentDataTypeArmorType());
    }

    return fromMaterial(item.getType());

  }

  /**
   * Sets the armor type of this item by storing it in NBT data. If type is null, the nbt data will
   * be removed.
   * <p>
   * All future calls to {@link ArmorType#fromItemStack(ItemStack)} will return the given armor
   * type. However, calls to {@link ArmorType#fromMaterial(Material)} will still return the default
   * value. The main use case of this method is in cases where it is unclear what armor type an item
   * should be, and there is no other way to determine from its armor slot.
   *
   * @param item The item to set the nbt data on
   * @param type The armor type to set
   * @return The item stack with the nbt data
   */
  @Contract("null, _ -> null")
  public static ItemStack setArmorTypeOverride(ItemStack item, ArmorType type) {

    if (item == null || item.getType().isEmpty()) {
      return null;
    }

    ItemMeta meta = item.getItemMeta();
    PersistentDataContainer container = meta.getPersistentDataContainer();

    if (type == null) {
      container.remove(ARMOR_TYPE_OVERRIDE_KEY);
    } else {
      container.set(ARMOR_TYPE_OVERRIDE_KEY, new PersistentDataTypeArmorType(), type);
    }

    item.setItemMeta(meta);

    return item;

  }
}