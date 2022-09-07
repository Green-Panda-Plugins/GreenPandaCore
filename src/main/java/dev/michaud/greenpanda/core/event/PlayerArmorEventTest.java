package dev.michaud.greenpanda.core.event;

import dev.michaud.greenpanda.core.GreenPandaCore;
import dev.michaud.greenpanda.core.armor.EntityArmorChangeEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class PlayerArmorEventTest implements Listener {

  @EventHandler
  private void onPlayerArmorChange(EntityArmorChangeEvent event) {

    LivingEntity entity = event.getEntity();
    ItemStack oldItem = event.getOldArmor();
    ItemStack newItem = event.getNewArmor();

    if (oldItem != null && !oldItem.getType().isEmpty()) {
      GreenPandaCore.getCore().getServer().getConsoleSender().sendMessage(
          entity.name()
              .append(Component.text(" unequipped "))
              .append(oldItem.displayName())
      );
    }

    if (newItem != null && !newItem.getType().isEmpty()) {
      GreenPandaCore.getCore().getServer().getConsoleSender().sendMessage(
          entity.name()
              .append(Component.text(" equipped "))
              .append(newItem.displayName())
      );
    }

  }

}
