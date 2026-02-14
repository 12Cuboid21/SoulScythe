package com.cuboidlabs.soulscythe.command;

import com.cuboidlabs.soulscythe.util.PersistentModData;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class SoulSplitCommand {
    public static int switchCraftedScythe(CommandContext<ServerCommandSource> ctx) {
        if (ctx.getSource().getPlayer().getWorld() instanceof ServerWorld serverWorld) {
            PersistentModData persistentModData = PersistentModData.get(serverWorld);
            persistentModData.scytheCrafted = !persistentModData.scytheCrafted;
            persistentModData.markDirty();
            ctx.getSource().sendFeedback(() -> Text.literal("scytheCrafted = " + persistentModData.scytheCrafted), false);
        }
        return 1;
    }

    public static int switchCraftedSword(CommandContext<ServerCommandSource> ctx) {
        if (ctx.getSource().getPlayer().getWorld() instanceof ServerWorld serverWorld) {
            PersistentModData persistentModData = PersistentModData.get(serverWorld);
            persistentModData.greatswordCrafted = !persistentModData.greatswordCrafted;
            persistentModData.markDirty();
            ctx.getSource().sendFeedback(() -> Text.literal("greatswordCrafted = " + persistentModData.greatswordCrafted), false);
        }
        return 1;
    }

    public static int switchCraftedHammer(CommandContext<ServerCommandSource> ctx) {
        if (ctx.getSource().getPlayer().getWorld() instanceof ServerWorld serverWorld) {
            PersistentModData persistentModData = PersistentModData.get(serverWorld);
            persistentModData.hammerCrafted = !persistentModData.hammerCrafted;
            persistentModData.markDirty();
            ctx.getSource().sendFeedback(() -> Text.literal("hammerCrafted = " + persistentModData.hammerCrafted), false);
        }
        return 1;
    }

    public static int switchCraftedPiercer(CommandContext<ServerCommandSource> ctx) {
        if (ctx.getSource().getPlayer().getWorld() instanceof ServerWorld serverWorld) {
            PersistentModData persistentModData = PersistentModData.get(serverWorld);
            persistentModData.piercerCrafted = !persistentModData.piercerCrafted;
            persistentModData.markDirty();
            ctx.getSource().sendFeedback(() -> Text.literal("piercerCrafted = " + persistentModData.piercerCrafted), false);
        }
        return 1;
    }
}
