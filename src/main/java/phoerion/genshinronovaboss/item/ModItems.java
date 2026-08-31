package phoerion.genshinronovaboss.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import phoerion.genshinronovaboss.GenshinRonovaBoss;
import phoerion.genshinronovaboss.item.custom.IceSwordItem;

public class ModItems {
    public static final Item STELLAR_VISION = registerItem("stellar_vision", new Item(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item ICE_SWORD = registerItem("ice_sword", new IceSwordItem(ToolMaterials.NETHERITE, 20, 20f, new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));


    public static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(GenshinRonovaBoss.MOD_ID, name), item);
    }

    public static void registerItems(){
        GenshinRonovaBoss.LOGGER.info("Registering Items For " + GenshinRonovaBoss.MOD_ID);
    }
}
