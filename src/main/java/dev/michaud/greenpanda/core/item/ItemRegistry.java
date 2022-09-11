package dev.michaud.greenpanda.core.item;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

/**
 * Register custom items here. Stores the item id and the {@link CustomItem} class in a hashmap.
 */
public class ItemRegistry {

  private static final Map<String, CustomItem> map = new HashMap<>();

  /**
   * Gets an unmodifiable view of the registry containing all registered items.
   *
   * @return The map containing all registered items.
   */
  @Contract(pure = true)
  public static @NotNull @UnmodifiableView Map<String, CustomItem> getMap() {
    return Collections.unmodifiableMap(map);
  }

  /**
   * Gets an unmodifiable {@link Collection} of every {@link CustomItem} currently registered.
   *
   * @return A collection containing all registered items.
   */
  @Contract(pure = true)
  public static @UnmodifiableView @NotNull Collection<CustomItem> getValues() {
    return getMap().values();
  }

  /**
   * Gets an unmodifiable {@link Collection} of every {@link CustomItem} currently registered with
   * the given class type.
   *
   * @param type The class type to filter by.
   * @return A collection containing all registered items of the given class type.
   */
  @Contract(pure = true)
  public static <A extends CustomItem> @UnmodifiableView @NotNull Collection<A> getValues(
      @NotNull Class<A> type) {

    return getValues().stream()
        .filter(type::isInstance)
        .map(type::cast)
        .toList();

  }

  /**
   * Gets an unmodifiable {@link Set} containing all registered item ids.
   *
   * @return A set containing all registered item ids.
   */
  @Contract(pure = true)
  public static @UnmodifiableView @NotNull Set<String> getKeys() {
    return getMap().keySet();
  }

  /**
   * Registers the given custom item. Uses the item's customId as the key, so it will fail if there
   * is already an item with that id. If the item inherits craftable, it will also register the
   * recipe.
   *
   * @param item The custom item to register.
   * @return True if the item was registered, false otherwise.
   */
  @Contract("null -> false")
  public static boolean registerItem(CustomItem item) {

    if (item == null) {
      return false;
    }

    final String key = item.customId();

    if (key.isBlank() || map.containsKey(key)) {
      return false;
    }

    if (item instanceof Craftable craftable) {
      item.getOwnerPlugin().getServer().addRecipe(craftable.recipe());
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
  @Contract("null -> false")
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
  @Contract("null -> false")
  public static boolean unregisterItem(CustomItem item) {

    if (item == null) {
      return false;
    }

    final String key = item.customId();

    if (key.isBlank() || !map.containsKey(key)) {
      return false;
    }

    if (item instanceof Craftable craftable) {
      item.getOwnerPlugin().getServer().removeRecipe(craftable.namespacedKey());
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
  @Contract("null -> false")
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

  /**
   * Gets the custom item with the given customId.
   *
   * @param key The customId of the item to get.
   * @return The custom item with the given customId, or null if none exists.
   */
  @Nullable
  @Contract("null -> null")
  public static CustomItem findCustomItem(String key) {

    if (key == null || key.isBlank()) {
      return null;
    }

    return map.get(key);

  }

}