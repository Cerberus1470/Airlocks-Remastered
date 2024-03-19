package com.example.airlocks.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BlockWalkway extends SlabBlock {

        private static final VoxelShape WALKWAY_UPPER_AABB = Block.box(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        private static final VoxelShape WALKWAY_LOWER_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);
        private static final DirectionProperty FACING = BlockStateProperties.FACING;
        public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
        public BlockWalkway(Properties properties) {
                super(properties
                        .sound(SoundType.METAL)
                        .strength(5.0F, 10.0F)
                        .lightLevel((state) -> state.getValue(POWERED) ? 15 : 0)
                );
                registerDefaultState(this.defaultBlockState()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(POWERED, false));
        }

//        @Nullable
//        @Override
//        public BlockState getStateForPlacement(BlockPlaceContext context) {
//                return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
//        }
        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
                super.createBlockStateDefinition(builder.add(FACING).add(POWERED));
        }

        @Override
        @NotNull
        public VoxelShape getShape(BlockState state, BlockGetter p_56391_, BlockPos p_56392_, CollisionContext p_56393_) {
                return state.getValue(TYPE).equals(SlabType.BOTTOM) ? WALKWAY_LOWER_AABB : WALKWAY_UPPER_AABB;
        }


        @Override
        public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
                if (state.getValue(POWERED)) {
                        spawnParticles(level, pos, randomSource);
                }
        }

        private static void spawnParticles(Level level, BlockPos pos, RandomSource source) {
                BlockState state = level.getBlockState(pos);
                float particlePosX = pos.getX();
                float particlePosY = pos.getY();
                float particlePosZ = pos.getZ();
                int flip = (state.getValue(TYPE).equals(SlabType.BOTTOM) ? -1 : 1);
                int factor = 2;

                if (state.getValue(FACING) == Direction.EAST || state.getValue(FACING) == Direction.WEST) {
                        level.addParticle(ParticleTypes.CLOUD, false,particlePosX + 0.5F, particlePosY + 0.5F + (0.51F*flip), particlePosZ,
                                0.0D, (0.5D + source.nextFloat())/factor*flip, (0.3D + source.nextFloat())/factor);
                        level.addParticle(ParticleTypes.CLOUD, false, particlePosX + 0.5F, particlePosY + 0.5F + (0.51F*flip), particlePosZ + 1.0F,
                                0.0D, (0.5D + source.nextFloat())/factor*flip, (-0.3D + source.nextFloat())/factor);
                } else if (state.getValue(FACING) == Direction.NORTH || state.getValue(FACING) == Direction.SOUTH) {
                        level.addParticle(ParticleTypes.CLOUD, false,  particlePosX + 0.05F, particlePosY + 0.5F + (0.51F*flip), particlePosZ + 0.5F,
                                (0.3D + source.nextFloat())/factor, (0.5D + source.nextFloat())/factor*flip, 0.0D);
                        level.addParticle(ParticleTypes.CLOUD, false, particlePosX + 0.95F, particlePosY + 0.5F + (0.51F*flip), particlePosZ + 0.5F,
                                (-0.3D - source.nextFloat())/factor, (0.5D + source.nextFloat())/factor*flip, 0.0D);
                }
        }

        @Override
        public boolean placeLiquid(LevelAccessor p_56368_, BlockPos p_56369_, BlockState p_56370_, FluidState p_56371_) {
                return false;
        }

        @Override
        public boolean canPlaceLiquid(BlockGetter p_56363_, BlockPos p_56364_, BlockState p_56365_, Fluid p_56366_) {
                return false;
        }
}
