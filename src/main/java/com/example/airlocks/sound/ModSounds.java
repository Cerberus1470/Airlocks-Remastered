package com.example.airlocks.sound;

import com.example.airlocks.Airlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
        public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Airlocks.MODID);

        public static final RegistryObject<SoundEvent> BLOCK_CONSOLE_KEYPRESS = registerSoundEvent("block_console_keypress");
        public static final RegistryObject<SoundEvent> BLOCK_CONSOLE_PRESSURIZING = registerSoundEvent("block_console_pressurizing");
        public static final RegistryObject<SoundEvent> BLOCK_CONSOLE_COMPLETE = registerSoundEvent("block_console_complete");
        public static final RegistryObject<SoundEvent> BLOCK_DOOR_OPEN = registerSoundEvent("block_door_open");
        public static final RegistryObject<SoundEvent> BLOCK_DOOR_CLOSE = registerSoundEvent("block_door_close");

        private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
                return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Airlocks.MODID, name)));
        }

        public static void register(IEventBus eventBus) {
                SOUND_EVENTS.register(eventBus);
        }
}
