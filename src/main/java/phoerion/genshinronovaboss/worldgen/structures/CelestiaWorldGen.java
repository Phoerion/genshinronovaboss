package phoerion.genshinronovaboss.worldgen.structures;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.structure.Structure;

import java.util.HashSet;
import java.util.Set;

public class CelestiaWorldGen {

    private static final Identifier CELESTIA_ID = new Identifier("genshinronovaboss", "celestia");

    private static final Set<ChunkPos> GENERATED = new HashSet<>();

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(CelestiaWorldGen::tick);
    }

    private static void tick(ServerWorld world) {

        if (world.getRegistryKey() != ServerWorld.OVERWORLD) {
            return;
        }

        world.getPlayers().forEach(player -> {

            BlockPos playerPos = player.getBlockPos();

            ChunkPos playerChunk = new ChunkPos(playerPos);

            int radius = 8;

            for (int dx = -radius; dx <= radius; dx++) {

                for (int dz = -radius; dz <= radius; dz++) {

                    ChunkPos chunkPos = new ChunkPos(playerChunk.x + dx, playerChunk.z + dz);

                    checkChunk(world, chunkPos);
                }
            }
        });
    }

    private static void checkChunk(ServerWorld world, ChunkPos chunkPos) {

        if (GENERATED.contains(chunkPos)) {
            return;
        }

        Structure structure = world.getServer().getRegistryManager().get(net.minecraft.registry.RegistryKeys.STRUCTURE).get(CELESTIA_ID);

        if (structure == null) {
            return;
        }

        StructureStart start = world.getChunk(chunkPos.x, chunkPos.z).getStructureStart(structure);

        if (start == null) {
            return;
        }

        if (start.getStructure() != structure) {
            return;
        }

        GENERATED.add(chunkPos);

        BlockPos origin = new BlockPos(chunkPos.getStartX(), 180, chunkPos.getStartZ());

        System.out.println("Celestia found at " + origin);

        CelestiaPlacer.place(world, origin);
    }
}