package com.example.airlocks.block.custom;

import com.example.airlocks.Airlocks;
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
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class BlockAirlockDoor extends DoorBlock {
        private static final VoxelShape[] CLOSED_AABB = {
                Block.box(0,0,0,16,16,4), //SOUTH
                Block.box(12,0,0,16,16,16), //WEST
                Block.box(0,0,12,16,16,16), //NORTH
                Block.box(0,0,0,4,16,16) //EAST
        };
        private static final VoxelShape[] BOTTOM_LEFT_OPEN_AABB = {
                Airlocks.join(BooleanOp.OR,
                        Block.box(10.75,0.25,2,14.75,16,17.5),
                        Block.box(0,0,0,16,0.25,4),
                        Block.box(0,0,0,0.25,16,4),
                        Block.box(15.75,0,0,16,16,4)
                ), // SOUTH
                Airlocks.join(BooleanOp.OR,
                        Block.box(-1.5,0.25,10.75,14,16,14.75),
                        Block.box(12,0,0,16,0.25,16),
                        Block.box(12,0,0,16,16,0.25),
                        Block.box(12,0,15.75,16,16,16)
                ), // WEST
                Airlocks.join(BooleanOp.OR,
                        Block.box(1.25,0.25,-1.5,5.25,16,14),
                        Block.box(0,0,12,16,0.25,16),
                        Block.box(0,0,12,0.25,16,16),
                        Block.box(15.75,0,12,16,16,16)
                ), // NORTH
                Airlocks.join(BooleanOp.OR,
                        Block.box(2,0.25,1.25,17.5,16,5.25),
                        Block.box(0,0,0,4,0.25,16),
                        Block.box(0,0,0,4,16,0.25),
                        Block.box(0,0,15.75,4,16,16)
                ) // EAST
        };
        private static final VoxelShape[] BOTTOM_RIGHT_OPEN_AABB = {
                Airlocks.join(BooleanOp.OR,
                        Block.box(1.25,0.25,2,5.25,16,17.5),
                        Block.box(0,0,0,16,0.25,4),
                        Block.box(0,0,0,0.25,16,4),
                        Block.box(15.75,0,0,16,16,4)
                ), // SOUTH
                Airlocks.join(BooleanOp.OR,
                        Block.box(-1.5,0.25,1.25,14,16,5.25),
                        Block.box(12,0,0,16,0.25,16),
                        Block.box(12,0,0,16,16,0.25),
                        Block.box(12,0,15.75,16,16,16)
                ), // WEST
                Airlocks.join(BooleanOp.OR,
                        Block.box(10.75,0.25,-1.5,14.75,16,14),
                        Block.box(0,0,12,16,0.25,16),
                        Block.box(0,0,12,0.25,16,16),
                        Block.box(15.75,0,12,16,16,16)
                ), //NORTH
                Airlocks.join(BooleanOp.OR,
                        Block.box(2,0.25,10.75,17.5,16,14.75),
                        Block.box(0,0,0,4,0.25,16),
                        Block.box(0,0,0,4,16,0.25),
                        Block.box(0,0,15.75,4,16,16)
                ) // EAST
        };
        private static final VoxelShape[] TOP_LEFT_OPEN_AABB = {
                Airlocks.join(BooleanOp.OR,
                        Block.box(10.75,0,2,14.75,15.75,17.5),
                        Block.box(0,15.75,0,16,16,4),
                        Block.box(0,0,0,0.25,16,4),
                        Block.box(15.75,0,0,16,16,4)
                ), // SOUTH
                Airlocks.join(BooleanOp.OR,
                        Block.box(-1.5,0,10.75,14,15.75,14.75),
                        Block.box(12,15.75,0,16,16,16),
                        Block.box(12,0,0,16,16,0.25),
                        Block.box(12,0,15.75,16,16,16)
                ), // WEST
                Airlocks.join(BooleanOp.OR,
                        Block.box(1.25,0,-1.5,5.25,15.75,14),
                        Block.box(0,15.75,12,16,16,16),
                        Block.box(0,0,12,0.25,16,16),
                        Block.box(15.75,0,12,16,16,16)
                ), // NORTH
                Airlocks.join(BooleanOp.OR,
                        Block.box(2,0,1.25,17.5,15.75,5.25),
                        Block.box(0,15.75,0,4,16,16),
                        Block.box(0,0,0,4,16,0.25),
                        Block.box(0,0,15.75,4,16,16)
                ), // NORTH
        };
        private static final VoxelShape[] TOP_RIGHT_OPEN_AABB = {
                Airlocks.join(BooleanOp.OR,
                        Block.box(1.25,0,2,5.25,15.75,17.5),
                        Block.box(0,15.75,0,16,16,4),
                        Block.box(0,0,0,0.25,16,4),
                        Block.box(15.75,0,0,16,16,4)
                ), // SOUTH
                Airlocks.join(BooleanOp.OR,
                        Block.box(-1.5,0,1.25,14,15.75,5.25),
                        Block.box(12,15.75,0,16,16,16),
                        Block.box(12,0,0,16,16,0.25),
                        Block.box(12,0,15.75,16,16,16)
                ), // WEST
                Airlocks.join(BooleanOp.OR,
                        Block.box(10.75,0,-1.5,14.75,15.75,14),
                        Block.box(0,15.75,12,16,16,16),
                        Block.box(0,0,12,0.25,16,16),
                        Block.box(15.75,0,12,16,16,16)
                ), // NORTH
                Airlocks.join(BooleanOp.OR,
                        Block.box(2,0,10.75,17.5,15.75,14.75),
                        Block.box(0,15.75,0,4,16,16),
                        Block.box(0,0,0,4,16,0.25),
                        Block.box(0,0,15.75,4,16,16)
                ), // NORTH
        };


        public BlockAirlockDoor(Properties properties) {
                super(properties
                        .sound(SoundType.METAL)
                        .strength(5.0F, 20.0F)
                        .lightLevel((light) -> 0)
                , BlockSetType.IRON);
        }

        @Override
        @NotNull
        public InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult result) {
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


        @NotNull
        @Override
        public VoxelShape getShape(BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
                int index = state.getValue(FACING).get2DDataValue();
                if (state.getValue(OPEN)) {
                        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
                                return state.getValue(HINGE) == DoorHingeSide.LEFT ? BOTTOM_LEFT_OPEN_AABB[index] : BOTTOM_RIGHT_OPEN_AABB[index];
                        } else {
                                return state.getValue(HINGE) == DoorHingeSide.LEFT ? TOP_LEFT_OPEN_AABB[index] : TOP_RIGHT_OPEN_AABB[index];
                        }
//                        return OPEN_AABB[(state.getValue(HINGE) == DoorHingeSide.LEFT ? 0 : 4) + state.getValue(FACING).get2DDataValue()];
                } else {
                        return CLOSED_AABB[index];
                }
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

        public void neighborChanged(@NotNull BlockState p_52776_, @NotNull Level p_52777_, @NotNull BlockPos p_52778_, @NotNull Block p_52779_, @NotNull BlockPos p_52780_, boolean p_52781_) {
        }
}
