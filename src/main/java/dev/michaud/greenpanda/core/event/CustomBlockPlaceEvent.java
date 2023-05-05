package dev.michaud.greenpanda.core.event;

import dev.michaud.greenpanda.core.block.CustomBlock;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CustomBlockPlaceEvent extends BlockPlaceEvent {

  private static final HandlerList HANDLERS = new HandlerList();

  private final @NotNull CustomBlock customBlock;

  public CustomBlockPlaceEvent(@NotNull Block placedBlock, @NotNull BlockState replacedBlockState,
      @NotNull Block placedAgainst, @NotNull ItemStack itemInHand, @NotNull Player thePlayer, boolean canBuild,
      @NotNull EquipmentSlot hand, @NotNull CustomBlock customBlock) {
    super(placedBlock, replacedBlockState, placedAgainst, itemInHand, thePlayer, canBuild, hand);
    this.customBlock = customBlock;
  }

  public @NotNull CustomBlock getCustomBlock() {
    return customBlock;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLERS;
  }

  public static @NotNull HandlerList getHandlerList() {
    return HANDLERS;
  }

}