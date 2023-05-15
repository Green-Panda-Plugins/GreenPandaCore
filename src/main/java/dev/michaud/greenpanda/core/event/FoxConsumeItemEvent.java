package dev.michaud.greenpanda.core.event;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * When a {@link org.bukkit.entity.Fox Fox} picks up a food item and eats it. This is different from
 * a player right-clicking to feed an animal.
 */
public class FoxConsumeItemEvent extends EntityEvent {

  private static final HandlerList HANDLERS = new HandlerList();

  private final @NotNull ItemStack consumedItem;

  public FoxConsumeItemEvent(@NotNull Fox fox, @NotNull ItemStack item) {
    super(fox);
    this.consumedItem = item;
  }

  @Override
  public @NotNull Fox getEntity() {
    return (Fox) super.getEntity();
  }

  @Override
  public @NotNull EntityType getEntityType() {
    return EntityType.FOX;
  }

  public @NotNull ItemStack getItemConsumed() {
    return consumedItem;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}