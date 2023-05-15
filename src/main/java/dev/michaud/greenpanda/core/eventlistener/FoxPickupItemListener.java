package dev.michaud.greenpanda.core.eventlistener;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import dev.michaud.greenpanda.core.GreenPandaCore;
import dev.michaud.greenpanda.core.mobs.goals.DetectFoxEatGoal;
import org.bukkit.Bukkit;
import org.bukkit.entity.Fox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FoxPickupItemListener implements Listener {

  @EventHandler
  private void onFoxPickupItem(@NotNull EntityPickupItemEvent event) {

    if (!(event.getEntity() instanceof Fox foxEntity)) {
      return;
    }

    final ItemStack pickedUpItem = event.getItem().getItemStack();

    if (pickedUpItem.getType().isEdible()) {
      addGoal(foxEntity);
    }
  }

  @EventHandler
  private void onFoxAddToWorld(@NotNull EntityAddToWorldEvent event) {

    if (!(event.getEntity() instanceof Fox foxEntity)) {
      return;
    }

    final ItemStack mouthItem = foxEntity.getEquipment().getItemInMainHand();

    if (mouthItem.getType().isEdible()) {
      addGoal(foxEntity);
    }
  }

  private void addGoal(@NotNull Fox fox) {

    if (Bukkit.getMobGoals().hasGoal(fox, DetectFoxEatGoal.KEY)) {
      return; //Already has goal for some reason...
    }

    final Goal<Fox> goal = new DetectFoxEatGoal(GreenPandaCore.getCore(), fox);
    Bukkit.getMobGoals().addGoal(fox, 0, goal);
  }
}