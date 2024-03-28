package com.example.airlocks.block.entity;

import com.example.airlocks.item.ModItems;
import com.example.airlocks.screen.BlockTestingMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockTestingEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemStackHandler = new ItemStackHandler(2);

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    protected final ContainerData data;
    private int progress = 0;
    private final int maxProgress = 78;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();


    public BlockTestingEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLOCK_TESTING_BE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> BlockTestingEntity.this.progress;
                    case 1 -> BlockTestingEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int i1) {
                switch (i) {
                    case 0 -> BlockTestingEntity.this.progress = i1;
                    case 1 -> BlockTestingEntity.this.progress = i1;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemStackHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemStackHandler.getSlots());
        for (int i = 0 ; i<itemStackHandler.getSlots(); i++) {
            inventory.setItem(i, itemStackHandler.getStackInSlot(i));
        }
        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.airlocks.block_testing");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new BlockTestingMenu(i, inventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag p_187471_) {
        p_187471_.put("inventory", itemStackHandler.serializeNBT());
        p_187471_.putInt("block_testing.progress", progress);
        super.saveAdditional(p_187471_);
    }

    @Override
    public void load(CompoundTag p_155245_) {
        itemStackHandler.deserializeNBT(p_155245_.getCompound("inventory"));
        progress = p_155245_.getInt("block_testing.progress");
        super.load(p_155245_);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (hasRecipe()) {
            increaseCraftingProgress();
            setChanged(level, pos, state);

            if (hasProgressFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void resetProgress() {
        progress = 0;
    }

    private void craftItem() {
        ItemStack result = new ItemStack(ModItems.CANVAS_FABRIC.get(), 1);
        this.itemStackHandler.extractItem(INPUT_SLOT, 1, false);

        this.itemStackHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(),
                this.itemStackHandler.getStackInSlot(OUTPUT_SLOT).getCount() + result.getCount()));
    }

    private boolean hasRecipe() {
        boolean hasCraftingItem = this.itemStackHandler.getStackInSlot(INPUT_SLOT).getItem() == ModItems.CANVAS_FABRIC.get();
        ItemStack result = new ItemStack(ModItems.CANVAS_FABRIC.get());
        return hasCraftingItem && canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.itemStackHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() || this.itemStackHandler.getStackInSlot(OUTPUT_SLOT).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.itemStackHandler.getStackInSlot(OUTPUT_SLOT).getCount() + count <= this.itemStackHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

}
