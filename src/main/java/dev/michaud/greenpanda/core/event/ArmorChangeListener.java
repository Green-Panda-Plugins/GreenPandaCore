package dev.michaud.greenpanda.core.event;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import dev.michaud.greenpanda.core.GreenPandaCore;
import dev.michaud.greenpanda.core.armor.ArmorType;
import dev.michaud.greenpanda.core.armor.EntityArmorChangeEvent;
import dev.michaud.greenpanda.core.armor.EntityArmorChangeEvent.EquipMethod;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Listens for armor change events to pass to {@link EntityArmorChangeEvent}.
 */
public class ArmorChangeListener implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  private void onPlayerArmorChange(PlayerArmorChangeEvent event) {

    Player player = event.getPlayer();
    ItemStack oldItem = event.getOldItem();
    ItemStack newItem = event.getNewItem();
    ArmorType armorType = ArmorType.fromSlot(event.getSlotType());

    EntityArmorChangeEvent armorEvent = callEvent(player, EquipMethod.PLAYER, oldItem, newItem, armorType);

    if (armorEvent.isCancelled()) {
      GreenPandaCore.getCore().getServer().getLogger()
          .warning("Can't cancel PlayerArmorChange event");
    }

  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  private void onDispenseArmor(BlockDispenseArmorEvent event) {

    LivingEntity entity = event.getTargetEntity();
    if (entity instanceof Player) {
      return;
    }

    ItemStack newItem = event.getItem();
    ArmorType armorType = ArmorType.fromMaterial(newItem.getType());
    EntityArmorChangeEvent armorEvent = callEvent(entity, EquipMethod.DISPENSER, null, newItem, armorType);

    if (armorEvent.isCancelled()) {
      event.setCancelled(true);
    }

  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  private void onEntityDeath(EntityDeathEvent event) {

    LivingEntity entity = event.getEntity();
    EntityEquipment equipment = entity.getEquipment();

    if (equipment == null) {
      return;
    }

    ItemStack[] armor = equipment.getArmorContents();

    for (ItemStack item : armor) {
      entityDeathItem(entity, item);
    }

  }

  private static void entityDeathItem(@NotNull LivingEntity entity, ItemStack item) {

    if (item == null || item.getType().isEmpty()) {
      return;
    }

    ArmorType armorType = ArmorType.fromMaterial(item.getType());
    EntityArmorChangeEvent armorEvent = callEvent(entity, EquipMethod.DEATH, item, null, armorType);

    if (armorEvent.isCancelled()) {
      GreenPandaCore.getCore().getServer().getLogger().warning("Can't cancel death event");
    }

  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  private void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {

    ArmorStand armorStand = event.getRightClicked();
    ItemStack newItem = event.getPlayerItem();
    ItemStack oldItem = event.getArmorStandItem();
    ArmorType armorType = ArmorType.fromSlot(event.getSlot());

    EntityArmorChangeEvent armorEvent = callEvent(armorStand, EquipMethod.ARMOR_STAND, oldItem, newItem, armorType);

    if (armorEvent.isCancelled()) {
      event.setCancelled(true);
    }

  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  private void onEntityPickupItem(EntityPickupItemEvent event) {

    LivingEntity entity = event.getEntity();

    if (entity instanceof Player) {
      return;
    }

    ItemStack item = event.getItem().getItemStack();
    ArmorType armorType = ArmorType.fromMaterial(item.getType());

    if (armorType == null) {
      return;
    }

    EntityArmorChangeEvent armorEvent = callEvent(entity, EquipMethod.PICKUP, null, item, armorType);

    if (armorEvent.isCancelled()) {
      event.setCancelled(true);
    }

  }

  private static @NotNull EntityArmorChangeEvent callEvent(@NotNull LivingEntity entity, @NotNull EquipMethod method,
      @Nullable ItemStack oldArmor, @Nullable ItemStack newArmor, ArmorType slot) {

    EntityArmorChangeEvent armorEvent = new EntityArmorChangeEvent(entity, method, oldArmor, newArmor, slot);
    GreenPandaCore.getCore().getServer().getPluginManager().callEvent(armorEvent);

    return armorEvent;
  }

}