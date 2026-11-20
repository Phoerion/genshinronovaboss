package phoerion.genshinronovaboss.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import phoerion.genshinronovaboss.GenshinRonovaBoss;
import phoerion.genshinronovaboss.block.ModBlocks;
import phoerion.genshinronovaboss.item.ModItems;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        offerSmelting(exporter, List.of(ModItems.RAW_TIN, ModBlocks.TIN_ORE, ModBlocks.DEEPSLATE_TIN_ORE), RecipeCategory.MISC, ModItems.TIN_INGOT, 0.25f, 60, "raw_tin");
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.BRONZE_INGOT, 4)
                .input(ModItems.TIN_INGOT, 5)
                .input(Items.COPPER_INGOT, 4)
                .criterion(hasItem(ModItems.TIN_INGOT), conditionsFromItem(ModItems.TIN_INGOT))
                .offerTo(exporter, Identifier.of(GenshinRonovaBoss.MOD_ID, "bronze_from_tin_and_copper"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.RAW_TIN_BLOCK.asItem())
                .pattern("RRR")
                .pattern("RRR")
                .pattern("RRR")
                .input('R', ModItems.RAW_TIN)
                .criterion(hasItem(ModItems.RAW_TIN), conditionsFromItem(ModItems.RAW_TIN))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TIN_BLOCK.asItem())
                .pattern("TTT")
                .pattern("TTT")
                .pattern("TTT")
                .input('T', ModItems.TIN_INGOT)
                .criterion(hasItem(ModItems.TIN_INGOT), conditionsFromItem(ModItems.TIN_INGOT))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.BRONZE_BLOCK.asItem())
                .pattern("TTT")
                .pattern("TTT")
                .pattern("TTT")
                .input('T', ModItems.BRONZE_INGOT)
                .criterion(hasItem(ModItems.BRONZE_INGOT), conditionsFromItem(ModItems.BRONZE_INGOT))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.BRONZE_INGOT)
                .pattern("TTT")
                .pattern("TTT")
                .pattern("TTT")
                .input('T', ModItems.BRONZE_NUGGET)
                .criterion(hasItem(ModItems.BRONZE_NUGGET), conditionsFromItem(ModItems.BRONZE_NUGGET))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.TIN_INGOT)
                .pattern("TTT")
                .pattern("TTT")
                .pattern("TTT")
                .input('T', ModItems.TIN_NUGGET)
                .criterion(hasItem(ModItems.TIN_NUGGET), conditionsFromItem(ModItems.TIN_NUGGET))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.DEATH_INGOT, 4)
                .pattern("BBB")
                .pattern("BHB")
                .pattern("BBB")
                .input('B', ModItems.BRONZE_INGOT)
                .input('H', ModItems.RONOVA_SHARD)
                .criterion(hasItem(ModItems.RONOVA_SHARD), conditionsFromItem(ModItems.RONOVA_SHARD))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RONOVA_HEART, 8)
                .pattern("SSS")
                .pattern("SSS")
                .pattern("SSS")
                .input('S', ModItems.RONOVA_SHARD)
                .criterion(hasItem(ModItems.RONOVA_SHARD), conditionsFromItem(ModItems.RONOVA_SHARD))
                .offerTo(exporter);

        offerShapelessRecipe(exporter, ModItems.TIN_NUGGET, ModItems.TIN_INGOT, "tin_nugget_from_ingot", 9);
        offerShapelessRecipe(exporter, ModItems.BRONZE_NUGGET, ModItems.BRONZE_INGOT, "bronze_nugget_from_ingot", 9);
        offerShapelessRecipe(exporter, ModItems.BRONZE_INGOT, ModBlocks.BRONZE_BLOCK.asItem(), "bronze_ingot_from_block", 9);
        offerShapelessRecipe(exporter, ModItems.TIN_INGOT, ModBlocks.TIN_BLOCK.asItem(), "tin_ingot_from_block", 9);
        offerShapelessRecipe(exporter, ModItems.RAW_TIN, ModBlocks.RAW_TIN_BLOCK.asItem(), "raw_tin_from_block", 9);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RULER_OF_DEATH_BOOTS, 1)
                .pattern("D D")
                .pattern("D D")
                .input('D', ModItems.DEATH_INGOT)
                .criterion(hasItem(ModItems.DEATH_INGOT), conditionsFromItem(ModItems.DEATH_INGOT))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RULER_OF_DEATH_LEGGINGS, 1)
                .pattern("DDD")
                .pattern("D D")
                .pattern("D D")
                .input('D', ModItems.DEATH_INGOT)
                .criterion(hasItem(ModItems.DEATH_INGOT), conditionsFromItem(ModItems.DEATH_INGOT))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RULER_OF_DEATH_CHESTPLATE, 1)
                .pattern("D D")
                .pattern("DDD")
                .pattern("DDD")
                .input('D', ModItems.DEATH_INGOT)
                .criterion(hasItem(ModItems.DEATH_INGOT), conditionsFromItem(ModItems.DEATH_INGOT))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RULER_OF_DEATH_HELMET, 1)
                .pattern("DDD")
                .pattern("D D")
                .input('D', ModItems.DEATH_INGOT)
                .criterion(hasItem(ModItems.DEATH_INGOT), conditionsFromItem(ModItems.DEATH_INGOT))
                .offerTo(exporter);
    }
}
