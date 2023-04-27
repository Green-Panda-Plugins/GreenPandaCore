package dev.michaud.greenpanda.core.item;

import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a custom item.
 */
public interface CustomItem {

  /**
   * Gets the plugin that this item class is registered in. This also acts as the namespace for this
   * item when setting the custom item id.
   *
   * @return The plugin that this item class is registered in.
   */
  @NotNull JavaPlugin getOwnerPlugin();

  /**
   * Gets the unique identifier for this item. This is used to keep track of the item, so make sure
   * it's not a duplicate.
   *
   * @return The unique identifier for this item.
   */
  @NotNull String customId();

  /**
   * Gets the custom model data for the item. 0 if no custom model data is required.
   *
   * @return The custom model data of the item.
   */
  int customModelData();

  /**
   * Gets the display name for this item that will be shown in the inventory. If null, the display
   * name will be the default item name.
   *
   * @return The display name for this item. Null if the default display name should be used.
   */
  Component displayName();

  /**
   * Gets the base {@link Material} for this item. This is the underlying material the item is made
   * from.
   *
   * @return The base material for this item.
   */
  @NotNull Material baseMaterial();

  /**
   * Creates a new {@link ItemStack} for this item. By default, this automatically sets the display
   * name, model data, and custom id.
   *
   * @return A new item stack for this item.
   */
  default @NotNull ItemStack makeItem() {

    ItemStack item = new ItemStack(baseMaterial(), 1);
    ItemMeta meta = item.getItemMeta();

    NamespacedKey key = new NamespacedKey(getOwnerPlugin(), "custom_item_id");

    meta.displayName(displayName());
    meta.setCustomModelData(customModelData());
    meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, customId());

    item.setItemMeta(meta);
    return item;

  }

  /**
   * <p>Checks if the given {@link ItemStack} is of the same type as this {@link CustomItem}.
   * <p>An item is defined as the same type if {@link ItemStack#getType()} is equal
   * to {@link CustomItem#baseMaterial()}, and the {@link ItemStack} has NBT data matching
   * {@link CustomItem#customId()} in the namespace of {@link CustomItem#getOwnerPlugin()}.</p>
   *
   * @param item The item to check.
   * @return True if the item is of the same type as this item.
   */
  default boolean isType(ItemStack item) {

    if (item == null || item.getType() != baseMaterial()) {
      return false;
    }

    PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
    NamespacedKey key = new NamespacedKey(getOwnerPlugin(), "custom_item_id");

    if (!dataContainer.has(key, PersistentDataType.STRING)) {
      return false;
    }

    String itemId = dataContainer.get(key, PersistentDataType.STRING);

    return Objects.equals(itemId, customId());
  }

}