package phoerion.genshinronovaboss.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class RonovaSkyRenderer implements net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry.SkyRenderer {
    private static final Identifier EYE_TEXTURE = new Identifier("genshinronovaboss", "environment/ronova_eye.png");

    @Override
    public void render(WorldRenderContext context) {
        if (!ClientBossData.isRonovaFightActive) return;

        MatrixStack matrices = context.matrixStack();
        Camera camera = context.camera();

        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, EYE_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        matrices.push();
        matrices.multiply(camera.getRotation());
        Matrix4f modelViewMatrix = matrices.peek().getPositionMatrix();

        // Рисуем каждый глаз раздельно (теперь begin и draw находятся внутри drawEye)
        drawEye(modelViewMatrix, 0, 80, -30);
        drawEye(modelViewMatrix, -50, 40, -50);
        drawEye(modelViewMatrix, 50, 40, -50);

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        matrices.pop();
    }

    private void drawEye(Matrix4f matrix, float x, float y, float z) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float size = 30.0f;

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(matrix, x - size, y - size, z).texture(0.0f, 0.0f).next();
        buffer.vertex(matrix, x + size, y - size, z).texture(1.0f, 0.0f).next();
        buffer.vertex(matrix, x + size, y + size, z).texture(1.0f, 1.0f).next();
        buffer.vertex(matrix, x - size, y + size, z).texture(0.0f, 1.0f).next();
        tessellator.draw();
    }
}