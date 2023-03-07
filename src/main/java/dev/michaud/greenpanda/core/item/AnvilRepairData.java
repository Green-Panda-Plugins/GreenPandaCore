package dev.michaud.greenpanda.core.item;

import org.bukkit.inventory.ItemStack;

/**
 * Data about an anvil repair. This is used to determine the result and information about the
 * repair.
 */
public abstract class AnvilRepairData {

  private ItemStack result;
  private int xpCost = 0;
  private int repairCostAmount = 0;
  private int maxRepairCost = 40;

  public AnvilRepairData(ItemStack result) {
    this.result = result;
  }

  /**
   * Get the result of the repair.
   *
   * @return The result of the repair.
   */
  public ItemStack getResult() {
    return result;
  }

  /**
   * Get the xp cost of the repair.
   *
   * @return The xp cost of the repair.
   */
  public int getXpCost() {
    return xpCost;
  }

  /**
   * Get the amount of items to be removed from the anvil to repair the item.
   *
   * @return The repair cost amount.
   */
  public int getRepairCostAmount() {
    return repairCostAmount;
  }

  /**
   * Get the maximum experience cost to be allowed by the current repair.
   *
   * @return The maximum cost of the repair.
   */
  public int getMaxRepairCost() {
    return maxRepairCost;
  }

  /**
   * Set the result of the repair.
   *
   * @param result The result of the repair.
   * @return This instance.
   */
  public AnvilRepairData result(ItemStack result) {
    this.result = result;
    return this;
  }

  /**
   * Set the xp cost of the repair.
   *
   * @param levels The xp cost of the repair.
   * @return This instance.
   */
  public AnvilRepairData xpCost(int levels) {
    xpCost = levels;
    return this;
  }

  /**
   * Set the amount of items to be removed from the anvil to repair the item.
   *
   * @param itemAmount The item cost.
   * @return This instance.
   */
  public AnvilRepairData itemCost(int itemAmount) {
    repairCostAmount = itemAmount;
    return this;
  }

  /**
   * Set the maximum experience cost to be allowed by the current repair. The default is 40 levels.
   *
   * @param levels The amount in levels
   * @return This instance.
   */
  public AnvilRepairData maxRepairCost(int levels) {
    maxRepairCost = levels;
    return this;
  }

}