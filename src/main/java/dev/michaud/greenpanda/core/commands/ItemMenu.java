package dev.michaud.greenpanda.core.commands;

import dev.michaud.greenpanda.core.gui.ItemMenuHolder;
import dev.michaud.greenpanda.core.item.CustomItem;
import dev.michaud.greenpanda.core.item.ItemRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemMenu implements CommandExecutor {

  final static String TITLE_KEY = "greenpanda.gui.customitem.title";
  final static String PAGE_NUMBER_KEY = "greenpanda.gui.pagenumber";

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
      @NotNull String label, @NotNull String[] args) {

    if (!(sender instanceof Player player)) {
      return false;
    }

    final Inventory inventory = Bukkit.createInventory(
        new ItemMenuHolder(), 54,
        Component.translatable(TITLE_KEY).append(Component.text(" "))
            .append(Component.translatable(PAGE_NUMBER_KEY).args(Component.text(1), Component.text(1)))
    );

    final ItemStack[] inventoryItems = new ItemStack[54];
    final CustomItem[] customItems = ItemRegistry.getRegistered().toArray(new CustomItem[0]);

    for (int i = 0; i < Math.min(customItems.length, 54); i++) {
      inventoryItems[i] = customItems[i].makeItem();
    }

    inventory.setContents(inventoryItems);
    player.openInventory(inventory);

    return true;

  }
}