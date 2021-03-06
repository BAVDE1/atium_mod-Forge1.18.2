package com.BAVDE.atium_mod.block.custom;

import com.BAVDE.atium_mod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Random;

public class BuddingCrystallineLeaves extends CrystallineLeavesBlock implements BonemealableBlock {
    public static final IntegerProperty GROWTH = IntegerProperty.create("growth", 0, 10);
    public static final BooleanProperty GROWN = BooleanProperty.create("grown");

    public BuddingCrystallineLeaves(Properties p_54422_) {
        super(p_54422_);
        this.registerDefaultState(this.defaultBlockState().setValue(GROWTH, 0));
        this.registerDefaultState(this.defaultBlockState().setValue(GROWN, false));
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {return true;}
    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {return 60;}
    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {return 30;}

    //always ticking if not fully grown
    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return (!pState.getValue(PERSISTENT) || pState.getValue(GROWTH) < 10);
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRandom) {
        //for leaf decay
        if (!pState.getValue(PERSISTENT) && pState.getValue(DISTANCE) == 8) {
            dropResources(pState, pLevel, pPos);
            pLevel.removeBlock(pPos, false);
        }

        //for growth
        int growth = pState.getValue(GROWTH);
        //code to grow each tick
        if (growth < 9) { //if growth less than 10
            pLevel.setBlock(pPos, pState.setValue(GROWTH, (growth + 1)), 3); //add 1 to growth state
        } else if (growth == 9) { //if growth is 9
            pLevel.setBlock(pPos, pState.setValue(GROWTH, 10).setValue(GROWN, true), 3); //sets growth to 10 and grown to true
        }
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack itemStack = pPlayer.getItemInHand(pHand); //gets what the user is holding
        if (itemStack.is(Items.BONE_MEAL) && !pState.getValue(GROWN)) { //if holding bone meal and not fully grown
            return InteractionResult.PASS;
        } else if (pState.getValue(GROWN)) { //if the blocks is fully grown (if state is true)
            pLevel.setBlock(pPos, pState.setValue(GROWTH, 0).setValue(GROWN, false), 3); //set growth to 0 and grown to false
            playSound(pLevel, pPos);
            int amount = 1 + this.RANDOM.nextInt(3); //generates random number between 1 and 3
            popResource(pLevel, pPos, new ItemStack(ModItems.CRYSTALLIZED_SHARD.get(), amount)); //drops 1 to 3 crystallized shards
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        } else {
            return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
        }
    }

    @Override
    public void onProjectileHit(Level pLevel, BlockState pState, BlockHitResult pHit, Projectile pProjectile) {
        if (pState.getValue(GROWN)) {
            BlockPos pPos = pHit.getBlockPos();
            pLevel.setBlock(pPos, pState.setValue(GROWTH, 0).setValue(GROWN, false), 3); //set growth to 0 and grown to false
            playSound(pLevel, pPos);
            int amount = 1 + this.RANDOM.nextInt(3); //generates random number between 1 and 3
            popResource(pLevel, pPos, new ItemStack(ModItems.CRYSTALLIZED_SHARD.get(), amount)); //drops 1 to 3 crystallized shards
        }
    }

    private void playSound(Level level, BlockPos pos) {
        level.playSound((Player) null, pos, SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.BLOCKS, 1.0F, 0.5F + level.random.nextFloat() * 1.2F); //plays sound
        level.playSound((Player) null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 4.0F, 0.5F + level.random.nextFloat() * 1.2F); //plays sound
    }

    //bone meal stuff
    @Override
    public boolean isValidBonemealTarget(BlockGetter pLevel, BlockPos pPos, BlockState pState, boolean pIsClient) {
        return pState.getValue(GROWTH) < 10;}
    @Override
    public boolean isBonemealSuccess(Level pLevel, Random pRandom, BlockPos pPos, BlockState pState) {
        return true;
    }
    @Override
    public void performBonemeal(ServerLevel pLevel, Random pRandom, BlockPos pPos, BlockState pState) {
        int amount = Math.min(10, pState.getValue(GROWTH) + (1 + this.RANDOM.nextInt(3))); //math.min gets the lowest number of the two
        if (amount == 10) {
            pLevel.setBlock(pPos, pState.setValue(GROWTH, amount).setValue(GROWN, true), 2); //adds random growth to leaves
        } else {
            pLevel.setBlock(pPos, pState.setValue(GROWTH, amount), 2); //adds random growth to leaves
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(GROWTH, GROWN, DISTANCE, PERSISTENT);
    }
}