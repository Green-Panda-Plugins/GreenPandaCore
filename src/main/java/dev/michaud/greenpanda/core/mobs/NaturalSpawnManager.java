package dev.michaud.greenpanda.core.mobs;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import dev.michaud.greenpanda.core.GreenPandaCore;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;
import java.util.stream.Stream;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

/**
 * Registry for all natural spawn data. Each class is registered with its own instance. After being
 * registered, the mob will automatically be naturally spawnable according to the data provided.
 */
public class NaturalSpawnManager {

  //TODO: Could probably make this asynchronous. Might help performance
  private static final ClassToInstanceMap<AnimalSpawnData> animalSpawns = MutableClassToInstanceMap.create();
  private static final ClassToInstanceMap<MonsterSpawnData> monsterSpawns = MutableClassToInstanceMap.create();

  /**
   * Gets all registered animal spawn data.
   *
   * @param clazz The class of the animal spawn data to get.
   * @param <T>   The type of the animal spawn data.
   * @return The animal spawn data instance.
   */
  public static <T extends AnimalSpawnData> @Nullable T getAnimalData(@NotNull Class<T> clazz) {
    return animalSpawns.getInstance(clazz);
  }

  /**
   * Gets all registered monster spawn data.
   *
   * @param clazz The class of the monster spawn data to get.
   * @param <T>   The type of the monster spawn data.
   * @return The monster spawn data instance.
   */
  public static <T extends MonsterSpawnData> @Nullable T getMonsterData(@NotNull Class<T> clazz) {
    return monsterSpawns.getInstance(clazz);
  }

  /**
   * Gets all registered spawn data.
   *
   * @param clazz The class of the spawn data to get.
   * @param <T>   The type of the spawn data.
   * @return The spawn data instance.
   */
  public static <T extends NaturalSpawnData> @Nullable T getData(@NotNull Class<T> clazz) {

    if (AnimalSpawnData.class.isAssignableFrom(clazz)) {
      Class<? extends AnimalSpawnData> animalClazz = clazz.asSubclass(AnimalSpawnData.class);
      return clazz.cast(getAnimalData(animalClazz));
    }

    if (MonsterSpawnData.class.isAssignableFrom(clazz)) {
      Class<? extends MonsterSpawnData> monsterClazz = clazz.asSubclass(MonsterSpawnData.class);
      return clazz.cast(getMonsterData(monsterClazz));
    }

    return null;

  }

  /**
   * Gets an unmodifiable collection of all registered animal spawn data.
   *
   * @return A collection of animal spawn data.
   */
  public static @NotNull @UnmodifiableView Collection<AnimalSpawnData> getAnimalValues() {
    return Collections.unmodifiableCollection(animalSpawns.values());
  }

  /**
   * Gets an unmodifiable collection of all registered monster spawn data.
   *
   * @return A collection of monster spawn data.
   */
  public static @NotNull @UnmodifiableView Collection<MonsterSpawnData> getMonsterValues() {
    return Collections.unmodifiableCollection(monsterSpawns.values());
  }

  /**
   * Gets an unmodifiable collection of all registered spawn data.
   *
   * @return A collection of all registered values.
   */
  public static @NotNull @UnmodifiableView Collection<? extends NaturalSpawnData> getValues() {
    return Stream.concat(getAnimalValues().stream(), getMonsterValues().stream()).toList();
  }

  /**
   * Registers spawn data for an animal. This method should be called at the start of your plugin.
   *
   * @param data The animal spawn data to register.
   * @param <T>  The type of the animal spawn data.
   */
  public static <T extends AnimalSpawnData> void registerAnimalSpawn(@NotNull Class<T> data) {

    if (animalSpawns.containsKey(data)) {
      throw new IllegalArgumentException("Animal spawn data has already been registered: " + data.getName());
    }

    try {
      T instance = data.getDeclaredConstructor().newInstance();
      animalSpawns.putInstance(data, instance);
      startSpawnCycle(instance);
    } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
      GreenPandaCore.getCore().getLogger().log(Level.SEVERE,
          "Couldn't register natural spawning for " + data.getName() + ": " + e);
    }

  }

  /**
   * Registers spawn data for a monster. This method should be called at the start of your plugin.
   *
   * @param data The monster spawn data to register.
   * @param <T>  The type of the monster spawn data.
   */
  public static <T extends MonsterSpawnData> void registerMonsterSpawn(@NotNull Class<T> data) {

    if (monsterSpawns.containsKey(data)) {
      throw new IllegalArgumentException("Monster spawn data has already been registered: " + data.getName());
    }

    try {
      T instance = data.getDeclaredConstructor().newInstance();
      monsterSpawns.putInstance(data, instance);
      startSpawnCycle(instance);
    } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
      GreenPandaCore.getCore().getLogger().log(Level.SEVERE,
          "Couldn't register natural spawning for " + data.getName() + ": " + e);
    }

  }

  private static void startSpawnCycle(@NotNull NaturalSpawnData data) {

    new BukkitRunnable() {
      @Override
      public void run() {
        Random random = new Random();
        onSpawnCycle(data, random);
      }
    }.runTaskTimer(GreenPandaCore.getCore(), 1, data.getSpawnCycleDuration());

  }

  private static void onSpawnCycle(@NotNull NaturalSpawnData data, @NotNull Random random) {

    for (Player player : GreenPandaCore.getCore().getServer().getOnlinePlayers()) {
      data.onSpawnCycle(player.getLocation(), random);
    }

  }

}