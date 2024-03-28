package com.example.airlocks.screen;

import com.example.airlocks.block.ModBlocks;
import com.example.airlocks.block.entity.BlockTestingEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class BlockTestingMenu extends AbstractContainerMenu {
    public final BlockTestingEntity blockTestingEntity;
    private final Level level;
    private final ContainerData data;

    public BlockTestingMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public BlockTestingMenu(int containerId, Inventory inventory, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.BLOCK_TESTING_MENU.get(), containerId);
        checkContainerSize(inventory, 2);
        blockTestingEntity = ((BlockTestingEntity) entity);
        this.level = inventory.player.level();
        this.data = data;

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        this.blockTestingEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            this.addSlot(new SlotItemHandler(iItemHandler, 0, 80, 11));
            this.addSlot(new SlotItemHandler(iItemHandler, 0, 80, 59));
        });

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 26;

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockTestingEntity.getBlockPos()),
                player, ModBlocks.BLOCK_TESTING.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i=0 ; i < 3 ; i++) {
            for (int j=0 ; j < 9 ; j++ ) {
                this.addSlot(new Slot(playerInventory, j+i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i=0 ; i < 9 ; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
