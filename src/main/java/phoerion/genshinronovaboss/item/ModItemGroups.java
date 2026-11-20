package phoerion.genshinronovaboss.item;

import net.fabricmc.fabric.impl.itemgroup.FabricItemGroupBuilderImpl;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import phoerion.genshinronovaboss.GenshinRonovaBoss;
import phoerion.genshinronovaboss.block.ModBlocks;

public class ModItemGroups {
    public static final ItemGroup RONOVA_BOSS_ITEM_GROUP = Registry.register(Registries.ITEM_GROUP, new Identifier(GenshinRonovaBoss.MOD_ID, "grb_item_group"), new FabricItemGroupBuilderImpl().displayName(Text.translatable("itemgroup.grb")).icon(() -> new ItemStack(ModItems.STELLAR_VISION)).entries((displayContext, entries) -> {
        //entries.add(ModItems.STELLAR_VISION);
        entries.add(ModItems.RONOVA_SPAWN_EGG);
        entries.add(ModItems.RONOVA_SHARD);
        entries.add(ModItems.RONOVA_HEART);
        entries.add(ModBlocks.TIN_ORE.asItem());
        entries.add(ModBlocks.DEEPSLATE_TIN_ORE.asItem());
        entries.add(ModItems.RAW_TIN);
        entries.add(ModItems.TIN_INGOT);
        entries.add(ModItems.BRONZE_INGOT);
        entries.add(ModItems.BRONZE_NUGGET);
        entries.add(ModItems.TIN_NUGGET);
        entries.add(ModBlocks.RAW_TIN_BLOCK.asItem());
        entries.add(ModBlocks.TIN_BLOCK.asItem());
        entries.add(ModBlocks.BRONZE_BLOCK.asItem());
        entries.add(ModItems.DEATH_INGOT);
        entries.add(ModItems.RULER_OF_DEATH_BOOTS);
        entries.add(ModItems.RULER_OF_DEATH_LEGGINGS);
        entries.add(ModItems.RULER_OF_DEATH_CHESTPLATE);
        entries.add(ModItems.RULER_OF_DEATH_HELMET);
        entries.add(ModItems.RULER_OF_DEATH_SWORD);
        entries.add(ModItems.JOB_APPLICATION);
    }).build());

    public static void registerItemGroups(){
        GenshinRonovaBoss.LOGGER.info("Registering Item Groups" + GenshinRonovaBoss.MOD_ID);
    }
}
