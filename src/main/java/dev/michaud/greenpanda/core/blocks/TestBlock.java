package dev.michaud.greenpanda.core.blocks;

import dev.michaud.greenpanda.core.GreenPandaCore;
import dev.michaud.greenpanda.core.block.CustomBlock;
import dev.michaud.greenpanda.core.block.NoteblockState;
import dev.michaud.greenpanda.core.blocks.sounds.GenericStoneSoundGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.SoundGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestBlock extends CustomBlock {

  @Override
  public @NotNull JavaPlugin getOwnerPlugin() {
    return GreenPandaCore.getCore();
  }

  @Override
  public @NotNull String blockId() {
    return "test_block";
  }

  @Override
  public Component displayName() {
    return Component.text("Blue Nether Bricks")
        .decoration(TextDecoration.ITALIC, false);
  }

  @Override
  public @NotNull ItemStack[] getDrops(ItemStack tool) {
    return new ItemStack[] { this.makeItem() };
  }

  @Override
  public @NotNull NoteblockState getNoteblockState() {
    return NoteblockState.BANJO_0_UNPOWERED;
  }

  @Override
  public @Nullable SoundGroup getSoundGroup() {
    return new GenericStoneSoundGroup();
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