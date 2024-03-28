package com.example.airlocks.block.entity;

import com.example.airlocks.Airlocks;
import com.example.airlocks.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Airlocks.MODID);

    public static final RegistryObject<BlockEntityType<BlockTestingEntity>> BLOCK_TESTING_BE =
            BLOCK_ENTITIES.register("block_testing_be", () ->
                    BlockEntityType.Builder.of(BlockTestingEntity::new,
                            ModBlocks.BLOCK_TESTING.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
