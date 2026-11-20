package phoerion.genshinronovaboss.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import phoerion.genshinronovaboss.effects.ModEffects;
import phoerion.genshinronovaboss.item.ModItems;

public class ModArmorItem extends ArmorItem {
    public ModArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof ServerPlayerEntity player)){
            return;
        }
        if (hasFullArmor(player)) {
            player.addStatusEffect(new StatusEffectInstance(
                    ModEffects.RULER_OF_DEATH,
                    40,
                    0,
                    true,
                    false,
                    true
                    ));
                }
        };


        private boolean hasFullArmor(ServerPlayerEntity player) {
            return player.getInventory().getArmorStack(3).isOf(ModItems.RULER_OF_DEATH_HELMET)
                    && player.getInventory().getArmorStack(2).isOf(ModItems.RULER_OF_DEATH_CHESTPLATE)
                    && player.getInventory().getArmorStack(1).isOf(ModItems.RULER_OF_DEATH_LEGGINGS)
                    && player.getInventory().getArmorStack(0).isOf(ModItems.RULER_OF_DEATH_BOOTS);
        }

}
