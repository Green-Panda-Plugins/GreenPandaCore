package dev.michaud.greenpanda.core.item;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MutableClassToInstanceMap;
import dev.michaud.greenpanda.core.GreenPandaCore;
import java.util.Collection;
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

  private static final Map<String, CustomItem> ID_TO_INSTANCE_MAP = new HashMap<>();
  private static final ClassToInstanceMap<CustomItem> CLASS_TO_INSTANCE_MAP = MutableClassToInstanceMap.create();

  /**
   * Check if the given ItemStack is an instance of the given custom item
   *
   * @param clazz The class of item
   * @param item  The item to check
   * @return True if the class is registered and the item is of the same type
   */
  public static boolean isCustomItem(Class<? extends CustomItem> clazz, ItemStack item) {

    final CustomItem instance = CLASS_TO_INSTANCE_MAP.get(clazz);

    if (instance != null) {
      return instance.isType(item);
    } else {
      return false;
    }
  }

  /**
   * Check if the given ItemStack is an instance of the given custom item
   *
   * @param id   The id of the custom item
   * @param item The item to check
   * @return True if the id is registered and the item is of the same type
   */
  public static boolean isCustomItem(String id, ItemStack item) {

    final CustomItem instance = ID_TO_INSTANCE_MAP.get(id);

    if (instance != null) {
      return instance.isType(item);
    } else {
      return false;
    }
  }

  /**
   * Registers the given custom item. An instance of the class will automatically be created, which
   * you can get and modify using {@link ItemRegistry#getInstance(Class)}. If the item is an
   * instance of {@link Craftable}, the recipe will also be added.
   * <p>Note that if the constructor of your item requires arguments, you will likely need to use
   * {@link ItemRegistry#register(Class, CustomItem)}, where you can pass your own instance.</p>
   *
   * @param clazz The class of item to register
   * @param <T>   The item class' type
   */
  public static <T extends CustomItem> void register(@NotNull Class<T> clazz) {

    final T instance;

    try {
      instance = clazz.getDeclaredConstructor().newInstance();
    } catch (ReflectiveOperationException e) {
      GreenPandaCore.severe("Couldn't register item: " + e);
      return;
    }

    register(clazz, instance);
  }

  /**
   * Registers the given custom item. Uses the provided instance, which you can get and modify using
   * {@link ItemRegistry#getInstance(Class)}. If the item is an instance of {@link Craftable}, the
   * recipe will also be added.
   *
   * @param clazz    The class of the item to register
   * @param instance The instance of the class to use
   * @param <T>      The item class' type
   */
  public static <T extends CustomItem> void register(@NotNull Class<T> clazz, @NotNull T instance) {

    final String key = instance.customId();
    final Plugin plugin = instance.getOwnerPlugin();

    if (key.isEmpty() || key.trim().isEmpty()) {
      throw new IllegalArgumentException("Key cannot be empty!");
    }

    if (!key.matches("^[a-zA-Z0-9_-]+$")) {
      throw new IllegalArgumentException(
          "Key should only contain alphanumeric characters, underlines, and hyphens!");
    }

    if (ID_TO_INSTANCE_MAP.containsKey(key)) {
      throw new IllegalArgumentException(
          "This item has already been registered! Did you accidentally use the same key twice?");
    }

    if (clazz.isAssignableFrom(Craftable.class)) {
      Craftable craftable = (Craftable) instance;
      plugin.getServer().addRecipe(craftable.recipe());
    }

    ID_TO_INSTANCE_MAP.put(key, instance);
    CLASS_TO_INSTANCE_MAP.putInstance(clazz, instance);
  }

  /**
   * De-registers the given custom item. Functionality of all instances of this item will revert to
   * the base item, but will keep all data. If the item is craftable, the recipe will also be
   * removed.
   *
   * @param clazz The class of item to de-register
   * @param <T>   The item class' type
   */
  public static <T extends CustomItem> void deregister(@NotNull Class<T> clazz) {

    final T customItem = CLASS_TO_INSTANCE_MAP.getInstance(clazz);

    if (customItem == null) {
      throw new IllegalArgumentException("Can't deregister item that isn't registered");
    }

    final Plugin plugin = customItem.getOwnerPlugin();

    if (customItem instanceof Craftable craftable) {
      try {
        plugin.getServer().removeRecipe(craftable.namespacedKey());
      } catch (Exception e) {
        plugin.getLogger().severe("Couldn't remove recipe from custom item: " + e);
      }
    }

    ID_TO_INSTANCE_MAP.values().remove(customItem);
    CLASS_TO_INSTANCE_MAP.remove(clazz);
  }

  /**
   * Gets an unmodifiable {@link Collection} of every {@link CustomItem} currently registered.
   *
   * @return A collection containing all registered items.
   */
  @Contract(pure = true)
  public static @UnmodifiableView @NotNull Collection<CustomItem> getRegistered() {
    return ImmutableList.copyOf(ID_TO_INSTANCE_MAP.values());
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
   * Gets an unmodifiable Set containing all registered classes
   *
   * @return A set containing all registered classes
   */
  public static @UnmodifiableView @NotNull Set<Class<? extends CustomItem>> getRegisteredClasses() {
    return ImmutableSet.copyOf(CLASS_TO_INSTANCE_MAP.keySet());
  }

  /**
   * Gets an unmodifiable Set containing all registered item ids.
   *
   * @return A set containing all registered item ids.
   */
  @Contract(pure = true)
  public static @UnmodifiableView @NotNull Set<String> getRegisteredKeys() {
    return ImmutableSet.copyOf(ID_TO_INSTANCE_MAP.keySet());
  }

  /**
   * Gets the custom item with the given identifier
   *
   * @param key The customId of the item to get.
   * @return The custom item with the given customId, or null if none exists.
   */
  @Contract("null -> null")
  public static @Nullable CustomItem findCustomItem(String key) {

    if (key == null || key.isBlank()) {
      return null;
    }

    return ID_TO_INSTANCE_MAP.get(key);
  }

  /**
   * Gets the custom item with the specified class.
   *
   * @param clazz The item's class. Must be registered.
   * @return Custom item registered with the given class.
   */
  public static <T extends CustomItem> T getInstance(@NotNull Class<T> clazz) {

    if (!CLASS_TO_INSTANCE_MAP.containsKey(clazz)) {
      throw new IllegalArgumentException(String.format(
          "Class %s isn't registered. Ensure that your items are being registered in onEnable.",
          clazz.getName()));
    }

    CustomItem instance = CLASS_TO_INSTANCE_MAP.get(clazz);

    if (clazz.isInstance(instance)) {
      return clazz.cast(instance);
    } else {
      throw new ClassCastException(String.format(
          "Instance of %s couldn't be cast to %s. Are you sure that you've registered your items correctly?",
          instance.getClass().getName(), clazz.getName()));
    }

  }

  /**
   * Gets the custom item that this item is, if any
   *
   * @param item The item to check
   * @return The custom item, or null if item isn't a custom item
   */
  @Contract("null -> null")
  public static CustomItem findFromItemStack(ItemStack item) {

    if (item == null || item.getType().isEmpty()) {
      return null;
    }

    for (CustomItem customItem : CLASS_TO_INSTANCE_MAP.values()) {
      if (customItem.isType(item)) {
        return customItem;
      }
    }

    return null;
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

    if (key.isBlank() || ID_TO_INSTANCE_MAP.containsKey(key)) {
      return false;
    }

    if (item instanceof Craftable craftable) {
      item.getOwnerPlugin().getServer().addRecipe(craftable.recipe());
    }

    ID_TO_INSTANCE_MAP.put(key, item);

    return true;
  }

}