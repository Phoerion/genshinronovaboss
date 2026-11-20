package phoerion.genshinronovaboss.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import phoerion.genshinronovaboss.GenshinRonovaBoss;
import phoerion.genshinronovaboss.item.ModItems;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @ModifyVariable(method = "renderItem", at = @At(value = "HEAD"), argsOnly = true)
    public BakedModel useRadiationStaffModel(BakedModel value, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.isOf(ModItems.RULER_OF_DEATH_SWORD) && renderMode != ModelTransformationMode.GUI && renderMode != ModelTransformationMode.FIRST_PERSON_LEFT_HAND&& renderMode != ModelTransformationMode.FIRST_PERSON_RIGHT_HAND) {
            return ((ItemRendererAccessor) this).genshinronovaboss$getModels().getModelManager().getModel(new ModelIdentifier(GenshinRonovaBoss.MOD_ID, "ruler_of_death_sword_3d", "inventory"));
        }
        return value;
    }
}
