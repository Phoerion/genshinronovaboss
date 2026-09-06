package phoerion.genshinronovaboss.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.util.Identifier;
import phoerion.genshinronovaboss.item.ModItems;

import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.STELLAR_VISION, Models.GENERATED);
        itemModelGenerator.register(ModItems.ICE_SWORD, Models.GENERATED);
        itemModelGenerator.register(ModItems.RONOVA_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
    }
}
