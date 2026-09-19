package phoerion.genshinronovaboss.worldgen.structures;

import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import phoerion.genshinronovaboss.entity.ModEntities;
import phoerion.genshinronovaboss.entity.custom.RonovaEntity;

import java.util.Optional;

public class CelestiaPlacer {

    private static final String MOD_ID = "genshinronovaboss";

    private static final BlockPos[] OFFSETS = {
            new BlockPos(0, 0, 0),       // celestia_1
            new BlockPos(48, 0, 0),       // celestia_2
            new BlockPos(0, 0, 48),       // celestia_3
            new BlockPos(48, 0, 48),      // celestia_4
            new BlockPos(96, 0, 22),      // celestia_5
            new BlockPos(25, 48, 24)      // celestia_6
    };

    public static void place(ServerWorld world, BlockPos origin) {
        BlockPos ronovaPos = origin.add(58, 37, 48);

        System.out.println("[Celestia] Trying to spawn Ronova at: " + ronovaPos);

        RonovaEntity ronova = ModEntities.RONOVA_BOSS.spawn(world, ronovaPos, SpawnReason.COMMAND);

        if (ronova != null) {
            ronova.setPersistent();
            System.out.println("RONOVA SPAWNED: " + ronova.getBlockPos());
        } else {
            System.out.println("RONOVA SPAWN FAILED!");
        }

        StructureTemplateManager manager = world.getStructureTemplateManager();

        Random random = Random.create();

        for (BlockPos offset : OFFSETS) {

            BlockPos position = origin.add(offset);

            world.getChunk(position.getX() >> 4, position.getZ() >> 4);
        }

        for (int i = 0; i < 6; i++) {

            Identifier id = new Identifier(MOD_ID, "celestia_" + (i + 1));

            Optional<StructureTemplate> optional = manager.getTemplate(id);

            if (optional.isEmpty()) {
                System.out.println("[Ronova] Structure not found: " + id);
                continue;
            }

            StructureTemplate template = optional.get();

            BlockPos position = origin.add(OFFSETS[i]);

            StructurePlacementData placementData = new StructurePlacementData();

            boolean success = template.place(
                    world,
                    position,
                    position,
                    placementData,
                    random,
                    2
            );
        }

    }
}