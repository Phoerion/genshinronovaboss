package phoerion.genshinronovaboss.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import phoerion.genshinronovaboss.GenshinRonovaBoss;
import phoerion.genshinronovaboss.entity.ModEntities;
import phoerion.genshinronovaboss.item.custom.IceSwordItem;
import phoerion.genshinronovaboss.item.custom.RonovaHeartItem;

public class ModItems {
    public static final Item STELLAR_VISION = registerItem("stellar_vision", new Item(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item ICE_SWORD = registerItem("ice_sword", new IceSwordItem(ToolMaterials.NETHERITE, 10, 1f, new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item RONOVA_SPAWN_EGG = registerItem("ronova_spawn_egg", new SpawnEggItem(ModEntities.RONOVA_BOSS, 0x2e0300, 0xeb0f00, new FabricItemSettings()));
    public static final Item RONOVA_HEART = registerItem("ronova_heart", new RonovaHeartItem(new FabricItemSettings().food(ModFoodComponents.RONOVA_HEART)));


    public static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(GenshinRonovaBoss.MOD_ID, name), item);
    }

    public static void registerItems(){
        GenshinRonovaBoss.LOGGER.info("Registering Items For " + GenshinRonovaBoss.MOD_ID);
    }
}
