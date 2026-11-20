package phoerion.genshinronovaboss.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.EntityTypePredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import phoerion.genshinronovaboss.GenshinRonovaBoss;
import phoerion.genshinronovaboss.effects.ModEffects;
import phoerion.genshinronovaboss.entity.ModEntities;
import phoerion.genshinronovaboss.entity.custom.RonovaEntity;
import phoerion.genshinronovaboss.item.ModItems;

import java.util.function.Consumer;

public class ModAdvancementProvider extends FabricAdvancementProvider {
    public ModAdvancementProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {
        Advancement.Builder.create()
                .display(ModItems.TIN_INGOT, Text.literal("First Tin"), Text.literal("Try mixin it mit copper"),
                        null, AdvancementFrame.TASK, true, true, false)
                .criterion("get_tin",
                        InventoryChangedCriterion.Conditions.items(ModItems.TIN_INGOT)).build(consumer, "genshinronovaboss:get_tin");

    }
}
