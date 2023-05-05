package dev.michaud.greenpanda.core.block;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.MutableClassToInstanceMap;
import dev.michaud.greenpanda.core.block.data.CustomBlockDataSnapshot;
import dev.michaud.greenpanda.core.item.CustomItem;
import dev.michaud.greenpanda.core.item.ItemRegistry;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

/**
 * Register custom blocks here. Since a {@link CustomBlock} is also necessarily a
 * {@link CustomItem}, registering a block here will also register the item in the
 * {@link ItemRegistry} under the same ID and instance. <b>(So don't try to register a block as an
 * item, or it will fail)</b>
 */
public class CustomBlockRegistry {

  private static final Map<String, CustomBlock> ID_TO_INSTANCE_MAP = new ConcurrentHashMap<>();
  private static final ClassToInstanceMap<CustomBlock> CLASS_TO_INSTANCE_MAP = MutableClassToInstanceMap.create(
      new ConcurrentHashMap<>());
  private static final EnumMap<NoteblockState, CustomBlock> STATE_TO_INSTANCE_MAP = new EnumMap<>(
      NoteblockState.class);

  /**
   * Registers the given block type
   *
   * @param clazz The class of block to register
   * @param <T>   The block class' type
   */
  public static <T extends CustomBlock> void register(@NotNull Class<T> clazz) {

    ItemRegistry.register(clazz);
    final T instance = ItemRegistry.getInstance(clazz);
    final String key;
    final NoteblockState state;

    try {
      key = instance.blockId();
      state = instance.getNoteblockState();

      if (key.isEmpty() || key.trim().isEmpty()) {
        throw new IllegalArgumentException("Key cannot be empty!");
      }

      if (!key.matches("^[a-zA-Z0-9_-]+$")) {
        throw new IllegalArgumentException(
            "Key should only contain alphanumeric characters, underlines, and hyphens!");
      }

      if (ID_TO_INSTANCE_MAP.containsKey(key) || CLASS_TO_INSTANCE_MAP.containsKey(clazz)) {
        throw new IllegalArgumentException(
            "This item has already been registered! Did you accidentally use the same key twice?");
      }

      if (STATE_TO_INSTANCE_MAP.containsKey(state)) {
        throw new IllegalArgumentException("Noteblock state of " + state + " has already been "
            + "registered by " + STATE_TO_INSTANCE_MAP.get(state).getOwnerPlugin()
            + ".");
      }

      if (state.isDefault()) {
        throw new IllegalArgumentException("The default noteblock states (PIANO_0_POWERED and "
            + "PIANO_0_UNPOWERED) are reserved, and cannot be registered as custom blocks.");
      }

    } catch (Exception e) {
      ItemRegistry.deregister(clazz);
      throw new RuntimeException("Couldn't register block: " + e);
    }

    CLASS_TO_INSTANCE_MAP.putInstance(clazz, instance);
    ID_TO_INSTANCE_MAP.put(key, instance);
    STATE_TO_INSTANCE_MAP.put(state, instance);
  }

  /**
   * Register all the given block types.
   *
   * @param clazz1 The first class of block to register
   * @param others Other blocks to register
   */
  @SafeVarargs
  public static void registerAll(@NotNull Class<? extends CustomBlock> clazz1, Class<? extends CustomBlock> @NotNull ... others) {

    register(clazz1);

    for (var clazz : others) {
      register(clazz);
    }
  }

  /**
   * Gets all registered instances.
   *
   * @return All registered instances
   */
  @Contract(pure = true)
  public static @UnmodifiableView @NotNull Collection<CustomBlock> getRegistered() {
    return ImmutableList.copyOf(CLASS_TO_INSTANCE_MAP.values());
  }

  /**
   * Gets the custom block with the given identifier. Thread safe, backed by
   * {@link ConcurrentHashMap}.
   *
   * @param key The id to look up
   * @return The {@link CustomBlock} instance with the given id, or null if none exists
   */
  @Contract("null -> null")
  public static @Nullable CustomBlock findCustomBlock(String key) {
    if (key == null || key.isEmpty()) {
      return null;
    }

    return ID_TO_INSTANCE_MAP.get(key);
  }

  /**
   * Gets the custom block with the specified class.
   *
   * @param clazz The block's class
   * @return Custom block instance with the given class, or null if none exists
   */
  public static <T extends CustomBlock> T getInstance(@NotNull Class<T> clazz) {

    if (!CLASS_TO_INSTANCE_MAP.containsKey(clazz)) {
      throw new IllegalArgumentException(String.format(
          "Class %s isn't registered. Ensure that your block is being registered on enable!",
          clazz.getName()));
    }

    CustomBlock instance = CLASS_TO_INSTANCE_MAP.getInstance(clazz);

    if (clazz.isInstance(instance)) {
      return clazz.cast(instance);
    } else {
      throw new ClassCastException(String.format(
          "Instance of %s couldn't be cast to %s. Are you sure that you've registered your block correctly?",
          instance == null ? "null" : instance.getClass().getName(), clazz.getName()));
    }

  }

  /**
   * Gets the custom block that this item represents
   *
   * @param item The item to check
   * @return The custom block if found, or null if none is registered
   */
  @Contract("null -> null")
  public static @Nullable CustomBlock findFromItemStack(ItemStack item) {

    if (item == null || item.getType().isEmpty()) {
      return null;
    }

    for (CustomBlock block : CLASS_TO_INSTANCE_MAP.values()) {
      if (block.isType(item)) {
        return block;
      }
    }

    return null;
  }

  /**
   * Gets the custom block instance represented by this snapshot. If the type hasn't been
   * registered, returns null.
   *
   * @param snapshot The block data snapshot to look up
   * @return The custom block instance, or null if none exists.
   */
  @Contract("null -> null")
  public static @Nullable CustomBlock findFromSnapshot(CustomBlockDataSnapshot<?> snapshot) {
    if (snapshot == null) {
      return null;
    }

    return findCustomBlock(snapshot.getTypeIdentifier());
  }

  /**
   * Returns true if the given class has been registered as a custom block
   *
   * @param clazz The class to look up
   * @return True if the class has been registered
   */
  @Contract("null -> false")
  public static boolean has(Class<? extends CustomBlock> clazz) {
    if (clazz == null) {
      return false;
    }

    return CLASS_TO_INSTANCE_MAP.containsKey(clazz);
  }

  /**
   * Returns true if the given id has been registered as a custom block
   *
   * @param key The id to look up
   * @return True if the id has been registered
   */
  @Contract("null -> false")
  public static boolean has(String key) {
    if (key == null || key.isEmpty()) {
      return false;
    }

    return ID_TO_INSTANCE_MAP.containsKey(key);
  }

  /**
   * Returns true if the given state has been registered as a custom block
   *
   * @param state The state to look up
   * @return True if the state has been registered
   */
  @Contract("null -> false")
  public static boolean has(NoteblockState state) {
    if (state == null) {
      return false;
    }

    return STATE_TO_INSTANCE_MAP.containsKey(state);
  }

}