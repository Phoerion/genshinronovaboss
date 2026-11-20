package phoerion.genshinronovaboss.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;
import phoerion.genshinronovaboss.block.ModBlocks;
import phoerion.genshinronovaboss.item.ModItems;

import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_TIN_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.TIN_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.TIN_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RAW_TIN_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.BRONZE_BLOCK);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.STELLAR_VISION, Models.GENERATED);
        itemModelGenerator.register(ModItems.ICE_SWORD, Models.GENERATED);
        itemModelGenerator.register(ModItems.RONOVA_HEART, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_TIN, Models.GENERATED);
        itemModelGenerator.register(ModItems.TIN_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.BRONZE_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.BRONZE_NUGGET, Models.GENERATED);
        itemModelGenerator.register(ModItems.TIN_NUGGET, Models.GENERATED);
        itemModelGenerator.register(ModItems.DEATH_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.RONOVA_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.registerArmor((ArmorItem) ModItems.RULER_OF_DEATH_BOOTS);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.RULER_OF_DEATH_LEGGINGS);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.RULER_OF_DEATH_CHESTPLATE);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.RULER_OF_DEATH_HELMET);
        itemModelGenerator.register(ModItems.RONOVA_SHARD, Models.GENERATED);
        itemModelGenerator.register(ModItems.JOB_APPLICATION, Models.GENERATED);
    }
}
