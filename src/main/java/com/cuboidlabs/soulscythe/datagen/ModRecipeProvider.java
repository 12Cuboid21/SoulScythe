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
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SOUL_SCYTHE)
                .pattern(" NO")
                .pattern(" SN")
                .pattern("S  ")
                .input('S', Items.STICK)
                .input('O', Items.DIAMOND)
                .input('N', Items.IRON_INGOT)
                .criterion(hasItem(ModItems.PLAYER_SOUL), conditionsFromItem(ModItems.PLAYER_SOUL))
                .offerTo(recipeExporter);
    }
}
