package dev.michaud.greenpanda.core.blocks;

import dev.michaud.greenpanda.core.GreenPandaCore;
import dev.michaud.greenpanda.core.item.CustomItem;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class TestBlock extends CustomBlock {

  @Override
  public @NotNull JavaPlugin getOwnerPlugin() {
    return GreenPandaCore.getCore();
  }

  @Override
  public @NotNull String customId() {
    return "TestBlock";
  }

  @Override
  public CustomItem heldItem() {
    return null;
  }

  @Override
  public @NotNull ItemStack[] getDrops(ItemStack tool) {
    return new ItemStack[] {};
  }

  @Override
  public @NotNull Instrument getInstrument() {
    return Instrument.BANJO;
  }

  @Override
  public @NotNull Note getNote() {
    return new Note(0);
  }

  @Override
  public boolean getPowered() {
    return false;
  }

  @Override
  public boolean getBreakable() {
    return true;
  }

  @Override
  public double getBreakTime(ItemStack tool) {
    return 10;
  }

  @Override
  public boolean isPreferredTool(ItemStack tool) {
    return true;
  }

  @Override
  public boolean requiresPreferredToolForDrops() {
    return false;
  }
}