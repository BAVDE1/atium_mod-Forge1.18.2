package com.BAVDE.atium_mod.block.custom;

import com.BAVDE.atium_mod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class AtiumOre extends Block {
    public final Random random = new Random();

    public AtiumOre(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pBlockEntity, ItemStack pTool) {
        super.playerDestroy(pLevel, pPlayer, pPos, pState, pBlockEntity, pTool);
        if (!pLevel.isClientSide) {
            //when block is destroyed replace with empty atium ore
            pLevel.setBlock(pPos, ModBlocks.EMPTY_ATIUM_ORE.get().defaultBlockState(), 3);
        }
    }
}
