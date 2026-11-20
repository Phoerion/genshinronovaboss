package phoerion.genshinronovaboss.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import phoerion.genshinronovaboss.effects.ModEffects;

import java.util.List;

public class RulerOfDeathSwordItem extends SwordItem {

    private static final int SHIELD_MAX_COOLDOWN = 800;

    public RulerOfDeathSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient &&
                attacker.getWorld().getRandom().nextFloat() <= 0.2f) {

            target.addStatusEffect(
                    new StatusEffectInstance(ModEffects.RONOVA_CURSE, 400)
            );
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        ItemStack stack = user.getStackInHand(hand);

        if (user.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.fail(stack);
        }

        if (!world.isClient) {

            user.addStatusEffect(
                    new StatusEffectInstance(ModEffects.DEATH_SHIELD, 400, 4)
            );

            user.getItemCooldownManager().set(this, SHIELD_MAX_COOLDOWN);
        }

        return TypedActionResult.success(stack, world.isClient);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.genshinronovaboss.ruler_of_death_sword.tooltip"));
        tooltip.add(Text.translatable("item.genshinronovaboss.ruler_of_death_sword.tooltip.1"));

        super.appendTooltip(stack, world, tooltip, context);
    }
}