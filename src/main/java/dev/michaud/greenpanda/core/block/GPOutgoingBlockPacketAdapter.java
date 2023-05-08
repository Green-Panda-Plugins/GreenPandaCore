package dev.michaud.greenpanda.core.block;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.ChunkSectionType1_18;
import dev.michaud.greenpanda.core.GreenPandaCore;
import dev.michaud.greenpanda.core.block.data.ChunkCoordinates;
import dev.michaud.greenpanda.core.block.data.ChunkDataSnapshot;
import dev.michaud.greenpanda.core.block.data.CustomBlockDataSnapshot;
import dev.michaud.greenpanda.core.block.data.PersistentBlockData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

/**
 * Intercepts outgoing packets relating to custom blocks
 */
public class GPOutgoingBlockPacketAdapter extends PacketAdapter {

  public static final Type<ChunkSection> CHUNK_SECTION_TYPE = new ChunkSectionType1_18(
      Block.BLOCK_STATE_REGISTRY.size(),
      BuiltInRegistries.BIOME_SOURCE.size());

  public static final BlockState DEFAULT_NOTE_BLOCK_STATE = Blocks.NOTE_BLOCK.defaultBlockState();
  public static final int DEFAULT_NOTE_BLOCK_ID = Block.BLOCK_STATE_REGISTRY
      .getId(DEFAULT_NOTE_BLOCK_STATE);

  public GPOutgoingBlockPacketAdapter(GreenPandaCore plugin) {
    super(plugin, ListenerPriority.NORMAL,
        List.of(PacketType.Play.Server.MAP_CHUNK,
            PacketType.Play.Server.BLOCK_CHANGE,
            PacketType.Play.Server.MULTI_BLOCK_CHANGE),
        ListenerOptions.ASYNC); //TODO: Make sure async isn't being a silly goose
  }

  @Override
  public void onPacketSending(@NotNull PacketEvent event) {

    PacketContainer packet = event.getPacket();
    Player player = event.getPlayer();

    //When a client loads a chunk
    if (packet.getType() == PacketType.Play.Server.MAP_CHUNK) {
      //player.sendMessage("Map chunk");
      parseMapChunkPacket(packet, player);
    }

    //When a single block changes
    if (packet.getType() == PacketType.Play.Server.BLOCK_CHANGE) {
      //player.sendMessage("Block change");
      final ClientboundBlockUpdatePacket blockChangePacket = parseBlockChangePacket(packet, player);
      final PacketContainer updatedPacket = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE,
          blockChangePacket);
      event.setPacket(updatedPacket);
    }

