package phoerion.genshinronovaboss.mixin.not_used;

import net.minecraft.client.render.DimensionEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phoerion.genshinronovaboss.client.RonovaSkyRenderer;

@Mixin(DimensionEffects.class)
public abstract class DimensionEffectsMixin {

    // Создаем экземпляр нашего рендерера один раз
    private final RonovaSkyRenderer ronovaRenderer = new RonovaSkyRenderer();

    @Inject(method = "getSkyRenderer", at = @At("RETURN"), cancellable = true)
    private void getRonovaSkyRenderer(CallbackInfoReturnable<RonovaSkyRenderer> cir) {
        // Если это экземпляр DimensionEffects для оверворлда (проверяем через context)
        // Самый простой способ: если мы в оверворлде и бой активен, возвращаем наш рендерер

        // Здесь нужна проверка на текущее измерение. В клиенте это можно получить через MinecraftClient.
        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.world != null && client.world.getRegistryKey() == net.minecraft.world.World.OVERWORLD) {
            if (phoerion.genshinronovaboss.client.ClientBossData.isRonovaFightActive) {
                // Подменяем стандартный рендерер на наш
                cir.setReturnValue(ronovaRenderer);
            }
        }
    }
}
