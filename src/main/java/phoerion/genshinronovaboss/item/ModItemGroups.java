package phoerion.genshinronovaboss.item;

import net.fabricmc.fabric.impl.itemgroup.FabricItemGroupBuilderImpl;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import phoerion.genshinronovaboss.GenshinRonovaBoss;

public class ModItemGroups {
    public static final ItemGroup RONOVA_BOSS_ITEM_GROUP = Registry.register(Registries.ITEM_GROUP, new Identifier(GenshinRonovaBoss.MOD_ID, "grb_item_group"), new FabricItemGroupBuilderImpl().displayName(Text.translatable("itemgroup.grb")).icon(() -> new ItemStack(ModItems.STELLAR_VISION)).entries((displayContext, entries) -> {
        entries.add(ModItems.STELLAR_VISION);
        entries.add(ModItems.RONOVA_SPAWN_EGG);
    }).build());

    public static void registerItemGroups(){
        GenshinRonovaBoss.LOGGER.info("Registering Item Groups" + GenshinRonovaBoss.MOD_ID);

    }
}
