package com.fantasticsource.faeruncharacters;

import com.fantasticsource.mctools.sound.BetterSoundEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.LinkedHashMap;

import static com.fantasticsource.faeruncharacters.FaerunCharacters.MODID;

public class CCSounds
{
    public static final ResourceLocation
            SUCCESS = new ResourceLocation(MODID, "success"),
            ERROR = new ResourceLocation(MODID, "error"),
            CLICK = new ResourceLocation(MODID, "click");


    @SubscribeEvent
    public static void soundEventRegistry(RegistryEvent.Register<SoundEvent> event)
    {
        IForgeRegistry<SoundEvent> registry = event.getRegistry();
        registry.register(new BetterSoundEvent(SUCCESS));
        registry.register(new BetterSoundEvent(ERROR));
        registry.register(new BetterSoundEvent(CLICK));


        for (LinkedHashMap<String, ResourceLocation> map : VoiceSets.ALL_VOICE_SETS.values())
        {
            for (ResourceLocation rl : map.values())
            {
                if (!registry.containsKey(rl)) registry.register(new BetterSoundEvent(rl));
            }
        }
    }
}
