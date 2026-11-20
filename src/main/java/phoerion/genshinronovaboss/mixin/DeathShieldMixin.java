package phoerion.genshinronovaboss.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phoerion.genshinronovaboss.effects.ModEffects;

@Mixin(LivingEntity.class)
public abstract class DeathShieldMixin {

    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float reduceDamage(float amount) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!entity.hasStatusEffect(ModEffects.DEATH_SHIELD)) return amount;
        if (entity.getWorld().isClient) return amount;

        return amount / 3;
    }

    @Inject(method = "damage", at = @At("RETURN"))
    private void reflectDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.getWorld().isClient) return;
        if (!cir.getReturnValue()) return;
        if (!entity.hasStatusEffect(ModEffects.DEATH_SHIELD)) return;

        if (source.getAttacker() instanceof LivingEntity attacker) {
            attacker.damage(
                    entity.getDamageSources().thorns(entity),
                    amount / 2
            );
        }

        StatusEffectInstance effect = entity.getStatusEffect(ModEffects.DEATH_SHIELD);

        if (effect == null) return;

        int reflects = effect.getAmplifier();

        if (reflects <= 0) {
            entity.removeStatusEffect(ModEffects.DEATH_SHIELD);
        } else {
            entity.removeStatusEffect(ModEffects.DEATH_SHIELD);
            entity.addStatusEffect(new StatusEffectInstance(
                    ModEffects.DEATH_SHIELD,
                    effect.getDuration(),
                    reflects - 1,
                    effect.isAmbient(),
                    effect.shouldShowParticles(),
                    effect.shouldShowIcon()
            ));
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void spawnShieldParticles(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.getWorld().isClient) return;
        if (!entity.hasStatusEffect(ModEffects.DEATH_SHIELD)) return;

        if (!(entity.getWorld() instanceof ServerWorld world)) return;

        double radius = 1.5;
        int particlesCount = 3;

        for (int i = 0; i < particlesCount; i++) {
            double theta = entity.getRandom().nextDouble() * Math.PI * 2;
            double phi = Math.acos(entity.getRandom().nextDouble() * 2 - 1);

            double x = entity.getX() + radius * Math.sin(phi) * Math.cos(theta);
            double y = entity.getY() + entity.getHeight() / 2.0
                    + radius * Math.sin(phi) * Math.sin(theta);
            double z = entity.getZ() + radius * Math.cos(phi);

            world.spawnParticles(
                    ParticleTypes.DRAGON_BREATH,
                    x, y, z,
                    1,
                    0, 0, 0,
                    0
            );
        }
    }
}