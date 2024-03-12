package com.example.airlocks.item;

import com.example.airlocks.Airlocks;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
        public static final DeferredRegister<Item> ITEMS =
                DeferredRegister.create(ForgeRegistries.ITEMS, Airlocks.MODID);

        public static final RegistryObject<Item> CANVAS_FABRIC = ITEMS.register("canvas_fabric", () -> new Item(new Item.Properties()));

        public static final RegistryObject<Item> CONSOLE_CASE = ITEMS.register("console_case", () -> new Item(new Item.Properties()));

        public static void register(IEventBus eventBus) {
                ITEMS.register(eventBus);
        }
}
