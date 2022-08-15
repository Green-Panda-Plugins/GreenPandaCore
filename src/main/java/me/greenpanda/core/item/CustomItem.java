package me.greenpanda.core.item;

import java.util.Objects;
import me.greenpanda.core.GreenPandaCore;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public interface CustomItem {

  /**
   * Get the custom model data for the item.
   *
   * @return The custom model data of the item.
   */
  int customModelData();

  /**
   * Get the unique identifier for this item. This is used to keep track of the item, so make sure
   * it's not a duplicate.
   *
   * @return The unique identifier for this item.
   */
  @NotNull String customId();

  /**
   * Get the display name for this item. This is the name that will be displayed in the inventory.
   *
   * @return The display name for this item.
   */
  @NotNull Component displayName();

  /**
   * Get the base material for this item. This is the material that will be used to create the
   * item.
   *
   * @return The base material for this item.
   */
  @NotNull Material baseMaterial();

  /**
   * Creates a new item stack for this item. By default, this automatically sets the display name,
   * model data, and custom id.
   *
   * @return A new item stack for this item.
   */
  default @NotNull ItemStack makeItem() {

    final ItemStack item = new ItemStack(baseMaterial(), 1);
    final ItemMeta meta = item.getItemMeta();

    final NamespacedKey key = new NamespacedKey(GreenPandaCore.getCore(), "custom_item_id");

    meta.displayName(displayName());
    meta.setCustomModelData(customModelData());
    meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, customId());

    item.setItemMeta(meta);
    return item;

  }

  /**
   * Checks if the given item is of the same type as this item. By default, checks if the item is of
   * the same type and has the same custom id.
   *
   * @param item The item to check.
   * @return True if the item is of the same type as this item.
   */
  default boolean isType(ItemStack item) {

    if (item == null || item.getType() != baseMaterial()) {
      return false;
    }

    PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
    NamespacedKey key = new NamespacedKey(GreenPandaCore.getCore(), "custom_item_id");

    if (!dataContainer.has(key, PersistentDataType.STRING)) {
      return false;
    }

    String itemId = dataContainer.get(key, PersistentDataType.STRING);

    return Objects.equals(itemId, customId());
  }

}