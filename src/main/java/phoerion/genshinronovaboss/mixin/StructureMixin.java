package phoerion.genshinronovaboss.mixin;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import phoerion.genshinronovaboss.worldgen.structures.CelestiaPlacer;

import java.util.HashSet;
import java.util.Set;

@Mixin(Structure.class)
public class StructureMixin {

    private static final Set<String> PLACED = new HashSet<>();

    @Inject(method = "postPlace", at = @At("TAIL"))

    private void genshinronovaboss$afterCelestia(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, StructurePiecesList pieces, CallbackInfo ci) {

        ServerWorld serverWorld = world.toServerWorld();

        Registry<Structure> registry = serverWorld.getRegistryManager().get(RegistryKeys.STRUCTURE);

        Identifier id = registry.getId((Structure) (Object) this);

        if (id == null || !id.equals(new Identifier("genshinronovaboss", "celestia"))) {
            return;
        }

        for (StructureStart start : structureAccessor.getStructureStarts(chunkPos, structure -> structure == (Structure) (Object) this)) {

            if (!start.hasChildren()) {
                continue;
            }

            ChunkPos startPos = start.getPos();

            String key = serverWorld.getRegistryKey().getValue() + ":" + startPos.x + ":" + startPos.z;

            if (!PLACED.add(key)) {
                continue;
            }

            BlockPos origin = new BlockPos(startPos.getStartX(), 255, startPos.getStartZ());

            serverWorld.getServer().execute(() -> {
                CelestiaPlacer.place(serverWorld, origin);
            });
        }
    }
}