package com.cuboidlabs.soulscythe.datagen;

import com.cuboidlabs.soulscythe.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter recipeExporter) {
        /* Example
        List<ItemConvertible> ITEMS_WITH_THE_SAME_RESULT = List.of(ModItems.ITEM1, ModBlocks.BLOCK1, ModItems.ITEM2);
        offerSmelting(recipeExporter, ITEMS_WITH_THE_SAME_RESULT, RecipeCategory.MISC, ModItems.RESULT_ITEM, 0.25f, 200, "result_item");
        offerBlasting(recipeExporter, ITEMS_WITH_THE_SAME_RESULT, RecipeCategory.MISC, ModItems.RESULT_ITEM, 0.25f, 100, "result_item");
                                                       ModItems.LIGHT_RAY, *amount of items in result*)
                                                                              \/
        */
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.HAMMER_BLUEPRINT)
                .pattern("ini")
                .pattern("psp")
                .pattern("psp")
                .input('i', Items.IRON_BLOCK).input('n', Items.NETHERITE_INGOT).input('p', Items.PAPER).input('s', Items.STICK)
                .criterion(FabricRecipeProvider.hasItem(Items.NETHERITE_INGOT), FabricRecipeProvider.conditionsFromItem(Items.NETHERITE_INGOT))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.PRISMARITE_SHARD)
                .pattern("ada")
                .pattern("dgd")
                .pattern("ada")
                .input('a', Items.AMETHYST_SHARD).input('d', Items.DIAMOND).input('g', Items.GLASS)
                .criterion(FabricRecipeProvider.hasItem(Items.AMETHYST_SHARD), FabricRecipeProvider.conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(recipeExporter);
    }
}
