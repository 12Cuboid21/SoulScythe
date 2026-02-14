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
            if (!modDataState.scytheCrafted) {
                player.sendMessage(Text.of("There is nothing to defend against YET."));
                return ActionResult.success(true);
            }
            if (handStack.getItem() instanceof LifeGemItem) {
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
            } else {
                player.sendMessage(Text.of("You are missing the gem in the heart."));
                return ActionResult.success(true);
            }
        }
        return ActionResult.FAIL;
    }
}
