package phoerion.genshinronovaboss.mixin;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemRenderer.class)
public interface ItemRendererAccessor {

    @Accessor(value = "models", remap = true)
    ItemModels genshinronovaboss$getModels();

    @Invoker("renderBakedItemModel")
    void genshinronovaboss$renderBakedItemModel(
            BakedModel model,
            ItemStack stack,
            int light,
            int overlay,
            MatrixStack matrixStack,
            VertexConsumer vertexConsumer
    );
}
