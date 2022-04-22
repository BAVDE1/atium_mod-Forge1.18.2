package com.BAVDE.atium_mod.screen;

import com.BAVDE.atium_mod.block.ModBlocks;
import com.BAVDE.atium_mod.block.entity.InfusingTableBlockEntity;
import com.BAVDE.atium_mod.item.ModItems;
import com.BAVDE.atium_mod.recipe.InfusingTableRecipe;
import com.BAVDE.atium_mod.recipe.ModRecipes;
import com.BAVDE.atium_mod.screen.slot.ModInputSlot;
import com.BAVDE.atium_mod.screen.slot.ModResultSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class InfusingTableMenu extends AbstractInfusingMenu {
    private final InfusingTableBlockEntity blockEntity;
    private final List<InfusingTableRecipe> recipes;
    public final Level level;
    protected final Player player;
    protected final ContainerLevelAccess access;


    public InfusingTableMenu(int window, Inventory inv, FriendlyByteBuf friendlyByteBuf) {
        this(window, inv, inv.player.level.getBlockEntity(friendlyByteBuf.readBlockPos()), ContainerLevelAccess.NULL);
    }

    public InfusingTableMenu(int window, Inventory inv, BlockEntity entity, ContainerLevelAccess access) {
        super(ModMenuTypes.INFUSING_TABLE_MENU.get(), window, inv, entity);
        blockEntity = ((InfusingTableBlockEntity) entity);
        this.access = access;
        this.player = inv.player;
        this.level = inv.player.level;
        this.recipes = this.level.getRecipeManager().getAllRecipesFor(InfusingTableRecipe.Type.INSTANCE);

        this.blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            //slotItemHandler determines position (in pixels) of slot in inventory
            //the coords start at 0 on the top right most pixel of inventory texture
            //+x -->  &  +y v
            //slot placement is top right corner of slot (not centre)
            //index should start at 0
            this.addSlot(new ModInputSlot(handler, 0, 79, 23) {
                public void setChanged() {
                    super.setChanged();
                    InfusingTableMenu.this.slotsChanged(this.container);
                }
            });
            this.addSlot(new ModInputSlot(handler, 1, 79, 57) {
                public void setChanged() {
                    super.setChanged();
                    InfusingTableMenu.this.slotsChanged(this.container);
                }
            });
            this.addSlot(new ModResultSlot(handler, 2, 79, 98) {
                public boolean mayPlace(ItemStack itemStack) {return false;}
                public void onTake(Player player, ItemStack itemStack) {
                    InfusingTableMenu.this.onTake(player, itemStack);
                }
            });
        });
        addPlayerInventory(inv, 139);
        addPlayerHotbar(inv, 196);
    }

    @Override
    protected boolean mayPickup(Player player, boolean bool) {
        return true;
    }

    @Override
    protected void onTake(Player player, ItemStack itemStack) {
        itemStack.onCraftedBy(player.level, player, itemStack.getCount());
        this.shrinkStackInSlot(0);
        this.shrinkStackInSlot(1);
        this.access.execute((level, blockPos) -> {
            level.levelEvent(1044, blockPos, 0);
        });
    }

    private void shrinkStackInSlot(int slot) {
        ItemStack itemstack = blockEntity.itemHandler.getStackInSlot(slot);
        itemstack.shrink(1);
        blockEntity.itemHandler.setStackInSlot(slot, itemstack);
    }

    @Override
    protected boolean isValidBlock(BlockState blockState) {
        return true;
    }

    @Override
    public void createResult() {
        if (hasRecipe(blockEntity)) {
            Level level = blockEntity.getLevel();
            SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
            for (int i = 0; i < blockEntity.itemHandler.getSlots(); i++) {
                inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));
            }
            Optional<InfusingTableRecipe> match = level.getRecipeManager().getRecipeFor(InfusingTableRecipe.Type.INSTANCE, inventory, level);

            if (match.isPresent()) {
                blockEntity.itemHandler.setStackInSlot(2, new ItemStack(match.get().getResultItem().getItem(), 1));
            }
        } else {
            blockEntity.itemHandler.setStackInSlot(2, ItemStack.EMPTY);
        }
    }

    private static boolean hasRecipe(InfusingTableBlockEntity entity) {
        Level level = entity.getLevel();
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }
        Optional<InfusingTableRecipe> match = level.getRecipeManager().getRecipeFor(InfusingTableRecipe.Type.INSTANCE, inventory, level);

        return match.isPresent();
    }

    public int hasMetal() {
        /*
        1 = Iron
        2 = Steel
        3 = Tin
        4 = Pewter
        5 = Brass
        6 = Zinc
        7 = Copper
        8 = Bronze
        9 = Gold */
        ItemStack itemStack = this.slots.get(0).getItem();
        int metal = 0;
        if (itemStack.getItem() == Items.IRON_INGOT) {metal = 1;}
        else if (itemStack.getItem() == ModItems.STEEL.get()) {metal = 2;}
        else if (itemStack.getItem() == ModItems.TIN.get()) {metal = 3;}
        else if (itemStack.getItem() == ModItems.PEWTER.get()) {metal = 4;}
        else if (itemStack.getItem() == ModItems.BRASS.get()) {metal = 5;}
        else if (itemStack.getItem() == ModItems.ZINC.get()) {metal = 6;}
        else if (itemStack.getItem() == Items.COPPER_INGOT) {metal = 7;}
        else if (itemStack.getItem() == ModItems.BRONZE.get()) {metal = 8;}
        else if (itemStack.getItem() == Items.GOLD_INGOT) {metal = 9;}
        return metal;
    }

    //if still close enough to access inventory
    //e.g. if player is in mine-cart and moves too far away from block
    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.INFUSING_TABLE.get());
    }


}