package phoerion.genshinronovaboss.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phoerion.genshinronovaboss.client.ClientBossData;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    @Shadow private static float red;
    @Shadow private static float green;
    @Shadow private static float blue;

    @Inject(method = "render", at = @At("RETURN"))
    private static void changeSkyColor(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness, CallbackInfo ci) {
        if (ClientBossData.isRonovaFightActive) {
            red = 0.8F;
            green = 0.0F;
            blue = 0.1F;
        }
    }

    @Inject(method = "applyFog", at = @At("TAIL"))
    private static void applyCustomFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        if (ClientBossData.isRonovaFightActive) {
            // Устанавливаем багровый/красный цвет тумана
            RenderSystem.setShaderFogColor(0.6f, 0.05f, 0.05f, 1.0f);

            // Регулируем дальность тумана (делаем его ближе к игроку)
            // Start — расстояние, с которого начинается туман
            // End — расстояние полной видимости (чем меньше числа, тем плотнее туман)
            RenderSystem.setShaderFogStart(32.0f);
            RenderSystem.setShaderFogEnd(64.0f);
        }
    }
}
