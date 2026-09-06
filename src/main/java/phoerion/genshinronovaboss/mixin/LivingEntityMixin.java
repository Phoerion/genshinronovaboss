package phoerion.genshinronovaboss.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phoerion.genshinronovaboss.effects.ModEffects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract boolean hasStatusEffect(net.minecraft.entity.effect.StatusEffect effect);

    @Inject(method = "tryUseTotem", at = @At("HEAD"), cancellable = true)
    private void cancelTotemWithCurse(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        // Если у сущности есть наш эффект проклятия, тотем не сработает
        if (entity.hasStatusEffect(ModEffects.RONOVA_CURSE)) {
            cir.setReturnValue(false);
        }
    }
}
