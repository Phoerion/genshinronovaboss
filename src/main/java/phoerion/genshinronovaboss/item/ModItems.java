package phoerion.genshinronovaboss.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import phoerion.genshinronovaboss.GenshinRonovaBoss;
import phoerion.genshinronovaboss.entity.ModEntities;
import phoerion.genshinronovaboss.item.custom.*;

public class ModItems {
    public static final Item STELLAR_VISION = registerItem("stellar_vision", new Item(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item ICE_SWORD = registerItem("ice_sword", new IceSwordItem(ToolMaterials.NETHERITE, 10, 1f, new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item RONOVA_SPAWN_EGG = registerItem("ronova_spawn_egg", new SpawnEggItem(ModEntities.RONOVA_BOSS, 0x2e0300, 0xeb0f00, new FabricItemSettings()));
    public static final Item RONOVA_HEART = registerItem("ronova_heart", new RonovaHeartItem(new FabricItemSettings().food(ModFoodComponents.RONOVA_HEART).rarity(Rarity.EPIC)));
    public static final Item RAW_TIN = registerItem("raw_tin", new Item(new FabricItemSettings()));
    public static final Item TIN_INGOT = registerItem("tin_ingot", new Item(new FabricItemSettings()));
    public static final Item BRONZE_INGOT = registerItem("bronze_ingot", new Item(new FabricItemSettings()));
    public static final Item BRONZE_NUGGET = registerItem("bronze_nugget", new Item(new FabricItemSettings()));
    public static final Item TIN_NUGGET = registerItem("tin_nugget", new Item(new FabricItemSettings()));
    public static final Item DEATH_INGOT = registerItem("death_ingot", new Item(new FabricItemSettings().rarity(Rarity.EPIC)));
    public static final Item RONOVA_SHARD = registerItem("ronova_shard", new Item(new FabricItemSettings().rarity(Rarity.EPIC)));
    public static final Item JOB_APPLICATION = registerItem("job_application", new JobApplication(new FabricItemSettings().rarity(Rarity.EPIC)));

    public static final Item RULER_OF_DEATH_SWORD = registerItem("ruler_of_death_sword", new RulerOfDeathSwordItem(ModItemMaterials.RULER_OF_DEATH, 3, -2.4f, new FabricItemSettings().rarity(Rarity.EPIC)));

    public static final Item RULER_OF_DEATH_HELMET = registerItem("ruler_of_death_helmet", new ModArmorItem(ModArmorMaterials.RULER_OF_DEATH, ArmorItem.Type.HELMET, new FabricItemSettings().rarity(Rarity.EPIC)));
    public static final Item RULER_OF_DEATH_CHESTPLATE = registerItem("ruler_of_death_chestplate", new ModArmorItem(ModArmorMaterials.RULER_OF_DEATH, ArmorItem.Type.CHESTPLATE, new FabricItemSettings().rarity(Rarity.EPIC)));
    public static final Item RULER_OF_DEATH_LEGGINGS = registerItem("ruler_of_death_leggings", new ModArmorItem(ModArmorMaterials.RULER_OF_DEATH, ArmorItem.Type.LEGGINGS, new FabricItemSettings().rarity(Rarity.EPIC)));
    public static final Item RULER_OF_DEATH_BOOTS = registerItem("ruler_of_death_boots", new ModArmorItem(ModArmorMaterials.RULER_OF_DEATH, ArmorItem.Type.BOOTS, new FabricItemSettings().rarity(Rarity.EPIC)));

    public static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(GenshinRonovaBoss.MOD_ID, name), item);
    }

    public static void registerItems(){
        GenshinRonovaBoss.LOGGER.info("Registering Items For " + GenshinRonovaBoss.MOD_ID);
    }
}