    //When multiple blocks change in the same chunk on the same tick
    if (packet.getType() == PacketType.Play.Server.MULTI_BLOCK_CHANGE) {
      //player.sendMessage("Multi-block change");
      parseMultiBlockChangePacket(packet, player);
    }
  }

  //MAP_CHUNK
  private static void parseMapChunkPacket(@NotNull PacketContainer packet, @NotNull Player player) {

    final ClientboundLevelChunkWithLightPacket chunkWithLightPacket = (ClientboundLevelChunkWithLightPacket) packet.getHandle();
    final ClientboundLevelChunkPacketData chunkData = chunkWithLightPacket.getChunkData();
    final int chunkX = chunkWithLightPacket.getX();
    final int chunkZ = chunkWithLightPacket.getZ();

    final World world = player.getWorld(); //Concurrent API access... should be fine (I hope)
    final int worldMinHeight = world.getMinHeight();
    final CompletableFuture<ChunkDataSnapshot> snapshotFuture = world
        .getChunkAtAsync(chunkX, chunkZ, false, true)
        .thenApply(ChunkDataSnapshot::new);

    final int subChunkCount = calcTrueHeight(world) / 16;

    final FriendlyByteBuf readBuffer = chunkData.getReadBuffer();
    final byte[] byteArray = readBuffer.array();
    final ByteBuf byteBuf = Unpooled.copiedBuffer(byteArray);

    ChunkSection[] sections = new ChunkSection[subChunkCount];

    for (int i = 0; i < subChunkCount; i++) {
      try {
        sections[i] = CHUNK_SECTION_TYPE.read(byteBuf);
      } catch (Exception e) {
        GreenPandaCore.severe("Ran into an error parsing chunk: " + e);
      }
    }

    final ChunkDataSnapshot snapshot = snapshotFuture.join();
    boolean shouldUpdatePacket = false;

    for (int i = 0; i < subChunkCount; i++) {
      final ChunkSection section = sections[i];
      final DataPalette palette = section.palette(PaletteType.BLOCKS);

      if (palette == null) {
        continue;
      }

      for (int y = 0; y < 16; y++) {
        for (int x = 0; x < 16; x++) {
          for (int z = 0; z < 16; z++) {

            final int globalY = y + (16 * i) - Math.abs(worldMinHeight);
            final CustomBlockDataSnapshot<?> data = snapshot.getDataAt(x, globalY, z);
            final CustomBlock block = CustomBlockRegistry.findFromSnapshot(data);

            //Custom Block?
            if (block != null) {
              final int blockStateId = Block.BLOCK_STATE_REGISTRY.getId(createBlockState(block));
              palette.setIdAt(x, y, z, blockStateId);
              shouldUpdatePacket = true;
              continue;
            }

            //Note Block?
            final int blockId = palette.idAt(x, y, z);
            final BlockState blockState = Block.BLOCK_STATE_REGISTRY.byId(blockId);

            if (blockState != null && blockState.getBukkitMaterial() == Material.NOTE_BLOCK) {
              palette.setIdAt(x, y, z, DEFAULT_NOTE_BLOCK_ID);
              shouldUpdatePacket = true;
            }
          }
        }
      }
    }

    if (shouldUpdatePacket) {

      ByteBuf newByteBuf = Unpooled.buffer();

      Arrays.stream(sections).filter(Objects::nonNull).forEach(s -> {
        try {
          CHUNK_SECTION_TYPE.write(newByteBuf, s);
        } catch (Exception e) {
          e.printStackTrace();
        }
      });

      writeBytesToChunkPacket(chunkData, newByteBuf.array());
    }

  }

  //BLOCK_CHANGE
  private static @NotNull ClientboundBlockUpdatePacket parseBlockChangePacket(
      @NotNull PacketContainer packet, @NotNull Player player) {

    final ClientboundBlockUpdatePacket blockUpdatePacket = (ClientboundBlockUpdatePacket) packet.getHandle();
    final BlockPos position = blockUpdatePacket.getPos();
    final BlockState state = blockUpdatePacket.getBlockState();
    final Material material = state.getBukkitMaterial();

    final World world = player.getWorld();
    final int globalX = position.getX();
    final int globalZ = position.getZ();
    final int chunkX = globalX / 16;
    final int chunkZ = globalZ / 16;
    final int x = ChunkCoordinates.trueMod(globalX, 16);
    final int y = position.getY();
    final int z = ChunkCoordinates.trueMod(globalZ, 16);

    final Chunk chunk = world.getChunkAtAsync(chunkX, chunkZ, false, true).join();

    final String customBlockType = PersistentBlockData.getCustomBlockType(chunk, x, y, z);
    final CustomBlock customBlock = CustomBlockRegistry.findCustomBlock(customBlockType);

    if (customBlock != null) {
      return new ClientboundBlockUpdatePacket(position, createBlockState(customBlock));
    }

    if (material == Material.NOTE_BLOCK) {
      return new ClientboundBlockUpdatePacket(position, DEFAULT_NOTE_BLOCK_STATE);
    }

    return blockUpdatePacket;
  }

  //MULTI_BLOCK_CHANGE
  private static void parseMultiBlockChangePacket(@NotNull PacketContainer packet,
      @NotNull Player player) {

    final BlockPosition sectionPosition = packet.getSectionPositions().read(0);
    final short[] positions = packet.getShortArrays().read(0);
    final WrappedBlockData[] changedBlocks = packet.getBlockDataArrays().read(0);
    final int chunkX = sectionPosition.getX();
    final int chunkZ = sectionPosition.getZ();
    final int globalY = sectionPosition.getY() * 16;

    final ChunkDataSnapshot chunkSnapshot = player.getWorld()
        .getChunkAtAsync(chunkX, chunkZ, false, true)
        .thenApply(ChunkDataSnapshot::new)
        .join();

    for (int i = 0; i < changedBlocks.length; i++) {
      final WrappedBlockData blockData = changedBlocks[i];

      final int relativeBlockPos = positions[i];
      final int x = ((relativeBlockPos >>> 8) & 15);
      final int y = (relativeBlockPos & 15) + globalY;
      final int z = ((relativeBlockPos >>> 4) & 15);

      CustomBlockDataSnapshot<?> data = chunkSnapshot.getDataAt(x, y, z);
      CustomBlock block = CustomBlockRegistry.findFromSnapshot(data);

      //Custom Block?
      if (block != null) {
        changedBlocks[i] = createWrappedBlockData(block);
        continue;
      }

      //Note block?
      if (blockData.getType() == Material.NOTE_BLOCK) {
        changedBlocks[i] = WrappedBlockData.createData(Material.NOTE_BLOCK);
      }
    }

    packet.getBlockDataArrays().write(0, changedBlocks);
  }

  private static void writeBytesToChunkPacket(ClientboundLevelChunkPacketData packet, byte[] data) {

    try {
      Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
      unsafeField.setAccessible(true);
      Unsafe unsafe = (Unsafe) unsafeField.get(null);

      //Buffer = c, see https://nms.screamingsandals.org/1.19.4/net/minecraft/network/protocol/game/ClientboundLevelChunkPacketData.html
      Field ourField = ClientboundLevelChunkPacketData.class.getDeclaredField("c");
      long staticFieldOffset = unsafe.objectFieldOffset(ourField);
      unsafe.putObject(packet, staticFieldOffset, data);

    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }

  }

  private static @NotNull WrappedBlockData createWrappedBlockData(@NotNull CustomBlock block) {
    org.bukkit.block.data.type.NoteBlock data = (org.bukkit.block.data.type.NoteBlock) Material.NOTE_BLOCK.createBlockData();
    data.setNote(block.getNote());
    data.setInstrument(data.getInstrument());
    data.setPowered(block.isIndestructible());

    return WrappedBlockData.createData(data);
  }

  private static @NotNull BlockState createBlockState(@NotNull CustomBlock block) {
    final int note = block.getNoteId();
    final boolean powered = block.getPowered();

    final NoteBlockInstrument instrument = switch (block.getInstrument()) {
      case PIANO -> NoteBlockInstrument.HARP;
      case BASS_DRUM -> NoteBlockInstrument.BASEDRUM;
      case SNARE_DRUM -> NoteBlockInstrument.SNARE;
      case STICKS -> NoteBlockInstrument.HAT;
      case BASS_GUITAR -> NoteBlockInstrument.BASS;
      case FLUTE -> NoteBlockInstrument.FLUTE;
      case BELL -> NoteBlockInstrument.BELL;
      case GUITAR -> NoteBlockInstrument.GUITAR;
      case CHIME -> NoteBlockInstrument.CHIME;
      case XYLOPHONE -> NoteBlockInstrument.XYLOPHONE;
      case IRON_XYLOPHONE -> NoteBlockInstrument.IRON_XYLOPHONE;
      case COW_BELL -> NoteBlockInstrument.COW_BELL;
      case DIDGERIDOO -> NoteBlockInstrument.DIDGERIDOO;
      case BIT -> NoteBlockInstrument.BIT;
      case BANJO -> NoteBlockInstrument.BANJO;
      case PLING -> NoteBlockInstrument.PLING;
      case ZOMBIE -> NoteBlockInstrument.ZOMBIE;
      case SKELETON -> NoteBlockInstrument.SKELETON;
      case CREEPER -> NoteBlockInstrument.CREEPER;
      case DRAGON -> NoteBlockInstrument.DRAGON;
      case WITHER_SKELETON -> NoteBlockInstrument.WITHER_SKELETON;
      case PIGLIN -> NoteBlockInstrument.PIGLIN;
      case CUSTOM_HEAD -> NoteBlockInstrument.CUSTOM_HEAD;
    };

    return Blocks.NOTE_BLOCK.defaultBlockState()
        .setValue(NoteBlock.INSTRUMENT, instrument)
        .setValue(NoteBlock.NOTE, note)
        .setValue(NoteBlock.POWERED, powered);
  }

  private static int calcTrueHeight(@NotNull World world) {
    return (Math.abs(world.getMinHeight()) + world.getMaxHeight());
  }

}