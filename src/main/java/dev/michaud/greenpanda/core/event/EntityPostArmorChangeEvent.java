package dev.michaud.greenpanda.core.event;

import dev.michaud.greenpanda.core.armor.ArmorType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called one tick after an entity's armor changes. Less buggy than handling the event on the same
 * tick, but doesn't happen immediately. To handle the event on the same tick use
 * {@link EntityArmorChangeEvent}.
 */
public class EntityPostArmorChangeEvent extends EntityEvent {

  private static final HandlerList HANDLERS = new HandlerList();
  private final @NotNull EntityArmorChangeEvent.EquipMethod equipMethod;
  private final @Nullable ItemStack oldArmor;
  private final @Nullable ItemStack newArmor;
  private final @NotNull ArmorType slot;

  public EntityPostArmorChangeEvent(@NotNull LivingEntity entity, @NotNull EntityArmorChangeEvent.EquipMethod equipMethod,
      @Nullable ItemStack oldArmor, @Nullable ItemStack newArmor, @NotNull ArmorType slot) {
    super(entity);

    this.equipMethod = equipMethod;
    this.oldArmor = oldArmor;
    this.newArmor = newArmor;
    this.slot = slot;
  }

  public @NotNull EntityArmorChangeEvent.EquipMethod getMethod() {
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

}