package dev.michaud.greenpanda.core.blocks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import dev.michaud.greenpanda.core.GreenPandaCore;
import java.util.Set;
import org.bukkit.entity.Player;

public class GPBlockPacketAdapter extends PacketAdapter {

  public GPBlockPacketAdapter(GreenPandaCore plugin) {
    super(plugin, ListenerPriority.NORMAL,
        Set.of(PacketType.Play.Server.MAP_CHUNK,
            PacketType.Play.Server.BLOCK_CHANGE,
            PacketType.Play.Server.MULTI_BLOCK_CHANGE),
        ListenerOptions.ASYNC);
  }

  @Override
  public void onPacketSending(PacketEvent event) {

    PacketContainer packet = event.getPacket();
    Player player = event.getPlayer();

    if (packet.getType() == PacketType.Play.Server.MAP_CHUNK) {

      Integer chunkX = packet.getIntegers().readSafely(0);
      Integer chunkZ = packet.getIntegers().readSafely(1);

//      ClientboundLevelChunkWithLightPacket chunkWithLightPacket = (ClientboundLevelChunkWithLightPacket) packet.getHandle();
//      ClientboundLevelChunkPacketData chunkData = getChunkData(chunkWithLightPacket);
    }

  }

//  public ClientboundLevelChunkPacketData getChunkData(ClientboundLevelChunkWithLightPacket packet) {
//    return packet.d();
//  }

}