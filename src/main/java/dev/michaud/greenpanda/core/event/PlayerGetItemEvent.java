package dev.michaud.greenpanda.core.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Triggers when a player get an item.
 * <p>
 * WARNING: Don't use this for anything you need precision for. Due to the nature of how this event
 * is triggered, there are quite a few false positives. Namely, when the player opens a container
 * and moves items between their inventory. Removing false negatives is prioritized over false positives.</p>
 */
public class PlayerGetItemEvent extends PlayerEvent implements Cancellable {

  public static final HandlerList HANDLERS = new HandlerList();

  private final ItemStack item;
  private final PlayerGetType eventType;
  private boolean isCancelled;

  public PlayerGetItemEvent(@NotNull Player who, ItemStack item, @NotNull PlayerGetType eventType) {
    super(who);

    this.item = item;
    this.eventType = eventType;
    this.isCancelled = false;
  }

  public ItemStack getItem() {
    return item;
  }

  public PlayerGetType getEventType() {
    return eventType;
  }

  public boolean isFromItemPickup() {
    return eventType == PlayerGetType.ITEM_PICKUP;
  }

  public boolean isFromContainer() {
    return eventType == PlayerGetType.FROM_CONTAINER;
  }

  public boolean isFromCommand() {
    return eventType == PlayerGetType.FROM_COMMAND;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public boolean isCancelled() {
    return isCancelled;
  }

  @Override
  public void setCancelled(boolean cancel) {
    isCancelled = cancel;
  }

  public enum PlayerGetType {
    ITEM_PICKUP,
    FROM_CONTAINER,
    FROM_COMMAND,
    UNKNOWN
  }

}