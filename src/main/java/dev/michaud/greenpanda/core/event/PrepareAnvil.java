package dev.michaud.greenpanda.core.event;

import dev.michaud.greenpanda.core.GreenPandaCore;
import dev.michaud.greenpanda.core.item.AnvilRepairData;
import dev.michaud.greenpanda.core.item.AnvilRepairable;
import dev.michaud.greenpanda.core.item.CustomItem;
import dev.michaud.greenpanda.core.item.ItemRegistry;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PrepareAnvil implements Listener {

  @EventHandler
  private void onPrepareAnvil(@NotNull PrepareAnvilEvent event) {

    AnvilInventory inventory = event.getInventory();
    ItemStack item1 = inventory.getFirstItem();
    ItemStack item2 = inventory.getSecondItem();

    if (item1 == null || item2 == null) {
      return;
    }

    List<AnvilRepairable> options = ItemRegistry.getMap().values().stream()
        .filter(c -> validRepairable(c, item1, item2))
        .map(c -> (AnvilRepairable) c).toList();

    if (options.isEmpty()) {
      return;
    }

    if (options.size() > 1) {
      GreenPandaCore.getCore().getLogger().log(Level.WARNING,
          "More than one valid anvil recipe found. The first element will be chosen.");
    }

    AnvilRepairable repairable = options.get(0);
    AnvilRepairData data = repairable.anvilRepair(item1, item2, inventory.getRenameText());

    if (data == null) {
      return;
    }

    ItemStack result = data.getResult();
    int xpCost = data.getXpCost();
    int repairCost = data.getRepairCostAmount();
    int maxRepairCost = data.getMaxRepairCost();

    event.setResult(result);
    inventory.setRepairCost(xpCost);
    inventory.setRepairCostAmount(repairCost);
    inventory.setMaximumRepairCost(maxRepairCost);

  }

  @Contract("null, _, _ -> false")
  private static boolean validRepairable(CustomItem customItem, ItemStack item1, ItemStack item2) {

    if (customItem == null || item1 == null || item2 == null) {
      return false;
    }

    if (!(customItem instanceof AnvilRepairable repairable)) {
      return false;
    }

    if (!repairable.isType(item1)) {
      return false;
    }

    return repairable.validRepairMaterial(item2);

  }

}