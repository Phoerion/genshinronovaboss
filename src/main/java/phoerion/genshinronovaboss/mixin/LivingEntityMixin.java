package phoerion.genshinronovaboss.mixin;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phoerion.genshinronovaboss.effects.ModEffects;
import phoerion.genshinronovaboss.particle.ModParticles;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public abstract boolean hasStatusEffect(net.minecraft.entity.effect.StatusEffect effect);

    @Inject(method = "tryUseTotem", at = @At("HEAD"), cancellable = true)
    private void cancelTotemWithCurse(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.hasStatusEffect(ModEffects.RONOVA_CURSE)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "damage", at = @At("RETURN"))
    private void onDamaged(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {

        if (!(source.getAttacker() instanceof ServerPlayerEntity player)) return;

        if (!cir.getReturnValue()) return;

        LivingEntity target = (LivingEntity) (Object) this;

//        if (player.hasStatusEffect(ModEffects.RULER_OF_DEATH) && target.getHealth() / target.getMaxHealth() <= 0.2) {
//            target.damage(
//                    player.getDamageSources().playerAttack(player),
//                    10000
//            );
//        }

//        if(player.hasStatusEffect(ModEffects.RULER_OF_DEATH)){
//            target.addStatusEffect(new StatusEffectInstance(ModEffects.RONOVA_CURSE, 400));
//        }

        if(player.getCommandTags().contains("canCurse") && player.getCommandTags().contains("ronova")) {
            target.addStatusEffect(new StatusEffectInstance(ModEffects.RONOVA_CURSE, 200));
        }

//            Random pos = target.getWorld().getRandom();
//
//            for(int i = 1; i <= 30; i++) {
//                if (target.getWorld() instanceof ServerWorld world) {
//                    world.spawnParticles(ModParticles.CURSE_PARTICLE,
//                            target.getX(), target.getY(), target.getZ(),
//                            1,
//                            0.5 -pos.nextFloat(), pos.nextBetween(0, 2), 0.5 - pos.nextFloat(),
//                            0);
//                }
//            }
        }

}





