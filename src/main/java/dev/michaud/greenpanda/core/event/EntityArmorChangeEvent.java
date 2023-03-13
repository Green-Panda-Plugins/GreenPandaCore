package dev.michaud.greenpanda.core.event;

import dev.michaud.greenpanda.core.armor.ArmorType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when an entity's armor changes. Doesn't really work for creative players, due to the
 * nature of how creative works.
 *
 * @see EntityPostArmorChangeEvent
 */
public class EntityArmorChangeEvent extends EntityEvent implements Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();
  private final @NotNull EquipMethod equipMethod;
  private @Nullable ItemStack oldArmor;
  private @Nullable ItemStack newArmor;
  private final @NotNull ArmorType slot;
  private boolean cancelled;

  public EntityArmorChangeEvent(@NotNull LivingEntity entity, @NotNull EquipMethod equipMethod,
      @Nullable ItemStack oldArmor, @Nullable ItemStack newArmor, @NotNull ArmorType slot) {

    super(entity);

    this.equipMethod = equipMethod;
    this.oldArmor = oldArmor;
    this.newArmor = newArmor;
    this.slot = slot;
    cancelled = false;

  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  /**
   * Sets the cancellation state of this event. If it is cancelled, the plugin will try its best to
   * prevent the execution of the event on the server, but it may still happen. For example, you
   * cannot cancel the event if the equip method is <code>EquipMethod.DEATH</code> (but you can
   * still modify the dropped item)
   *
   * @param cancel true if you wish to cancel this event
   */
  @Override
  public void setCancelled(boolean cancel) {
    cancelled = cancel;
  }

  /**
   * Gets the reason the entity's armor changed.
   *
   * @return The armor change type
   */
  public @NotNull EquipMethod getMethod() {
    return equipMethod;
  }

  /**
   * Gets the armor previously worn. Will be null or air if the player wasn't wearing anything.
   *
   * @return The previously worn armor
   */
  public @Nullable ItemStack getOldArmor() {
    return oldArmor;
  }

  /**
   * Gets the new armor being equipped. Will be null or air if the player isn't equipping anything.
   *
   * @return The newly equipped armor
   */
  public @Nullable ItemStack getNewArmor() {
    return newArmor;
  }

  /**
   * Gets the type of armor being changed
   *
   * @return The armor type
   */
  public @NotNull ArmorType getArmorSlot() {
    return slot;
  }

  /**
   * Sets the armor being equipped. Can be a non-wearable item, but may cause unexpected behavior.
   *
   * @param newArmor The armor the player should equip
   */
  public void setNewArmor(@Nullable ItemStack newArmor) {
    this.newArmor = newArmor;
  }

  /**
   * Sets the armor being unequipped. Can be a non-wearable item, but may cause unexpected
   * behavior.
   *
   * @param oldArmor The armor the player should unequip
   */
  public void setOldArmor(@Nullable ItemStack oldArmor) {
    this.oldArmor = oldArmor;
  }

  @Override
  public @NotNull LivingEntity getEntity() {
    return (LivingEntity) entity;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLERS;
  }

  public static @NotNull HandlerList getHandlerList() {
    return HANDLERS;
  }

  public enum EquipMethod {

    /**
     * A player changing armor from their inventory
     */
    PLAYER_INVENTORY,

    /**
     * A player right-clicking with armor in hand
     */
    PLAYER_RIGHT_CLICK,

    /**
     * When a player drops armor from their equipment slot onto the ground.
     */
    PLAYER_DROP,

    /**
     * When a player dies, and drops their worn armor
     */
    PLAYER_DEATH,

    /**
     * An armor piece being dispensed from a dispenser.
     */
    DISPENSER,

    /**
     * An armor piece being dropped by an entity on death.
     */
    DEATH,

    /**
     * A player changing the armor on an armor stand. Doesn't include armor equipped on an armor
     * stand by a dispenser.
     */
    ARMOR_STAND,

    /**
     * When an armor stand breaks, and drops the armor it was wearing
     */
    ARMOR_STAND_BREAK,

    /**
     * When an entity picks up an item from the ground
     */
    PICKUP,

    /**
     * A player's armor breaking after running out of durability
     */
    ITEM_BREAK

  }

}