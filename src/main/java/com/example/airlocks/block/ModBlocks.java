package com.example.airlocks.block;

import com.example.airlocks.Airlocks;
import com.example.airlocks.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
        public static final DeferredRegister<Block> BLOCKS =
                DeferredRegister.create(ForgeRegistries.BLOCKS, Airlocks.MODID);

        public static final RegistryObject<Block> BLOCK_CANVAS = registerBlock("block_canvas",
                () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.WOOL)));
        public static final RegistryObject<Block> BLOCK_WALKWAY = registerBlock("block_walkway",
                () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

        private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<Block> block) {
                RegistryObject<T> toReturn = (RegistryObject<T>) BLOCKS.register(name, block);
                registerBlockItem(name, toReturn);
                return toReturn;
        }

        private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
                return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        }

        public static void register(IEventBus eventBus) {
                BLOCKS.register(eventBus);
        }
}
