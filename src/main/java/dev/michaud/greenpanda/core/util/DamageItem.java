package dev.michaud.greenpanda.core.util;

import java.util.HashMap;
import java.util.Random;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

public class DamageItem {

  /**
   * Damages an item, taking unbreaking enchantments into account.
   * @param player The player with the item
   * @param item The item to damage
   * @return True if the item was damaged.
   */
  public static boolean damageItemWithUnbreaking(Player player, @NotNull ItemStack item) {
    return damageItemWithUnbreaking(player, item, new Random());
  }

  /**
   * Damages an item, taking unbreaking enchantments into account.
   * @param player The player with the item
   * @param item The item to damage
   * @param random The random instance to use
   * @return True if the item was damaged.
   */
  public static boolean damageItemWithUnbreaking(Player player, @NotNull ItemStack item, Random random) {

    if (!(item.getItemMeta() instanceof Damageable meta)) {
      return false;
    }

    if (meta.isUnbreakable()) {
      return false;
    }

    //Tool has (chance)% to break
    int level = meta.getEnchantLevel(Enchantment.DURABILITY);
    double chance = 100.0 / (level + 1);

    double rand = random.nextDouble() * 100;

    if (rand < chance) {
      return damageItem(player, item);
    }

    return false;
  }

  /**
   * Damages an item without taking unbreaking enchantments into account.
   * @param player The player with the item
   * @param item The item to damage
   * @return True if the item was damaged.
   */
  public static boolean damageItem(Player player, @NotNull ItemStack item) {
    if (!(item.getItemMeta() instanceof Damageable meta)) {
      return false;
    }

    if (meta.isUnbreakable()) {
      return false;
    }

    int damage = meta.getDamage();

    if (damage < item.getType().getMaxDurability()) {
      meta.setDamage(meta.getDamage() + 1);
      item.setItemMeta(meta);
      return true;
    } else {
      return breakItem(player, item, true);
    }
  }

  /**
   * Breaks an item.
   * @param player The player with the item
   * @param item The item to break
   * @param playSound Whether to play the breaking sound.
   * @return True if the item was broken.
   */
  public static boolean breakItem(@NotNull Player player, @NotNull ItemStack item, boolean playSound) {

    HashMap<Integer, ItemStack> remove = player.getInventory().removeItem(item);
    if (!remove.isEmpty()) {
      return false;
    }

    if (playSound) {
      player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
    }

    return true;

  }
}
