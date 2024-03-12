package com.example.airlocks.item;

import com.example.airlocks.Airlocks;
import com.example.airlocks.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
        public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS=
                DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Airlocks.MODID);

        public static final RegistryObject<CreativeModeTab> AIRLOCKS_Tab = CREATIVE_MODE_TABS.register("airlocks_tab",
                () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.CANVAS_FABRIC.get()))
                        .title(Component.translatable("creativetab.airlocks_tab"))
                        .displayItems((pParameters, pOutput) -> {
                                pOutput.accept(ModItems.CANVAS_FABRIC.get());
                                pOutput.accept(ModItems.CONSOLE_CASE.get());
                                pOutput.accept(ModBlocks.BLOCK_CANVAS.get());
                                pOutput.accept(ModBlocks.BLOCK_WALKWAY.get());
                        })
                        .build());

        public static void register(IEventBus eventBus) {
                CREATIVE_MODE_TABS.register(eventBus);
        }
}
