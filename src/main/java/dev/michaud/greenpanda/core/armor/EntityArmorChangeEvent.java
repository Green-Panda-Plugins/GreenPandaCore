package dev.michaud.greenpanda.core.armor;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when an entity's armor changes.
 */
public class EntityArmorChangeEvent extends EntityEvent implements Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();
  private final @NotNull EquipMethod equipMethod;
  private final @Nullable ItemStack oldArmor;
  private final @Nullable ItemStack newArmor;
  private final @NotNull ArmorType slot;
  private boolean isCancelled = false;

  public EntityArmorChangeEvent(@NotNull LivingEntity entity, @NotNull EquipMethod equipMethod,
      @Nullable ItemStack oldArmor, @Nullable ItemStack newArmor, @NotNull ArmorType slot) {

    super(entity);

    this.entity = entity;
    this.equipMethod = equipMethod;
    this.oldArmor = oldArmor;
    this.newArmor = newArmor;
    this.slot = slot;

  }

  @Override
  public boolean isCancelled() {
    return isCancelled;
  }

  @Override
  public void setCancelled(boolean cancel) {
    isCancelled = cancel;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLERS;
  }

  @Override
  public @NotNull LivingEntity getEntity() {
    return (LivingEntity) entity;
  }

  public static @NotNull HandlerList getHandlerList() {
    return HANDLERS;
  }

  public @NotNull EquipMethod getMethod() {
    return equipMethod;
  }

  public @Nullable ItemStack getOldArmor() {
    return oldArmor;
  }

  public @Nullable ItemStack getNewArmor() {
    return newArmor;
  }

  public @NotNull ArmorType getArmorSlot() {
    return slot;
  }

  public enum EquipMethod {

    /**
     * A player changing armor, including death, commands, and by breaking the item.
     */
    PLAYER,
    /**
     * An armor piece being dispensed from a dispenser.
     */
    DISPENSER,
    /**
     * An armor piece being dropped by an entity on death.
     */
    DEATH,
    /**
     * A player changing the armor on an armor stand.
     */
    ARMOR_STAND,
    /**
     * When an entity picks up an item from the ground
     */
    PICKUP

  }

}