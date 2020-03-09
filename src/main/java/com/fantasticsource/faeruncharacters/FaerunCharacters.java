package com.fantasticsource.faeruncharacters;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.FileNotFoundException;
import java.io.IOException;

@Mod(modid = FaerunCharacters.MODID, name = FaerunCharacters.NAME, version = FaerunCharacters.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.033b,);required-after:armourers_workshop@[1.12.2-0.49.1.527,)")
public class FaerunCharacters
{
    public static final String MODID = "faeruncharacters";
    public static final String NAME = "Faerun Characters";
    public static final String VERSION = "1.12.2.000";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) throws IOException
    {
        MinecraftForge.EVENT_BUS.register(FaerunCharacters.class);
        Race.init(event);
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }


//    @SubscribeEvent
//    public static void test(PlayerInteractEvent.EntityInteractSpecific event)
//    {
//        if (event.getSide() == Side.CLIENT || event.getHand() == EnumHand.OFF_HAND) return;
//
//        String renderMode = RenderModes.getRenderMode(event.getEntityPlayer(), "TestChannel");
//        if (renderMode == null || renderMode.equals("TestMode2")) RenderModes.setRenderMode(event.getEntityPlayer(), "TestChannel", "TestMode1");
//        else RenderModes.setRenderMode(event.getEntityPlayer(), "TestChannel", "TestMode2");
//    }
}
