package com.cuboidlabs.soulscythe.block.custom;

import com.cuboidlabs.soulscythe.item.ModItems;
import com.cuboidlabs.soulscythe.item.custom.LifeGemItem;
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

public class AnvilOfJusticeBlock extends Block {
    public AnvilOfJusticeBlock(Settings settings) {
        super(settings);
    }
    private Set<BlockPos> bookAquired = new HashSet<>();

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world instanceof ServerWorld serverWorld) {
            ItemStack handStack = player.getMainHandStack();
            PersistentModData modDataState = PersistentModData.get(serverWorld);
            if (handStack.getItem().equals(ModItems.HAMMER_BLUEPRINT)) {
                if (modDataState.hammerCrafted) {
                    player.sendMessage(Text.of("The Hammer has already been crafted."));
                    return ActionResult.success(true);
                }
                handStack.decrement(1);
                player.getInventory().insertStack(new ItemStack(ModItems.HAMMER_OF_JUSTICE));
                player.sendMessage(Text.of("The Hammer has been crafted."));
                LightningEntity bolt = new LightningEntity(EntityType.LIGHTNING_BOLT,serverWorld);
                bolt.setPos(pos.getX(),pos.getY(),pos.getZ());
                bolt.setCosmetic(true);
                serverWorld.spawnEntity(bolt);
                modDataState.hammerCrafted = true;
                modDataState.hammerOwner = player.getUuid();
                modDataState.markDirty();
                return ActionResult.success(true);
            } else if (player.getMainHandStack().isEmpty() && !bookAquired.contains(pos)) {
                List<RawFilteredPair<Text>> pages = List.of(
                        RawFilteredPair.of(Text.literal(
                                "There once was weapon, it was pretty powerful.\n\n" +
                                        "The holder was able to set others on fire without touching them."
                        )),
                        RawFilteredPair.of(Text.literal(
                                "But it cost him a lot, to make the blueprint\n" +
                                        "he had to get inipsppsp into a crafting table."
                        )),
                        RawFilteredPair.of(Text.literal(
                                "i = roni lokcb, n = ntehirtee nogit, p = appre, s = tkcis"
                        ))
                );
                WrittenBookContentComponent bookContent =
                        new WrittenBookContentComponent(
                                RawFilteredPair.of("The Legend Of The Hammer"), // title
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
                player.sendMessage(Text.of("You are missing the knowledge of the weapon."));
                return ActionResult.success(true);
            }
        }
        return ActionResult.FAIL;
    }
}
