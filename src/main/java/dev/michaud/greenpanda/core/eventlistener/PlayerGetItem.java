package dev.michaud.greenpanda.core.eventlistener;

import dev.michaud.greenpanda.core.event.PlayerGetItemEvent;
import dev.michaud.greenpanda.core.item.Craftable;
import dev.michaud.greenpanda.core.item.ItemRegistry;
import dev.michaud.greenpanda.core.item.RecipeUnlockable;
import java.util.Collection;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * When a player "gets" an item. Used for unlocking recipes.
 */
public class PlayerGetItem implements Listener {

  @EventHandler
  private void onPlayerGetItem(@NotNull PlayerGetItemEvent event) {

    Player player = event.getPlayer();
    ItemStack item = event.getItem();

    if (item == null || item.getType().isEmpty()) {
      return;
    }

    Material itemType = item.getType();

    Collection<NamespacedKey> recipesToUnlock = ItemRegistry.getValues(RecipeUnlockable.class)
        .stream()
        .filter(r -> itemType.equals(r.recipeRequirement()))
        .map(Craftable::namespacedKey)
        .collect(Collectors.toUnmodifiableSet());

    player.discoverRecipes(recipesToUnlock);
  }

}