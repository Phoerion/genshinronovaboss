package phoerion.genshinronovaboss.mixin.not_used;

import net.minecraft.client.render.DimensionEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phoerion.genshinronovaboss.client.RonovaSkyRenderer;

@Mixin(DimensionEffects.class)
public abstract class DimensionEffectsMixin {

    private final RonovaSkyRenderer ronovaRenderer = new RonovaSkyRenderer();

    @Inject(method = "getSkyRenderer", at = @At("RETURN"), cancellable = true)
    private void getRonovaSkyRenderer(CallbackInfoReturnable<RonovaSkyRenderer> cir) {
        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.world != null && client.world.getRegistryKey() == net.minecraft.world.World.OVERWORLD) {
            if (phoerion.genshinronovaboss.client.ClientBossData.isRonovaFightActive) {
                cir.setReturnValue(ronovaRenderer);
            }
        }
    }
}
