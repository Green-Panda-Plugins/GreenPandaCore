package dev.michaud.greenpanda.core.blocks;

import dev.michaud.greenpanda.core.GreenPandaCore;
import dev.michaud.greenpanda.core.block.CustomBlock;
import dev.michaud.greenpanda.core.block.NoteblockState;
import dev.michaud.greenpanda.core.blocks.sounds.GenericStoneSoundGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Instrument;
import org.bukkit.SoundGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NullBlock extends CustomBlock {

  public static final Component BLOCK_NAME = Component.translatable("greenpanda.blocks.null")
      .fallback("Block of Null");

  @Override
  public @NotNull String blockId() {
    return "null";
  }

  @Override
  public @NotNull ItemStack[] getDrops(ItemStack tool) {
    return new ItemStack[0];
  }

  @Override
  public @NotNull NoteblockState getNoteblockState() {
    return NoteblockState.of(Instrument.PIANO, 1, false);
  }

  @Override
  public @Nullable SoundGroup getSoundGroup() {
    return new GenericStoneSoundGroup();
  }

  @Override
  public double getBreakTime(ItemStack tool) {
    return 0;
  }

  @Override
  public boolean isPreferredTool(ItemStack tool) {
    return false;
  }

  @Override
  public boolean requiresPreferredToolForDrops() {
    return false;
  }

  @Override
  public boolean isIndestructible() {
    return true;
  }

  @Override
  public @NotNull JavaPlugin getOwnerPlugin() {
    return GreenPandaCore.getCore();
  }

  @Override
  public Component displayName() {
    return BLOCK_NAME.decoration(TextDecoration.ITALIC, false).color(NamedTextColor.LIGHT_PURPLE);
  }

}