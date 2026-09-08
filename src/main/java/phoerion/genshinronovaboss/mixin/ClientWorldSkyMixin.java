package phoerion.genshinronovaboss.mixin;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phoerion.genshinronovaboss.client.ClientBossData;

@Mixin(ClientWorld.class)
public class ClientWorldSkyMixin {
    @Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
    private void changeRonovaSkyColor(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        if (ClientBossData.isRonovaFightActive) {
            // Кроваво-красный цвет (значения от 0.0 до 1.0)
            cir.setReturnValue(new Vec3d(0.6D, 0.1D, 0.15D));
        }
    }
}
