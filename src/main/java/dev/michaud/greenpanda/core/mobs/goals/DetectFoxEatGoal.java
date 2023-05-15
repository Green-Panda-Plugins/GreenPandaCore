package dev.michaud.greenpanda.core.mobs.goals;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import dev.michaud.greenpanda.core.GreenPandaCore;
import dev.michaud.greenpanda.core.event.FoxConsumeItemEvent;
import java.util.EnumSet;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Fox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Goal that detects when a fox eats an item
 */
public class DetectFoxEatGoal implements Goal<Fox>, Listener {

  public static final GoalKey<Fox> KEY = GoalKey.of(Fox.class,
      new NamespacedKey(GreenPandaCore.getCore(), "detect_fox_eat"));
  private final GreenPandaCore plugin;
  private final Fox fox;
  private ItemStack lastHeldItem;

  public DetectFoxEatGoal(@NotNull GreenPandaCore plugin, @NotNull Fox fox) {
    this.plugin = plugin;
    this.fox = fox;
    this.lastHeldItem = new ItemStack(getCurrentMouthItem());
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  private void onDropItem(@NotNull EntityDropItemEvent event) {
    if (event.getEntity() == fox) {
      deleteSelf();
    }
  }

  @Override
  public boolean shouldActivate() {
    return getCurrentMouthItem().getType().isEdible();
  }

  @Override
  public boolean shouldStayActive() {
    return shouldActivate();
  }

  @Override
  public void start() {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @Override
  public void stop() {
    callEvent();
    HandlerList.unregisterAll(this);
  }

  @Override
  public void tick() {
    if (fox.isDead() || !fox.isValid()) {
      deleteSelf();
      return;
    }

    if (!getCurrentMouthItem().equals(lastHeldItem) && !getCurrentMouthItem().getType().isEmpty()) {
      lastHeldItem = new ItemStack(getCurrentMouthItem());
    }
  }

  private void deleteSelf() {
    Bukkit.getMobGoals().removeGoal(fox, KEY);
    HandlerList.unregisterAll(this);
  }

  private void callEvent() {
    final FoxConsumeItemEvent event = new FoxConsumeItemEvent(fox, lastHeldItem);
    plugin.getServer().getPluginManager().callEvent(event);
  }

  private @NotNull ItemStack getCurrentMouthItem() {
    return fox.getEquipment().getItemInMainHand();
  }

  @Override
  public @NotNull GoalKey<Fox> getKey() {
    return KEY;
  }

  @Override
  public @NotNull EnumSet<GoalType> getTypes() {
    return EnumSet.of(GoalType.UNKNOWN_BEHAVIOR);
  }

}