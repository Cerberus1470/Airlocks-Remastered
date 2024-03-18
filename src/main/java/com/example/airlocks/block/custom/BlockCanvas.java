package com.example.airlocks.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockCanvas extends DirectionalBlock {

        private static final VoxelShape[] CORNER_LOWER_AABB = {Block.box(0.0D,0.0D,8D,16D,8D,16D), // SOUTH
                Block.box(0.0D,0.0D,0.0D,8.0D,8D,16D), // WEST
                Block.box(0.0D,0.0D,0.0D,16D,8D,8D), // NORTH
                Block.box(8.0D,0.0D,0.0D,16D,8D,16D) // EAST
        };
        private static final VoxelShape[] CORNER_UPPER_AABB = {Block.box(0.0D,8D,8.0D,16D,16D,16D), // SOUTH
                Block.box(0.0D,8D,0.0D,8D,16D,16D), // WEST
                Block.box(0.0D,8D,0.0D,16D,16D,8D), // NORTH
                Block.box(8.0D,8D,0.0D,16D,16D,16D) // EAST
        };
        private static final BooleanProperty CORNER = BooleanProperty.create("corner");
        private static final EnumProperty<Half> HALF = BlockStateProperties.HALF;

        public BlockCanvas(Properties properties) {
                super(properties
                        .sound(SoundType.WOOL)
                        .strength(5.0F, 20.0F)
                        .lightLevel((light) -> 0)
                );
                registerDefaultState(this.defaultBlockState()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(CORNER, false)
                        .setValue(HALF, Half.BOTTOM)
                );
        }

        @Nullable
        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context) {
                Direction facing = context.getHorizontalDirection();
                boolean corner = false, half = false;
                Level level = context.getLevel();
                BlockPos pos = context.getClickedPos();
                if (level.getBlockState(pos.below()).getBlock() instanceof BlockCanvas && !level.getBlockState(pos.below()).getValue(CORNER)) {
                        if (level.getBlockState(pos.relative(facing)).getBlock() instanceof BlockCanvas &&
                                !level.getBlockState(pos.relative(facing)).getValue(CORNER)) {
                                corner = true;
                        }
                } else if (level.getBlockState(pos.above()).getBlock() instanceof BlockCanvas && !level.getBlockState(pos.above()).getValue(CORNER)) {
                        if (level.getBlockState(pos.relative(facing)).getBlock() instanceof BlockWalkway) {
                                corner = true;
                                half = true;
                        }
                }
                return this.defaultBlockState()
                        .setValue(FACING, facing)
                        .setValue(CORNER, corner)
                        .setValue(HALF, half ? Half.TOP : Half.BOTTOM);
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
                builder.add(FACING).add(CORNER).add(HALF);
        }

        @Override
        public @NotNull VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
                try {
                        if (state.getValue(HALF).equals(Half.BOTTOM)) {
                                return (state.getValue(CORNER) ? CORNER_LOWER_AABB[state.getValue(FACING).get2DDataValue()] : Shapes.block());
                        } else {
                                return (state.getValue(CORNER) ? CORNER_UPPER_AABB[state.getValue(FACING).get2DDataValue()] : Shapes.block());
                        }
                } catch (IndexOutOfBoundsException e) {
                        return Shapes.block();
                }
        }

        //        @Override
//        @Deprecated
//        public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
//
//                level.playSound(player, blockPos, SoundEvents.NOTE_BLOCK_DIDGERIDOO.get(), SoundSource.BLOCKS, 1f, 1f);
//
//                return InteractionResult.SUCCESS;
//        }
}
