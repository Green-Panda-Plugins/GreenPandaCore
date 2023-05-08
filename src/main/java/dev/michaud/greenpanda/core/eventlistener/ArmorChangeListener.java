package dev.michaud.greenpanda.core.eventlistener;

import dev.michaud.greenpanda.core.GreenPandaCore;
import dev.michaud.greenpanda.core.armor.ArmorType;
import dev.michaud.greenpanda.core.event.EntityArmorChangeEvent;
import dev.michaud.greenpanda.core.event.EntityArmorChangeEvent.EquipMethod;
import dev.michaud.greenpanda.core.event.EntityPostArmorChangeEvent;
import dev.michaud.greenpanda.core.util.MaterialInfo;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Listens for armor change events to pass to {@link EntityArmorChangeEvent}.
 */
public class ArmorChangeListener implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  private void onPlayerClickInventory(@NotNull InventoryClickEvent event) {

    final Player player = (Player) event.getWhoClicked();
    final ItemStack oldArmor;
    final ItemStack newArmor;

    final InventoryAction action = event.getAction();
    final ArmorType armorType = ArmorType.fromSlot(event.getSlot());

    switch (action) {

      case PLACE_ALL, PLACE_ONE, PLACE_SOME, SWAP_WITH_CURSOR -> {
        newArmor = event.getCursor();
        oldArmor = event.getCurrentItem();
        placeArmor(event, player, oldArmor, newArmor, armorType);
      }

      case PICKUP_ALL, PICKUP_ONE, PICKUP_SOME, PICKUP_HALF -> {
        oldArmor = event.getCurrentItem();
        pickupArmor(event, player, oldArmor, armorType);
      }

      case DROP_ALL_SLOT, DROP_ONE_SLOT -> {
        oldArmor = event.getCurrentItem();
        dropArmor(event, player, oldArmor, armorType);
      }

      case MOVE_TO_OTHER_INVENTORY -> {

        if (armorType == null) { //Equip
          newArmor = event.getCurrentItem();
          shiftClickEquipArmor(event, player, newArmor);
        } else { //Unequip
          oldArmor = event.getCurrentItem();
          shiftClickUnequipArmor(event, player, oldArmor, armorType);
        }

      }

      case HOTBAR_SWAP -> {
        newArmor = player.getInventory().getItem(event.getHotbarButton());
        oldArmor = event.getCurrentItem();

        hotbarSwap(event, player, oldArmor, newArmor, armorType);
      }

      default -> {
      }
    }

  }

  private void placeArmor(@NotNull InventoryClickEvent event, @NotNull Player player,
      @Nullable ItemStack oldArmor, @Nullable ItemStack newArmor, @Nullable ArmorType armorType) {

    if (armorType == null
        || newArmor != null && armorType != ArmorType.fromMaterial(newArmor.getType())) {
      return; //Null or not clicking the right slot
    }

    EntityArmorChangeEvent newEvent = callEvent(player, EquipMethod.PLAYER_INVENTORY, oldArmor,
        newArmor, armorType);

    playerSetArmor(player, armorType, newEvent.getNewArmor());
    player.setItemOnCursor(newEvent.getOldArmor());
    player.updateInventory();

    event.setCancelled(true);

  }

  private void pickupArmor(@NotNull InventoryClickEvent event, @NotNull Player player,
      @Nullable ItemStack oldArmor, @Nullable ArmorType armorType) {

    if (armorType == null) {
      return;
    }

    EntityArmorChangeEvent newEvent = callEvent(player, EquipMethod.PLAYER_INVENTORY, oldArmor,
        null, armorType);

    if (oldArmor != newEvent.getOldArmor()) {
      player.setItemOnCursor(newEvent.getOldArmor());
      player.updateInventory();
    }

    if (newEvent.getNewArmor() != null) {
      playerSetArmor(player, armorType, newEvent.getNewArmor());
      player.updateInventory();
    }

    if (newEvent.isCancelled()) {
      event.setCancelled(true);
    }

  }

  private void dropArmor(@NotNull InventoryClickEvent event, @NotNull Player player,
      @Nullable ItemStack oldArmor, @Nullable ArmorType armorType) {

    if (armorType == null) {
      return;
    }

    EntityArmorChangeEvent newEvent = callEvent(player, EquipMethod.PLAYER_DROP, oldArmor, null,
        armorType);

    if (oldArmor != newEvent.getOldArmor()) {
      event.setCurrentItem(newEvent.getOldArmor());
    }

    if (newEvent.getNewArmor() != null) {
      playerSetArmor(player, armorType, newEvent.getNewArmor());
      player.updateInventory();
    }

    if (newEvent.isCancelled()) {
      event.setCancelled(true);
    }

  }

  private void shiftClickEquipArmor(@NotNull InventoryClickEvent event, @NotNull Player player,
      @Nullable ItemStack newArmor) {

    if (newArmor == null) {
      return;
    }

    ArmorType armorType = ArmorType.fromMaterial(newArmor.getType());

    if (armorType == null) {
      return;
    }

    if (event.getClickedInventory() == null
        || event.getClickedInventory().getType() != InventoryType.PLAYER
        || event.getView().getTopInventory().getType() != InventoryType.CRAFTING) {
      return; //Not player inventory
    }

    ItemStack itemInSlot = playerGetArmor(player, armorType);
    if (!(itemInSlot == null || itemInSlot.getType().isEmpty())) {
      return; //Already item in armor slot
    }

    EntityArmorChangeEvent newEvent = callEvent(player, EquipMethod.PLAYER_INVENTORY, null,
        newArmor, armorType);

    playerSetArmor(player, armorType, newEvent.getNewArmor());
    event.setCurrentItem(newEvent.getOldArmor());
    player.updateInventory();

  }

  private void shiftClickUnequipArmor(@NotNull InventoryClickEvent event, @NotNull Player player,
      @Nullable ItemStack oldArmor, @NotNull ArmorType armorType) {

    EntityArmorChangeEvent newEvent = callEvent(player, EquipMethod.PLAYER_INVENTORY, oldArmor,
        null, armorType);

    if (oldArmor != newEvent.getOldArmor()) {
      event.setCurrentItem(newEvent.getOldArmor());
      player.updateInventory();
    }

    if (newEvent.isCancelled()) {
      event.setCancelled(true);
    }

  }

  private void hotbarSwap(@NotNull InventoryClickEvent event, @NotNull Player player,
      ItemStack oldArmor, ItemStack newArmor, @Nullable ArmorType armorType) {

    if (armorType == null) {
      return;
    }

    EntityArmorChangeEvent newEvent = callEvent(player, EquipMethod.PLAYER_INVENTORY, oldArmor,
        newArmor, armorType);

    if (newEvent.isCancelled()) {
      event.setCancelled(true);
      return;
    }

    playerSetArmor(player, armorType, newEvent.getNewArmor());
    player.getInventory().setItem(event.getHotbarButton(), newEvent.getOldArmor());
    player.updateInventory();

    event.setCancelled(true);

  }

  @EventHandler(priority = EventPriority.HIGHEST)
  private void onPlayerRightClick(@NotNull PlayerInteractEvent event) {

    final Player player = event.getPlayer();
    final Block block = event.getClickedBlock();
    final ItemStack handItem = event.getItem();
    final Action action = event.getAction();
    final EquipmentSlot hand = event.getHand(); //Could be main or offhand

    if (!action.isRightClick()
        || event.useItemInHand() == Result.DENY
        || hand == null) {
      return;
    }

    if (handItem == null || handItem.getType().isEmpty()) {
      return;
    }

    if (event.getAction() == Action.RIGHT_CLICK_BLOCK && handItem.getType().isBlock()) {
      return;
    }

    //Won't equip when selecting certain blocks
    if (block != null && !player.isSneaking()) {
      final BlockData data = block.getBlockData();
      if (MaterialInfo.isInteractable(data)) {
        return;
      }
    }

    ArmorType armorType = ArmorType.fromMaterial(handItem.getType());

    if (armorType == null) {
      return;
    }

    ItemStack wornArmor = playerGetArmor(player, armorType);

    EntityArmorChangeEvent newEvent = callEvent(player, EquipMethod.PLAYER_RIGHT_CLICK, wornArmor,
        handItem, armorType);

    playerSetArmor(player, armorType, newEvent.getNewArmor());
    player.getInventory().setItem(hand, newEvent.getOldArmor());
    player.updateInventory();

    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  private void onDispenseArmor(@NotNull BlockDispenseArmorEvent event) {

    LivingEntity entity = event.getTargetEntity();

    ItemStack newArmor = event.getItem();
    ArmorType armorType = ArmorType.fromMaterial(newArmor.getType());
    assert armorType != null;

    EntityArmorChangeEvent newEvent = callEvent(entity, EquipMethod.DISPENSER, null, newArmor,
        armorType);

    if (newEvent.isCancelled()) {
      event.setCancelled(true);
      return;
    }

    //Set new item
    ItemStack armorEventItem = newEvent.getNewArmor();

    if (armorEventItem == null) {
      armorEventItem = new ItemStack(Material.AIR);
    }

    ItemStack eventNewArmor = newEvent.getNewArmor();

    if (ArmorType.fromMaterial(eventNewArmor.getType()) == null) {
      //If the armor was changed to a non-armor item, we can't change the event item, otherwise it
      //will just drop on the floor (and that's littering!) or crash the server
      GreenPandaCore.getCore().getLogger().warning(
          "EntityArmorChangeEvent: Can't change armor from dispenser to a non-armor "
              + "item. Instead, handle this behavior after one tick (try EntityPostArmorChangeEvent)");

    } else {
      event.setItem(armorEventItem);
    }

  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  private void onEntityDeath(@NotNull EntityDeathEvent event) {

    final LivingEntity entity = event.getEntity();
    final EntityEquipment equipment = entity.getEquipment();
    final EquipMethod method = (entity instanceof Player) ? EquipMethod.PLAYER_DEATH : EquipMethod.DEATH;

    if (entity instanceof ArmorStand armorStand) {
      onArmorStandDeath(event, armorStand);
      return;
    }

    if (equipment == null) {
      return;
    }

    List<ItemStack> drops = event.getDrops();

    EquipmentSlot[] slots = new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST,
        EquipmentSlot.LEGS, EquipmentSlot.FEET};

    for (EquipmentSlot slot : slots) {

      ItemStack item = equipment.getItem(slot);
      ArmorType armorType = ArmorType.fromSlot(slot);
      int index = drops.indexOf(item);

      if (item.getType().isEmpty() || !drops.contains(item)) {
        return;
      }

      EntityArmorChangeEvent armorEvent = callEvent(entity, method, item, null, armorType);

      drops.set(index, armorEvent.getOldArmor());

    }

  }

  private void onArmorStandDeath(@NotNull EntityDeathEvent event, ArmorStand armorStand) {

    List<ItemStack> drops = event.getDrops();

    for (int i = 0; i < drops.size(); i++) {

      ItemStack item = drops.get(i);
      ArmorType armorType = ArmorType.fromItemStack(item);

      if (armorType != null) {
        EntityArmorChangeEvent armorEvent = callEvent(armorStand, EquipMethod.ARMOR_STAND_BREAK,
            item, null, armorType);
        drops.set(i, armorEvent.getOldArmor());
      }

    }

  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  private void onArmorStandManipulate(@NotNull PlayerArmorStandManipulateEvent event) {

    ArmorStand armorStand = event.getRightClicked();
    ItemStack newArmor = event.getPlayerItem();
    ItemStack oldArmor = event.getArmorStandItem();
    ArmorType armorType = ArmorType.fromSlot(event.getSlot());

    EntityArmorChangeEvent armorEvent = callEvent(armorStand, EquipMethod.ARMOR_STAND, oldArmor,
        newArmor, armorType);

    if (armorEvent.isCancelled()) {
      event.setCancelled(true);
    }

  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  private void onEntityPickupItem(@NotNull EntityPickupItemEvent event) {

    LivingEntity entity = event.getEntity();

    if (entity instanceof Player) {
      return;
    }

    Item itemEntity = event.getItem();
    ItemStack item = itemEntity.getItemStack();
    ArmorType armorType = ArmorType.fromMaterial(item.getType());

    if (armorType == null) {
      return;
    }

    EntityArmorChangeEvent armorEvent = callEvent(entity, EquipMethod.PICKUP, null,
        item.clone(), armorType);

    if (armorEvent.isCancelled()) {
      event.setCancelled(true);
    }

  }

  private void playerSetArmor(@NotNull Player player, @NotNull ArmorType armorType,
      @Nullable ItemStack item) {

    switch (armorType) {
      case HELMET -> player.getInventory().setHelmet(item);
      case CHESTPLATE -> player.getInventory().setChestplate(item);
      case LEGGINGS -> player.getInventory().setLeggings(item);
      case BOOTS -> player.getInventory().setBoots(item);
      default -> {
      }
    }

  }

  private void entitySetArmor(@NotNull LivingEntity entity, @NotNull ArmorType armorType,
      @Nullable ItemStack item) {

    EntityEquipment entityEquipment = entity.getEquipment();

    if (entityEquipment == null) {
      return;
    }

    switch (armorType) {
      case HELMET -> entityEquipment.setHelmet(item);
      case CHESTPLATE -> entityEquipment.setChestplate(item);
      case LEGGINGS -> entityEquipment.setLeggings(item);
      case BOOTS -> entityEquipment.setBoots(item);
      default -> {
      }
    }

  }

  private @Nullable ItemStack playerGetArmor(@NotNull Player player, @NotNull ArmorType armorType) {

    return switch (armorType) {
      case HELMET -> player.getInventory().getHelmet();
      case CHESTPLATE -> player.getInventory().getChestplate();
      case LEGGINGS -> player.getInventory().getLeggings();
      case BOOTS -> player.getInventory().getBoots();
    };

  }

  private static @NotNull EntityArmorChangeEvent callEvent(@NotNull LivingEntity entity,
      @NotNull EquipMethod method, @Nullable ItemStack oldArmor, @Nullable ItemStack newArmor,
      @NotNull ArmorType slot) {

    final PluginManager manager = GreenPandaCore.getCore().getServer().getPluginManager();

    //First
    final EntityArmorChangeEvent armorEvent = new EntityArmorChangeEvent(entity, method, oldArmor,
        newArmor, slot);

    manager.callEvent(armorEvent);

    //Second
    final EntityPostArmorChangeEvent lateArmorEvent = new EntityPostArmorChangeEvent(entity, method,
        armorEvent.getOldArmor(), armorEvent.getNewArmor(), slot);

    Bukkit.getScheduler().runTaskLater(GreenPandaCore.getCore(), () ->
        manager.callEvent(lateArmorEvent), 1);

    return armorEvent;
  }

}