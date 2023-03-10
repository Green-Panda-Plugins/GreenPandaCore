package dev.michaud.greenpanda.core.blocks;

import dev.michaud.greenpanda.core.item.CustomItem;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CustomBlockData {

  @NotNull JavaPlugin getOwnerPlugin();

  @NotNull String customId();

  @Nullable CustomItem heldItem();

  @NotNull ItemStack[] getDrops(ItemStack tool);

  @NotNull Instrument getInstrument();

  @NotNull Note getNote();

  boolean getPowered();

  boolean getBreakable();

  double getBreakTime(ItemStack tool);

  boolean isPreferredTool(ItemStack tool);

  boolean requiresPreferredToolForDrops();

}