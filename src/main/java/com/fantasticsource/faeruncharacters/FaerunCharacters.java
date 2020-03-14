package com.fantasticsource.faeruncharacters;

import com.fantasticsource.fantasticlib.api.FLibAPI;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.io.IOException;

@Mod(modid = FaerunCharacters.MODID, name = FaerunCharacters.NAME, version = FaerunCharacters.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.034a,);required-after:instances@[1.12.2-001b,);required-after:armourers_workshop@[1.12.2-0.49.1.527,)")
public class FaerunCharacters
{
    public static final String MODID = "faeruncharacters";
    public static final String NAME = "Faerun Characters";
    public static final String VERSION = "1.12.2.000";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) throws IOException
    {
        Network.init();

        FLibAPI.attachNBTCapToEntityIf(MODID, entity -> entity instanceof EntityLivingBase);
        MinecraftForge.EVENT_BUS.register(FaerunCharacters.class);
        Race.init(event);
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }


    @SubscribeEvent(priority = EventPriority.LOW)
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (!(event.player instanceof EntityPlayerMP)) return;

        CharacterCreation.validate((EntityPlayerMP) event.player);
    }
}
