package com.cuboidlabs.soulscythe.block.custom;

import com.cuboidlabs.soulscythe.SoulScythe;
import com.cuboidlabs.soulscythe.component.ModDataComponentTypes;
import com.cuboidlabs.soulscythe.effect.ModEffects;
import com.cuboidlabs.soulscythe.item.ModItems;
import com.cuboidlabs.soulscythe.item.custom.LifeGemItem;
import com.cuboidlabs.soulscythe.item.custom.PlayerSoulItem;
import com.cuboidlabs.soulscythe.util.GhostEffectHandler;
import com.cuboidlabs.soulscythe.util.PersistentModData;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.*;

public class LightForgeBlock extends Block {
    public LightForgeBlock(Settings settings) {
        super(settings);
    }
    private Set<BlockPos> bookAquired = new HashSet<>();

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world instanceof ServerWorld serverWorld) {
            ItemStack handStack = player.getMainHandStack();
            PersistentModData modDataState = PersistentModData.get(serverWorld);
            if (handStack.getItem() instanceof LifeGemItem) {
                if (!modDataState.scytheCrafted) {
                    player.sendMessage(Text.of("There is nothing to defend against YET."));
                    return ActionResult.success(true);
                }
                if (modDataState.greatswordCrafted) {
                    player.sendMessage(Text.of("The Greatsword has already been crafted."));
                    return ActionResult.success(true);
                }
                handStack.decrement(1);
                player.getInventory().insertStack(new ItemStack(ModItems.LIGHTFORGED_GREATSWORD));
                player.sendMessage(Text.of("The Greatsword has been crafted."));
                LightningEntity bolt = new LightningEntity(EntityType.LIGHTNING_BOLT,serverWorld);
                bolt.setPos(pos.getX(),pos.getY(),pos.getZ());
                bolt.setCosmetic(true);
                serverWorld.spawnEntity(bolt);
                modDataState.greatswordCrafted = true;
                modDataState.markDirty();
                return ActionResult.success(true);
            } else if (player.getMainHandStack().isEmpty() && !bookAquired.contains(pos)) {
                List<RawFilteredPair<Text>> pages = List.of(
                        RawFilteredPair.of(Text.literal(
                                "There once was weapon, it was very powerful.\n\n" +
                                        "The holder was able to call the Zeus's Lighting."
                        )),
                        RawFilteredPair.of(Text.literal(
                                "He had to get a gem, from a very hard dungeon,\n" +
                                        "but that didn't scare him."
                        )),
                        RawFilteredPair.of(Text.literal(
                                "ouSl railTs"
                        ))
                );
                WrittenBookContentComponent bookContent =
                        new WrittenBookContentComponent(
                                RawFilteredPair.of("The Legend Of The Greatsword"), // title
                                "???",            // author
                                0,                                         // generation
                                pages,
                                false                                      // resolved
                        );

                ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
                book.set(
                        DataComponentTypes.WRITTEN_BOOK_CONTENT,
                        bookContent
                );
                player.getInventory().insertStack(book);
                player.sendMessage(Text.of("You found a seemingly old book"), true);
                bookAquired.add(pos);
                return ActionResult.SUCCESS;
            } else {
                player.sendMessage(Text.of("You are missing the gem in the heart."));
                return ActionResult.success(true);
            }
        }
        return ActionResult.FAIL;
    }
}
