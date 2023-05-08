package dev.michaud.greenpanda.core.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Sets;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.BrewingStand;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Comparator;
import org.bukkit.block.data.type.DaylightDetector;
import org.bukkit.block.data.type.Furnace;
import org.bukkit.block.data.type.Grindstone;
import org.bukkit.block.data.type.HangingSign;
import org.bukkit.block.data.type.Jukebox;
import org.bukkit.block.data.type.Lectern;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.Snow;
import org.bukkit.block.data.type.Switch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public class MaterialInfo {

  public static final ImmutableSet<Material> REPLACEABLE_MATERIALS = Sets.immutableEnumSet(
      Material.HANGING_ROOTS, Material.GLOW_LICHEN, Material.SCULK_VEIN, Material.GRASS,
      Material.TALL_GRASS, Material.TALL_SEAGRASS, Material.FERN, Material.LARGE_FERN,
      Material.DEAD_BUSH, Material.VINE, Material.WARPED_ROOTS, Material.CRIMSON_ROOTS,
      Material.NETHER_SPROUTS, Material.SEAGRASS, Material.FIRE, Material.SOUL_FIRE,
      Material.SNOW
  );

  public static final ImmutableSet<Material> INTERACTABLE_BLOCKS = buildInteractableBlocks();

  @SuppressWarnings("UnstableApiUsage")
  private static @NotNull @UnmodifiableView ImmutableSet<Material> buildInteractableBlocks() {
    final Set<Class<? extends BlockData>> interactableClasses = ImmutableSet.of(
        Openable.class, Chest.class, Switch.class, DaylightDetector.class, BrewingStand.class,
        Comparator.class, Grindstone.class, Lectern.class, Bed.class, Furnace.class, Sign.class,
        HangingSign.class, NoteBlock.class, Jukebox.class
    );

    final Builder<Material> builder = ImmutableSet.builder();

    for (Material material : Material.values()) {
      final Class<?> data = material.data;
      if (data != null && interactableClasses.stream().anyMatch(clazz -> clazz.isAssignableFrom(data))) {
        builder.add(material);
      }
    }

    builder.add(Material.CRAFTING_TABLE, Material.ANVIL, Material.BREWING_STAND, Material.CAULDRON,
        Material.COMPOSTER, Material.LOOM, Material.STONECUTTER, Material.BELL, Material.CARTOGRAPHY_TABLE);

    return builder.build();
  }

  /**
   * If the given block data can be replaced by placing a block, e.g. grass, fire, etc.
   *
   * @param data The data to check
   * @return True if the given data is replaceable
   * @see Material#isSolid()
   */
  public static boolean isReplaceable(@NotNull BlockData data) {
    if (data instanceof Snow snowData) {
      return snowData.getLayers() == 1;
    }

    return REPLACEABLE_MATERIALS.contains(data.getMaterial());
  }

  /**
   * If the given data is an interactable block, and will prevent actions like placing a block
   * in favor of its own interaction (e.g. chests, doors, etc). Note that this method is only
   * relevant to situations where the player is using an empty hand, for example a cauldron is
   * interactable if it has water and the player uses a bucket. If you want to know if a
   * block is interactable under any situation, use {@link Material#isInteractable()}
   *
   * @param data The data to check
   * @return True if the given data is interactable
   */
  public static boolean isInteractable(@NotNull BlockData data) {
    if (data instanceof Jukebox jukeboxData) {
      return jukeboxData.hasRecord();
    }

    return INTERACTABLE_BLOCKS.contains(data.getMaterial());
  }

}