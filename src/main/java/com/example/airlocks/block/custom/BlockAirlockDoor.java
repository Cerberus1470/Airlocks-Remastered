package com.example.airlocks.block.custom;

import com.example.airlocks.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class BlockAirlockDoor extends DoorBlock {
        public BlockAirlockDoor(Properties properties) {
                super(properties
                        .sound(SoundType.METAL)
                        .strength(5.0F, 20.0F)
                        .lightLevel((light) -> 0)
                , BlockSetType.IRON);
        }

        @Override
        @NotNull
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
                state = state.cycle(OPEN);
                level.setBlock(pos, state, 10);
                level.playSound(player, pos, state.getValue(OPEN) ? ModSounds.BLOCK_DOOR_OPEN.get() : ModSounds.BLOCK_DOOR_CLOSE.get(), SoundSource.BLOCKS);
                level.gameEvent(player, this.isOpen(state) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
                if (level.getBlockState(pos).getValue(HALF) == DoubleBlockHalf.UPPER) {
                        pos = pos.below();
                }
                state = level.getBlockState(pos);
                BlockPos connectedCanvas = getConnectedCanvas(level, state, pos);
                if (connectedCanvas != null) {
                        for (BlockPos connectedConsolePos : BlockAirlockConsole.getConnectedDevices(
                                        level, connectedCanvas, new ArrayList<>(), new ArrayList<>(), "BlockAirlockConsole")) {
                                BlockState consoleState = level.getBlockState(connectedConsolePos);
                                level.setBlock(connectedConsolePos, consoleState
                                        .setValue(BlockAirlockConsole.ERROR,
                                                BlockAirlockConsole.getConnectedDevices(level, connectedCanvas, new ArrayList<>(), new ArrayList<>(), "BlockAirlockDoor").isEmpty() ? 0 :
                                                        consoleState.getValue(BlockAirlockConsole.ERROR) == 1 ? 2 : 1)
                                        .setValue(BlockAirlockConsole.PRESSURIZED,
                                                consoleState.getValue(BlockAirlockConsole.ERROR) != 2 &&
                                                        (!consoleState.getValue(BlockAirlockConsole.POWERED) &&
                                                                consoleState.getValue(BlockAirlockConsole.PRESSURIZED))
                                        ), 3);
                        }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
        }

        public BlockPos getConnectedCanvas(Level level, BlockState state, BlockPos door_pos) {
                BlockPos hingeBlock;
                switch (state.getValue(HINGE)) {
                        case LEFT:
                                hingeBlock = door_pos.relative(state.getValue(FACING).getCounterClockWise());
                                if (level.getBlockState(hingeBlock).getBlock() instanceof BlockCanvas) {
                                         return hingeBlock;
                                }
                        case RIGHT:
                                hingeBlock = door_pos.relative(state.getValue(FACING).getClockWise());
                                if (level.getBlockState(hingeBlock).getBlock() instanceof BlockCanvas) {
                                        return hingeBlock;
                                }
                }
                return null;
        }

        @Override
        public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
                return false;
        }

        public void neighborChanged(BlockState p_52776_, Level p_52777_, BlockPos p_52778_, Block p_52779_, BlockPos p_52780_, boolean p_52781_) {
        }
}
