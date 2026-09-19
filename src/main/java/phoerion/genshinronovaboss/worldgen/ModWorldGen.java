package phoerion.genshinronovaboss.worldgen;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.StructureType;
import phoerion.genshinronovaboss.GenshinRonovaBoss;

public class ModWorldGen {

    public static final RegistryKey<StructureSet> CELESTIA = RegistryKey.of(
            RegistryKeys.STRUCTURE_SET, new Identifier(GenshinRonovaBoss.MOD_ID, "celestia")
    );

    public static final StructureType<JigsawStructure> CELESTIA_TYPE =
            Registry.register(
                    Registries.STRUCTURE_TYPE,
                    new Identifier(GenshinRonovaBoss.MOD_ID, "celestia"),
                    () -> JigsawStructure.CODEC
            );

    public static void register(){

    }

}
