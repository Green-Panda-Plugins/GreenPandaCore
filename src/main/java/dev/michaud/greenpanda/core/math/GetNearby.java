package dev.michaud.greenpanda.core.math;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

/**
 * Gets stuff nearby. All nearby entity methods are like {@link Location#getNearbyEntities} but
 * using a sphere instead of a bounding box.
 */
public class GetNearby {

  /**
   * Gets all nearby entities in the given spherical radius.
   *
   * @param location The location to search around
   * @param radius   The radius to search
   * @return A non-null unmodifiable set of every entity in the radius
   */
  public static @NotNull @UnmodifiableView Collection<Entity> getNearbyEntities(
      @NotNull Location location,
      double radius) {

    double radiusSquared = radius * radius;

    return location.getNearbyEntities(radius, radius, radius)
        .stream()
        .filter(entity -> entity.getLocation().distanceSquared(location) <= radiusSquared)
        .collect(Collectors.toUnmodifiableSet());

  }

  /**
   * Gets all nearby entities of a specific type in the given spherical radius
   *
   * @param location The location to search around
   * @param clazz    The class to filter by
   * @param radius   The radius to search
   * @param <T>      The type to filter by
   * @return A non-null unmodifiable set of every entity of the given type in the radius
   */
  public static @NotNull @UnmodifiableView <T extends Entity> Collection<T> getNearbyEntitiesByType(
      @NotNull Location location, @Nullable Class<? extends T> clazz, double radius) {

    double radiusSquared = radius * radius;

    return location.getNearbyEntitiesByType(clazz, radius)
        .stream()
        .filter(entity -> entity.getLocation().distanceSquared(location) <= radiusSquared)
        .collect(Collectors.toUnmodifiableSet());

  }

  /**
   * Gets every {@link Player} in the given spherical radius
   *
   * @param location The location to search around
   * @param radius   The radius to search
   * @return A non-null unmodifiable set of every player in the given radius
   */
  public static @NotNull @UnmodifiableView Collection<Player> getNearbyPlayers(Location location,
      double radius) {
    return getNearbyEntitiesByType(location, Player.class, radius);
  }

  /**
   * Gets every {@link LivingEntity} in the given spherical radius
   *
   * @param location The location to search around
   * @param radius   The radius to search
   * @return A non-null unmodifiable set of every living entity in the given radius.
   */
  public static @NotNull @UnmodifiableView Collection<LivingEntity> getNearbyLivingEntities(
      Location location,
      double radius) {
    return getNearbyEntitiesByType(location, LivingEntity.class, radius);
  }

  /**
   * Gets the surrounding chunks in a square around the given chunk. Only adds loaded and generated
   * chunks, meaning this method could result in an empty list.
   *
   * @param chunk    The center chunk
   * @param distance The distance from each end. E.g. a value of 8 results in a 17x17 square
   * @return A collection of each of the surrounding chunks.
   */
  public static @NotNull @UnmodifiableView Collection<Chunk> getSurroundingChunks(
      @NotNull Chunk chunk, int distance) {

    final Set<Chunk> loadedChunks = new HashSet<>();
    final World world = chunk.getWorld();

    final int minX = chunk.getX() - distance;
    final int maxX = chunk.getX() + distance;
    final int minZ = chunk.getZ() - distance;
    final int maxZ = chunk.getZ() + distance;

    for (int x = minX; x <= maxX; x++) {
      for (int z = minZ; z <= maxZ; z++) {

        if (world.isChunkLoaded(x, z)) {
          loadedChunks.add(world.getChunkAt(x, z));
        }

      }
    }

    return Collections.unmodifiableSet(loadedChunks);

  }

}