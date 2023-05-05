package dev.michaud.greenpanda.core.block;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import dev.michaud.greenpanda.core.GreenPandaCore;
import dev.michaud.greenpanda.core.event.CustomBlockPlaceEvent;
import dev.michaud.greenpanda.core.util.MaterialInfo;
import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Display;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Marker;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

public class GPIncomingBlockPacketAdapter extends PacketAdapter {

  public GPIncomingBlockPacketAdapter(GreenPandaCore plugin) {
    super(plugin, PacketType.Play.Client.USE_ITEM);
  }

  private static void parseUseItemEvent(@NotNull PacketEvent packetEvent) {

    final Player player = packetEvent.getPlayer();
    final PacketContainer packet = packetEvent.getPacket();
    final ServerboundUseItemOnPacket useItemOnPacket = (ServerboundUseItemOnPacket) packet.getHandle();
    final InteractionHand hand = useItemOnPacket.getHand();
    final BlockHitResult hitResult = useItemOnPacket.getHitResult();

    if (hitResult.getType() != HitResult.Type.BLOCK) {
      return;
    }

    final ItemStack item = switch (hand) {
      case MAIN_HAND -> player.getInventory().getItemInMainHand();
      case OFF_HAND -> player.getInventory().getItemInOffHand();
    };

    final CustomBlock customBlock = CustomBlockRegistry.findFromItemStack(item);

    if (customBlock == null) {
      return;
    }

    BlockPos againstPos = hitResult.getBlockPos();
    Direction againstDir = hitResult.getDirection();
    BlockPos placedPos = againstPos.relative(againstDir);

    final World world = player.getWorld();
    final BlockData againstData = getBlockAtPos(world, againstPos).getBlockData();

    if (!player.isSneaking() && MaterialInfo.isInteractable(againstData)) {
      return;
    }

    if (MaterialInfo.isReplaceable(againstData)) {
      placedPos = againstPos;
    }

    final Block placedBlock = getBlockAtPos(world, placedPos);
    final Block placedAgainst = getBlockAtPos(world, againstPos);
    final BlockState replacedState = placedBlock.getState();

    if (entitiesInside(placedBlock)) {
      return;
    }

    placedBlock.setType(CustomBlock.REAL_BLOCK_MATERIAL);

    final CustomBlockPlaceEvent customBlockPlaceEvent = new CustomBlockPlaceEvent(
        placedBlock, replacedState, placedAgainst, item, player, true, getSlot(hand), customBlock);

    Bukkit.getPluginManager().callEvent(customBlockPlaceEvent);

    if (customBlockPlaceEvent.isCancelled() || !customBlockPlaceEvent.canBuild()) {
      placedBlock.setBlockData(replacedState.getBlockData());
    } else {
      customBlock.place(placedBlock.getLocation(), player, true, true);
      packetEvent.setCancelled(true);
    }

  }

  private static boolean entitiesInside(@NotNull Block block) {
    final World world = block.getWorld();
    final Collection<Entity> nearby = world.getNearbyEntities(
        BoundingBox.of(block),
        entity -> !placeableInsideOf(entity));
    return !nearby.isEmpty();
  }

  private static boolean placeableInsideOf(@NotNull Entity entity) {

    if (entity.isDead()) {
      return true;
    }

    return entity instanceof ExperienceOrb || entity instanceof Item || entity instanceof Projectile
        || entity instanceof Hanging || entity instanceof EnderSignal
        || entity instanceof FallingBlock || entity instanceof LightningStrike
        || entity instanceof EvokerFangs || entity instanceof Marker || entity instanceof Display
        || entity instanceof Interaction;
  }

  private static @NotNull Block getBlockAtPos(@NotNull World world, @NotNull BlockPos position) {
    return world.getBlockAt(position.getX(), position.getY(), position.getZ());
  }

  private static EquipmentSlot getSlot(InteractionHand hand) {
    return switch (hand) {
      case MAIN_HAND -> EquipmentSlot.HAND;
      case OFF_HAND -> EquipmentSlot.OFF_HAND;
    };
  }

  @Override
  public void onPacketReceiving(@NotNull PacketEvent packetEvent) {
    Bukkit.getScheduler().scheduleSyncDelayedTask(GreenPandaCore.getCore(),
        () -> parseUseItemEvent(packetEvent));
  }

}