package phoerion.genshinronovaboss.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phoerion.genshinronovaboss.client.ClientBossData;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    private static final Identifier EYE_TEXTURE = new Identifier("genshinronovaboss", "textures/environment/ronova_eye.png");

    @Inject(
            method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V",
            at = @At("TAIL")
    )
    private void renderRonovaEyes(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable skyDarknessRunnable, CallbackInfo ci) {
        if (!ClientBossData.isRonovaFightActive) return;

        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, EYE_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        matrices.push();
        matrices.translate(0.0f, 100.0f, 0.0f);

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float size = 120.0f;
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(matrix, -size, 0, -120.0f).texture(0.0f, 0.0f).next();
        buffer.vertex(matrix,  size, 0, -120.0f).texture(1.0f, 0.0f).next();
        buffer.vertex(matrix,  size, size * 2, -120.0f).texture(1.0f, 1.0f).next();
        buffer.vertex(matrix, -size, size * 2, -120.0f).texture(0.0f, 1.0f).next();
        tessellator.draw();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        matrices.pop();
    }

    @Inject(
            method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FDDD)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cancelCloudsRender(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        if (ClientBossData.isRonovaFightActive) {
            ci.cancel();
        }
    }
}