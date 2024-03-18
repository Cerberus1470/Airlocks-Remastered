package com.example.airlocks.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockWalkway extends SlabBlock {

        private static final VoxelShape Walkway_AABB = Block.box(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        public static Level bruh;
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

        @Nullable
        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context) {
                return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
        }
        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
                super.createBlockStateDefinition(builder.add(FACING).add(POWERED));
        }

        @Override
        @NotNull
        public VoxelShape getShape(BlockState p_56390_, BlockGetter p_56391_, BlockPos p_56392_, CollisionContext p_56393_) {
                return Walkway_AABB;
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
                int factor = 2;

                if (state.getValue(FACING) == Direction.EAST || state.getValue(FACING) == Direction.WEST) {
                        level.addParticle(ParticleTypes.CLOUD, false,particlePosX + 0.5F, particlePosY + 1.01F, particlePosZ,
                                0.0D, 0.5D + source.nextFloat(), 0.3D + source.nextFloat());
                        level.addParticle(ParticleTypes.CLOUD, false, particlePosX + 0.5F, particlePosY + 1.01F, particlePosZ + 1.0F,
                                0.0D, 0.5D + source.nextFloat(), -0.3D + source.nextFloat());
                } else if (state.getValue(FACING) == Direction.NORTH || state.getValue(FACING) == Direction.SOUTH) {
                        level.addParticle(ParticleTypes.CLOUD, false,  particlePosX + 0.05F, particlePosY + 1.01F, particlePosZ + 0.5F,
                                (0.3D + source.nextFloat())/factor, (0.5D + source.nextFloat())/factor, 0.0D);
                        level.addParticle(ParticleTypes.CLOUD, false, particlePosX + 0.95F, particlePosY + 1.01F, particlePosZ + 0.5F,
                                (-0.3D - source.nextFloat())/factor, (0.5D + source.nextFloat())/factor, 0.0D);
                }
        }
}
