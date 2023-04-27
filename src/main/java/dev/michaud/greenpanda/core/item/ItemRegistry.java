package dev.michaud.greenpanda.core.item;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

/**
 * Register custom items here. Stores the item id and the {@link CustomItem} class in a hashmap.
 */
public class ItemRegistry {

  private static final Map<String, CustomItem> CUSTOM_ITEM_MAP = new HashMap<>();
  private static final ClassToInstanceMap<CustomItem> INSTANCE_MAP = MutableClassToInstanceMap.create();

  public static boolean isCustomItem(Class<? extends CustomItem> clazz, ItemStack item) {
    CustomItem instance = INSTANCE_MAP.get(clazz);

    if (instance == null) {
      return false;
    }

    return instance.isType(item);
  }

  public static boolean isCustomItem(String id, ItemStack item) {
    CustomItem instance = CUSTOM_ITEM_MAP.get(id);

    if (instance == null) {
      return false;
    }

    return instance.isType(item);
  }

  /**
   * Registers the given custom item. If the item is also craftable, the recipe will be added. An
   * instance of the class will be created, which you can get and modify using
   * {@link ItemRegistry#getInstance(Class)}.
   *
   * @param clazz The class of item to register
   * @param <T>   The item class' type
   */
  public static <T extends CustomItem> void register(@NotNull Class<T> clazz) {

    final T instance;

    try {
      instance = clazz.getDeclaredConstructor().newInstance();
    } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
             NoSuchMethodException e) {
      e.printStackTrace();
      return;
    }

    final String key = instance.customId();
    final Plugin plugin = instance.getOwnerPlugin();

    if (key.isEmpty() || key.trim().isEmpty()) {
      throw new IllegalArgumentException("Key cannot be empty!");
    }

    if (!key.matches("^[a-zA-Z0-9_-]+$")) {
      throw new IllegalArgumentException(
          "Key should only contain alphanumeric characters, underlines, and hyphens!");
    }

    if (CUSTOM_ITEM_MAP.containsKey(key)) {
      throw new IllegalArgumentException(
          "This item has already been registered! Did you accidentally use the same key twice?");
    }

    if (clazz.isAssignableFrom(Craftable.class)) {
      Craftable craftable = (Craftable) instance;
      plugin.getServer().addRecipe(craftable.recipe());
    }

    INSTANCE_MAP.putInstance(clazz, instance);
    CUSTOM_ITEM_MAP.put(key, instance);
  }

  /**
   * Gets an unmodifiable {@link Collection} of every {@link CustomItem} currently registered.
   *
   * @return A collection containing all registered items.
   */
  @Contract(pure = true)
  public static @UnmodifiableView @NotNull Collection<CustomItem> getRegistered() {
    return CUSTOM_ITEM_MAP.values();
  }

  /**
   * Gets an unmodifiable {@link Collection} of every {@link CustomItem} currently registered with
   * the given class type.
   *
   * @param type The class type to filter by.
   * @return A collection containing all registered items of the given class type.
   */
  @Contract(pure = true)
  public static <A extends CustomItem> @UnmodifiableView @NotNull Collection<A> getRegistered(
      @NotNull Class<A> type) {

    return getRegistered().stream()
        .filter(type::isInstance)
        .map(type::cast)
        .toList();
  }

  /**
   * Gets the custom item with the given customId.
   *
   * @param key The customId of the item to get.
   * @return The custom item with the given customId, or null if none exists.
   */
  @Contract("null -> null")
  public static @Nullable CustomItem findCustomItem(String key) {

    if (key == null || key.isBlank()) {
      return null;
    }

    return CUSTOM_ITEM_MAP.get(key);
  }

  /**
   * Gets the custom item with the specified class.
   *
   * @param clazz The item's class. Must be registered.
   * @return Custom item registered with the given class.
   */
  public static <T extends CustomItem> T getInstance(@NotNull Class<T> clazz) {

    if (!INSTANCE_MAP.containsKey(clazz)) {
      throw new IllegalArgumentException(String.format(
          "Class %s isn't registered. Ensure that your items are being registered in onEnable.",
          clazz.getName()));
    }

    CustomItem instance = INSTANCE_MAP.get(clazz);

    if (clazz.isInstance(instance)) {
      return clazz.cast(instance);
    } else {
      throw new ClassCastException(String.format(
          "Instance of %s couldn't be cast to %s. Are you sure that you've registered your items correctly?",
          instance.getClass().getName(), clazz.getName()));
    }

  }

  /**
   * Gets an unmodifiable Set containing all registered item ids.
   *
   * @return A set containing all registered item ids.
   */
  @Contract(pure = true)
  public static @UnmodifiableView @NotNull Set<String> getRegisteredKeys() {
    return Collections.unmodifiableSet(CUSTOM_ITEM_MAP.keySet());
  }

  /**
   * Gets an unmodifiable Set containing all registered classes
   *
   * @return A set containing all registered classes
   */
  public static @UnmodifiableView @NotNull Set<Class<? extends CustomItem>> getRegisteredClasses() {
    return Collections.unmodifiableSet(INSTANCE_MAP.keySet());
  }

  /**
   * Registers the given custom item. Uses the item's customId as the key, so it will fail if there
   * is already an item with that id. If the item inherits craftable, it will also register the
   * recipe.
   *
   * @param item The custom item to register.
   * @return True if the item was registered, false otherwise.
   * @deprecated Depreciated, use instead {@link ItemRegistry#register(Class)}
   */
  @Contract("null -> false")
  @Deprecated(since = "0.1.0", forRemoval = true)
  public static boolean registerItem(CustomItem item) {

    if (item == null) {
      return false;
    }

    final String key = item.customId();

    if (key.isBlank() || CUSTOM_ITEM_MAP.containsKey(key)) {
      return false;
    }

    if (item instanceof Craftable craftable) {
      item.getOwnerPlugin().getServer().addRecipe(craftable.recipe());
    }

    CUSTOM_ITEM_MAP.put(key, item);

    return true;
  }

}