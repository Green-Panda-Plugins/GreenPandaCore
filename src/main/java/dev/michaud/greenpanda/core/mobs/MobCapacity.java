package dev.michaud.greenpanda.core.mobs;

import dev.michaud.greenpanda.core.math.GetNearby;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.entity.SpawnCategory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MobCapacity {

  public static final int GLOBAL_CAP_CHUNK_DISTANCE = 8; //Distance around players to check for global cap
  public static final double MOB_NEARBY_PLAYER_RANGE = 128; //Distance around mobs to check for player

  /**
   * Checks if a mob can be spawned without surpassing the mob cap.
   *
   * @param location The spawn location to check
   * @param category The category of the mob
   * @return True if the mob would be allowed to be spawned.
   */
  public static boolean canFit(SpawnCategory category, Location location) {

    if (category == SpawnCategory.MISC) {
      return true;
    }

    final World world = location.getWorld();

    //Global cap
    final int globalCap = getGlobalCap(world, category);
    final int globalCount = getGlobalCount(world, category);

    if (globalCount >= globalCap) {
      return false;
    }

    //Per-player cap
    return GetNearby.getNearbyPlayers(location, MOB_NEARBY_PLAYER_RANGE)
        .stream()
        .anyMatch(p -> playerMobCapHasRoom(p, category));
  }

  /**
   * Gets the mob cap for a specific category. This is equivalent to
   * {@link World#getSpawnLimit(SpawnCategory)}
   *
   * @param world    The world to check
   * @param category The spawn category
   * @return The spawn limit for the given category
   */
  public static int getPerPlayerCap(@NotNull World world, SpawnCategory category) {
    return world.getSpawnLimit(category);
  }

  /**
   * Gets the maximum mobs that can fill the global mob cap. This number is scaled by the total
   * number of chunks within a 17x17 square around all players (additive). Chunks in the range of
   * multiple players are only counted once. The cap is then scaled as <code>mobCap * chunks /
   * 289</code>.
   *
   * @param world The world to check
   * @param category The spawn category to check
   * @return The global mob cap
   */
  public static int getGlobalCap(@NotNull World world, SpawnCategory category) {

    final int mobCap = world.getSpawnLimit(category);
    final List<Player> players = world.getPlayers();
    final Set<Chunk> eligibleChunks = new HashSet<>();

    for (Player player : players) {
      eligibleChunks.addAll(
          GetNearby.getSurroundingChunks(player.getChunk(), GLOBAL_CAP_CHUNK_DISTANCE));
    }

    return mobCap * eligibleChunks.size() / 289;

  }

  /**
   * Count the amount of mobs of a certain type loaded around this player. Persistent entities are
   * not counted.
   *
   * @param player   The player whose mob cap to check
   * @param category The category of mob to count
   * @return The number of mobs in the given category around the player
   */
  public static int getPerPlayerCount(@NotNull Player player, SpawnCategory category) {

    return (int) GetNearby.getNearbyLivingEntities(player.getLocation(), 128)
        .stream()
        .filter(entity -> getCategory(entity).equals(category))
        .filter(MobCapacity::nonPersistentAndLoaded)
        .count();

  }

  /**
   * Checks if this player has room in their per-player mob cap for the given mob category
   *
   * @param player   The player whose mob cap to check
   * @param category The category of mob to check
   * @return True if the player has room to spawn the given mob type
   */
  public static boolean playerMobCapHasRoom(@NotNull Player player, SpawnCategory category) {

    int max = getPerPlayerCap(player.getWorld(), category);
    int count = getPerPlayerCount(player, category);

    return count < max;

  }

  /**
   * Count the amount of mobs of a certain type loaded in the world. All loaded and non-persistent
   * entities are counted against the mob cap, even those not in range of a player or eligible for
   * spawning.
   *
   * @param world    The world to check
   * @param category The category of mob to count
   * @return The number of mobs loaded in the world.
   */
  public static int getGlobalCount(@NotNull World world, SpawnCategory category) {

    return (int) world.getEntities().stream()
        .filter(entity -> getCategory(entity).equals(category))
        .filter(MobCapacity::nonPersistentAndLoaded)
        .count();

  }

  /**
   * Checks the entity for the "persistence required" tag. This determines despawning behavior.
   * Persistent entities also don't count towards the mob cap. Always returns false if the entity
   * isn't an instance of {@link LivingEntity}.
   *
   * @param entity The entity to check
   * @return True if the entity persists
   */
  @Contract("null -> false")
  public static boolean nonPersistentAndLoaded(Entity entity) {

    if (!(entity instanceof LivingEntity livingEntity)) {
      return false;
    }

    return !livingEntity.isDead() && livingEntity.isValid() && livingEntity.getRemoveWhenFarAway();

  }

  /**
   * Get the category for a certain entity. If the entity isn't a mob, doesn't spawn naturally, or
   * otherwise shouldn't be counted in the mob cap then misc is returned. For other purposes than
   * for calculating the mob cap, this method may sometimes be inaccurate (e.g. illagers currently
   * taking part in a raid don't count towards the hostile mob cap, and as such are counted as
   * misc).
   *
   * @param entity The entity to categorize.
   * @return The mob category.
   */
  public static @NotNull SpawnCategory getCategory(Entity entity) {

    if (entity == null || !entity.isValid() || entity.isDead()) {
      return SpawnCategory.MISC;
    }

    if (entity instanceof Raider raider && isPartOfRaid(raider)) {
      return SpawnCategory.MISC;
    }

    EntityType type = entity.getType();

    return switch (type) {
      case ELDER_GUARDIAN, WITHER_SKELETON, STRAY, HUSK, ZOMBIE_VILLAGER, EVOKER, VINDICATOR, VEX,
          ILLUSIONER, CREEPER, SKELETON, SPIDER, GIANT, ZOMBIE, SLIME, GHAST, ZOMBIFIED_PIGLIN,
          ENDERMAN, CAVE_SPIDER, SILVERFISH, BLAZE, MAGMA_CUBE, ENDER_DRAGON, WITHER, WITCH,
          ENDERMITE, GUARDIAN, SHULKER, PHANTOM, DROWNED, PILLAGER, RAVAGER, HOGLIN, PIGLIN, ZOGLIN,
          PIGLIN_BRUTE, WARDEN -> SpawnCategory.MONSTER;
      case SKELETON_HORSE, TADPOLE, FROG, ZOMBIE_HORSE, DONKEY, MULE, PIG, SHEEP, COW, CHICKEN,
          WOLF, MUSHROOM_COW, OCELOT, HORSE, RABBIT, POLAR_BEAR, LLAMA, PARROT, TURTLE, CAT, PANDA,
          FOX, BEE, STRIDER, GOAT, ALLAY -> SpawnCategory.ANIMAL;
      case BAT -> SpawnCategory.AMBIENT;
      case SQUID, DOLPHIN -> SpawnCategory.WATER_ANIMAL;
      case COD, SALMON, PUFFERFISH, TROPICAL_FISH -> SpawnCategory.WATER_AMBIENT;
      case AXOLOTL -> SpawnCategory.AXOLOTL;
      case GLOW_SQUID -> SpawnCategory.WATER_UNDERGROUND_CREATURE;
      default -> SpawnCategory.MISC;
    };

  }

  private static boolean isPartOfRaid(@NotNull Raider raider) {

    if (!raider.isCanJoinRaid()) {
      return false;
    }

    World world = raider.getWorld();
    List<Raid> raids = world.getRaids();

    return raids.stream().anyMatch(raid -> raid.getRaiders().contains(raider));

  }

}