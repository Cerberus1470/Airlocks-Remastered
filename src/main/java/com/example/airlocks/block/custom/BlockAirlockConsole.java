package com.example.airlocks.block.custom;

import com.example.airlocks.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class BlockAirlockConsole extends ButtonBlock {

        private static final int TICK_RATE = 80;
        public static final VoxelShape[] AIRLOCK_CONSOLE_AABB = {
                Block.box(3D, 1D, 0D, 13D, 15D, 1D), //South
                Block.box(15D, 1D, 3D, 16D, 15D, 13D), //West
                Block.box(3D, 1D, 15D, 13D, 15D, 16D), //North
                Block.box(0D, 1D, 3D, 1D, 15D, 13D) //East
        };

//        public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
        public static final BooleanProperty PRESSURIZED = BooleanProperty.create("pressurized");
        public static final IntegerProperty ERROR = IntegerProperty.create("error", 0, 2);

        public BlockAirlockConsole(Properties properties) {
                super(properties
                        .sound(SoundType.METAL)
                        .strength(5.0F, 20.0F)
                        .lightLevel((light) -> 10),
                        BlockSetType.IRON, TICK_RATE, false);
                this.registerDefaultState(this.defaultBlockState()
                        .setValue(PRESSURIZED, false)
                        .setValue(ERROR, 0));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
                super.createBlockStateDefinition(builder.add(PRESSURIZED).add(ERROR));
        }

        @Override
        @NotNull
        public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collisionContext) {
                return AIRLOCK_CONSOLE_AABB[state.getValue(FACING).get2DDataValue()];
        }

        @Nullable
        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context) {
                for(Direction direction : context.getNearestLookingDirections()) {
                        BlockState blockstate;
                        if (direction.getAxis() == Direction.Axis.Y) {
                                blockstate = this.defaultBlockState().setValue(FACE, AttachFace.WALL).setValue(FACING, context.getHorizontalDirection().getOpposite());
                        } else {
                                blockstate = this.defaultBlockState().setValue(FACE, AttachFace.WALL).setValue(FACING, direction.getOpposite());
                        }

                        if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
                                return blockstate;
                        }
                }

                return null;
        }

        @Override
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
                state = state.setValue(ERROR, 0);
                BlockWalkway.bruh = level;
                BlockPos connectedCanvas = getConnectedCanvas(level, state, pos);
                if (state.getValue(POWERED)) {
                        return InteractionResult.sidedSuccess(level.isClientSide);
                } else {
                        if (connectedCanvas == null) {
                                state.setValue(POWERED, true);
                                this.playSound(player, level, pos, false);
                                level.scheduleTick(pos, this, TICK_RATE);
                        } else {
                                if (!getConnectedDevices(level, connectedCanvas, new ArrayList<>(), new ArrayList<>(), "BlockAirlockDoor").isEmpty()) {
                                        state = state.setValue(ERROR, 1);
                                } else {
                                        state = state.setValue(POWERED, true);
                                        this.playSound(player, level, pos, true);
                                        for (BlockPos connectedConsolePos : getConnectedDevices(level, connectedCanvas, new ArrayList<>(), new ArrayList<>(), "BlockAirlockConsole")) {
                                                level.setBlock(connectedConsolePos, state.setValue(FACING, level.getBlockState(connectedConsolePos).getValue(FACING))
                                                        .setValue(FACE, level.getBlockState(connectedConsolePos).getValue(FACE)), 19);
                                        }
                                        for (BlockPos connectedWalkwayPos : getConnectedDevices(level, connectedCanvas, new ArrayList<>(), new ArrayList<>(), "BlockWalkway")) {
                                                level.setBlock(connectedWalkwayPos, level.getBlockState(connectedWalkwayPos).setValue(POWERED, true), 19);
                                        }
                                        level.scheduleTick(pos, this, TICK_RATE);
                                }
                        }
                }
                level.setBlock(pos, state, 3);
                return InteractionResult.sidedSuccess(level.isClientSide);
        }

        @Override
        protected void playSound(@org.jetbrains.annotations.Nullable Player player, LevelAccessor level, BlockPos pos, boolean flag) {
                level.playSound(player, pos, ModSounds.BLOCK_CONSOLE_KEYPRESS.get(), SoundSource.BLOCKS, 10.0F, 1.0F);
                level.playSound(player, pos, ModSounds.BLOCK_CONSOLE_PRESSURIZING.get(), SoundSource.BLOCKS, 10.0F, 1.0F);
        }

        @Override
        public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
                state = state.cycle(POWERED);
                if (state.getValue(ERROR) == 0) {
                        state = state.cycle(PRESSURIZED);
                        level.playSound(null, pos, ModSounds.BLOCK_CONSOLE_COMPLETE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                } else {
                        state = state.setValue(PRESSURIZED, false);
                }
                BlockPos connectedCanvas = getConnectedCanvas(level, state, pos);
                if (connectedCanvas != null) {
                        for (BlockPos receiverPos : getConnectedDevices(level, connectedCanvas, new ArrayList<>(), new ArrayList<>(), "BlockAirlockConsole")) {
                                BlockState existing = level.getBlockState(receiverPos);
                                level.setBlock(receiverPos, state.setValue(FACING, existing.getValue(FACING)).setValue(FACE, existing.getValue(FACE)), 3);
                        }
                        for (BlockPos walkwayPos : getConnectedDevices(level, connectedCanvas, new ArrayList<>(), new ArrayList<>(), "BlockWalkway")) {
                                level.setBlock(walkwayPos, level.getBlockState(walkwayPos).setValue(BlockWalkway.POWERED, false), 3);
                        }
                }
                level.setBlock(pos, state, 3);
        }

        public static BlockPos getConnectedCanvas(Level level, BlockState state, BlockPos observer_pos) {
                if (level.getBlockState(observer_pos.relative(state.getValue(FACING).getOpposite())).getBlock() instanceof BlockCanvas) {
                        return observer_pos.relative(state.getValue(FACING).getOpposite());
                }
                return null;
        }

        public static ArrayList<BlockPos> getConnectedDevices(Level level, BlockPos transmitter, ArrayList<BlockPos> exclude, ArrayList<BlockPos> devices, String device) {
                BlockPos[] adjacentBlocks = {transmitter.north(), transmitter.east(), transmitter.above(),
                        transmitter.south(), transmitter.west(), transmitter.below()};
                for (BlockPos adjacentBlock : adjacentBlocks) {
                        if (adjacentBlock != null && !exclude.contains(adjacentBlock)) {
                                if (level.getBlockState(adjacentBlock).getBlock() instanceof BlockCanvas
//                                        || level.getBlockState(adjacentBlock).getBlock() instanceof BlockCanvasCorner
                                ) {
                                        exclude.add(transmitter);
                                        getConnectedDevices(level, adjacentBlock, exclude, devices, device);
                                }
                                if (device.equals("BlockAirlockConsole") && level.getBlockState(adjacentBlock).getBlock() instanceof BlockAirlockConsole) {
                                        if (!devices.contains(adjacentBlock) && transmitter.equals(getConnectedCanvas(level, level.getBlockState(adjacentBlock), adjacentBlock))) {
                                                devices.add(adjacentBlock);
                                        }
                                } else if (device.equals("BlockAirlockDoor") && level.getBlockState(adjacentBlock).getBlock() instanceof BlockAirlockDoor) {
                                        if (level.getBlockState(adjacentBlock).getValue(DoorBlock.OPEN)) {
                                                devices.add(adjacentBlock);
                                                return devices;
                                        }
                                } else if (device.equals("BlockWalkway") && level.getBlockState(adjacentBlock).getBlock() instanceof BlockWalkway) {
                                        if (!devices.contains(adjacentBlock)) {
                                                devices.add(adjacentBlock);
                                        }
                                }
                        }
                }
                return devices;
        }

        @Override
        public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @org.jetbrains.annotations.Nullable Direction direction) {
                return false;
        }
        @Override
        public boolean isSignalSource(BlockState p_51114_) {
                return false;
        }
        @Override
        public int getSignal(BlockState p_51078_, BlockGetter p_51079_, BlockPos p_51080_, Direction p_51081_) {
                return 0;
        }
        @Override
        public int getDirectSignal(BlockState p_51109_, BlockGetter p_51110_, BlockPos p_51111_, Direction p_51112_) {
                return 0;
        }
}
