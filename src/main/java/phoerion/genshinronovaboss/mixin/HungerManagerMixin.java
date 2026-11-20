package phoerion.genshinronovaboss.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.HungerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phoerion.genshinronovaboss.effects.ModEffects;

@Mixin(HungerManager.class)
public class HungerManagerMixin {

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void cancelNaturalRegen(PlayerEntity player, CallbackInfo ci) {
        if (player.hasStatusEffect(ModEffects.RONOVA_CURSE)) {
            ci.cancel();
        }
    }
}