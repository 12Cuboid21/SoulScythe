package com.cuboidlabs.soulscythe.block.custom;

import com.cuboidlabs.soulscythe.item.ModItems;
import com.cuboidlabs.soulscythe.item.custom.LifeGemItem;
import com.cuboidlabs.soulscythe.item.custom.PrismariteShardItem;
import com.cuboidlabs.soulscythe.util.PersistentModData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrismaHeartBlock extends Block {
    public PrismaHeartBlock(Settings settings) {
        super(settings);
    }
    private Set<BlockPos> bookAquired = new HashSet<>();

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world instanceof ServerWorld serverWorld) {
            ItemStack handStack = player.getMainHandStack();
            PersistentModData modDataState = PersistentModData.get(serverWorld);
            if (handStack.getItem() instanceof PrismariteShardItem) {
                if (modDataState.piercerCrafted) {
                    player.sendMessage(Text.of("The Prismapiercer has already been crafted."));
                    return ActionResult.success(true);
                }
                handStack.decrement(1);
                player.getInventory().insertStack(new ItemStack(ModItems.PRISMAPIERCER));
                player.sendMessage(Text.of("The Prismapiercer has been crafted."));
                LightningEntity bolt = new LightningEntity(EntityType.LIGHTNING_BOLT,serverWorld);
                bolt.setPos(pos.getX(),pos.getY(),pos.getZ());
                bolt.setCosmetic(true);
                serverWorld.spawnEntity(bolt);
                modDataState.piercerCrafted = true;
                modDataState.piercerOwner = player.getUuid();
                modDataState.markDirty();
                return ActionResult.success(true);
            } else if (player.getMainHandStack().isEmpty() && !bookAquired.contains(pos)) {
                List<RawFilteredPair<Text>> pages = List.of(
                        RawFilteredPair.of(Text.literal(
                                "There once was weapon, it was crafted to drive away\n\n" +
                                        "evil and unwanted players."
                        )),
                        RawFilteredPair.of(Text.literal(
                                "It was crafted from a magical material,\n" +
                                        "made using Diamonds (+), Amethyst (X)."
                        )),
                        RawFilteredPair.of(Text.literal(
                                "and Glass (center)"
                        ))
                );
                WrittenBookContentComponent bookContent =
                        new WrittenBookContentComponent(
                                RawFilteredPair.of("The Legend Of The Piercer"), // title
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
                player.sendMessage(Text.of("You are missing the gem in the spectrum."));
                return ActionResult.success(true);
            }
        }
        return ActionResult.FAIL;
    }
}
