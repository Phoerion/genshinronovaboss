package phoerion.genshinronovaboss.mixin;

import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phoerion.genshinronovaboss.client.ClientBossData;

@Mixin(DimensionEffects.Overworld.class)
public class OverworldDimensionEffectsMixin {
    @Inject(method = "adjustFogColor", at = @At("RETURN"), cancellable = true)
    private void changeFogColor(Vec3d color, float sunHeight, CallbackInfoReturnable<Vec3d> cir) {
        if (ClientBossData.isRonovaFightActive) {
            cir.setReturnValue(new Vec3d(0.35D, 0.01D, 0.02D));
        }
    }
}