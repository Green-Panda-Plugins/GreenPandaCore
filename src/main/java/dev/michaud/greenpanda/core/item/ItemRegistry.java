package dev.michaud.greenpanda.core.item;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;

/**
 * Register custom items here. Stores the item id and the {@link CustomItem} class in a hashmap.
 */
public class ItemRegistry {

  private static final Map<String, CustomItem> map = new HashMap<>();

  /**
   * Gets the registry containing all registered items. In most cases, you should use other methods
   * in this class instead of querying this map directly.
   *
   * @return The map containing all registered items.
   */
  public static Map<String, CustomItem> getMap() {
    return map;
  }

  /**
   * Registers the given custom item. Uses the item's customId as the key, so it will fail if there
   * is already an item with that id. If the item inherits craftable, it will also register the
   * recipe.
   *
   * @param item The custom item to register.
   * @return True if the item was registered, false otherwise.
   */
  public static boolean registerItem(CustomItem item) {

    if (item == null) {
      return false;
    }

    final String key = item.customId();

    if (key.isBlank() || map.containsKey(key)) {
      return false;
    }

    if (item instanceof Craftable craftable) {
      Bukkit.getServer().addRecipe(craftable.recipe());
    }

    map.put(key, item);

    return true;

  }

  /**
   * Removes the given custom item from the registry. If the item inherits craftable, it will also
   * remove the recipe.
   *
   * @param key The key of the custom item to remove.
   * @return True if the item was removed, false otherwise.
   */
  public static boolean unregisterItem(String key) {

    if (key == null || key.isBlank() || !map.containsKey(key)) {
      return false;
    }

    final CustomItem item = map.get(key);

    return unregisterItem(item);
  }

  /**
   * Removes the given custom item from the registry. If the item inherits craftable, it will also
   * remove the recipe. Uses the item's customId as the key.
   *
   * @param item The item to remove.
   * @return True if the item was removed, false otherwise.
   */
  public static boolean unregisterItem(CustomItem item) {

    if (item == null) {
      return false;
    }

    final String key = item.customId();

    if (key.isBlank() || !map.containsKey(key)) {
      return false;
    }

    if (item instanceof Craftable craftable) {
      Bukkit.getServer().removeRecipe(craftable.namespacedKey());
    }

    map.remove(key);

    return true;

  }

  /**
   * Registers the given custom item, or replaces an existing item with the same customId if one
   * exists. Uses the item's customId as the key.
   *
   * @param item The item to register/ update.
   * @return True if the item was registered or updated, false otherwise.
   */
  public static boolean updateItem(CustomItem item) {

    if (item == null) {
      return false;
    }

    final String key = item.customId();

    if (map.containsKey(key)) {
      boolean unregistered = unregisterItem(key);

      if (!unregistered) {
        return false;
      }

    }

    return registerItem(item);

  }

  @Nullable
  public static CustomItem findCustomItem(String key) {

    if (key == null || key.isBlank()) {
      return null;
    }

    return map.get(key);

  }

}