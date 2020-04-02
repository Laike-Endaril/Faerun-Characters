package com.fantasticsource.faeruncharacters;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import static com.fantasticsource.faeruncharacters.FaerunCharacters.MODID;

public class GUISounds
{
    public static final ResourceLocation
            SUCCESS = new ResourceLocation(MODID, "success"),
            ERROR = new ResourceLocation(MODID, "error"),
            CLICK = new ResourceLocation(MODID, "click");


    @SubscribeEvent
    public static void soundEventRegistry(RegistryEvent.Register<SoundEvent> event)
    {
        IForgeRegistry<SoundEvent> registry = event.getRegistry();
        registry.register(new SoundEvent(SUCCESS).setRegistryName(SUCCESS));
        registry.register(new SoundEvent(ERROR).setRegistryName(ERROR));
        registry.register(new SoundEvent(CLICK).setRegistryName(CLICK));
    }
}
