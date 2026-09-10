package phoerion.genshinronovaboss.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RonovaHeartItem extends Item {
    public RonovaHeartItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        user.addCommandTag("ronova");
        return super.finishUsing(stack, world, user);
    }
}
