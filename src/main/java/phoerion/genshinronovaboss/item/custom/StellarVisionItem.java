package phoerion.genshinronovaboss.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import phoerion.genshinronovaboss.item.ModItems;

public class StellarVisionItem extends Item {
    public StellarVisionItem(Settings settings) {
        super(settings);
    }

    public static void giveIceSword(World world, PlayerEntity player){
        player.giveItemStack(new ItemStack(ModItems.ICE_SWORD, 1));
    }


}
