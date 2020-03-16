package com.fantasticsource.faeruncharacters;

import com.fantasticsource.faeruncharacters.config.FaerunCharactersConfig;
import com.fantasticsource.fantasticlib.api.FLibAPI;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

@Mod(modid = FaerunCharacters.MODID, name = FaerunCharacters.NAME, version = FaerunCharacters.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.034a,);required-after:instances@[1.12.2-001b,);required-after:armourers_workshop@[1.12.2-0.49.1.527,)")
public class FaerunCharacters
{
    public static final String AW_SKIN_LIBRARY_DIR = MCTools.getConfigDir() + ".." + File.separator + "armourers_workshop" + File.separator + "skin-library" + File.separator;

    public static final String MODID = "faeruncharacters";
    public static final String NAME = "Faerun Characters";
    public static final String VERSION = "1.12.2.000";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) throws IOException
    {
        Network.init();

        FLibAPI.attachNBTCapToEntityIf(MODID, entity -> entity instanceof EntityLivingBase);
        MinecraftForge.EVENT_BUS.register(FaerunCharacters.class);
        CRace.init(event);
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (!event.getModID().equals(MODID)) return;


        ConfigManager.sync(MODID, Config.Type.INSTANCE);

        FaerunCharactersConfig.server.bareArmSkinSet.clear();
        loadSkins(FaerunCharactersConfig.server.bareArmSkinSet, FaerunCharactersConfig.server.bareArms);
        FaerunCharactersConfig.server.markingsSet.clear();
        loadSkins(FaerunCharactersConfig.server.markingsSet, FaerunCharactersConfig.server.markings);
        FaerunCharactersConfig.server.headAccessorySet.clear();
        loadSkins(FaerunCharactersConfig.server.headAccessorySet, FaerunCharactersConfig.server.headAccessories);
        FaerunCharactersConfig.server.faceAccessorySet.clear();
        loadSkins(FaerunCharactersConfig.server.faceAccessorySet, FaerunCharactersConfig.server.faceAccessories);
    }

    @SubscribeEvent
    public static void applyConfig(ConfigChangedEvent.PostConfigChangedEvent event)
    {
        if (!event.getModID().equals(MODID)) return;

        CharacterCustomizationGUI.activeButtonColor = new Color(FaerunCharactersConfig.client.activeButtonColor, true);
        CharacterCustomizationGUI.hoverButtonColor = new Color(FaerunCharactersConfig.client.hoverButtonColor, true);
        CharacterCustomizationGUI.idleButtonColor = new Color(FaerunCharactersConfig.client.idleButtonColor, true);
        CharacterCustomizationGUI.activeButtonColor = new Color(FaerunCharactersConfig.client.activePremiumButtonColor, true);
        CharacterCustomizationGUI.hoverButtonColor = new Color(FaerunCharactersConfig.client.hoverPremiumButtonColor, true);
        CharacterCustomizationGUI.idleButtonColor = new Color(FaerunCharactersConfig.client.idlePremiumButtonColor, true);

        FaerunCharactersConfig.server.bareArmSkinSet.clear();
        loadSkins(FaerunCharactersConfig.server.bareArmSkinSet, FaerunCharactersConfig.server.bareArms);
        FaerunCharactersConfig.server.markingsSet.clear();
        loadSkins(FaerunCharactersConfig.server.markingsSet, FaerunCharactersConfig.server.markings);
        FaerunCharactersConfig.server.headAccessorySet.clear();
        loadSkins(FaerunCharactersConfig.server.headAccessorySet, FaerunCharactersConfig.server.headAccessories);
        FaerunCharactersConfig.server.faceAccessorySet.clear();
        loadSkins(FaerunCharactersConfig.server.faceAccessorySet, FaerunCharactersConfig.server.faceAccessories);
    }


    @SubscribeEvent(priority = EventPriority.LOW)
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (!(event.player instanceof EntityPlayerMP)) return;

        CharacterCustomization.validate((EntityPlayerMP) event.player);
    }


    public static void loadSkins(HashSet<String> skinSet, String[] skinStrings)
    {
        boolean pool;
        for (String skinString : skinStrings)
        {
            if (skinString.substring(0, 7).equals("folder:"))
            {
                pool = true;
                skinString = skinString.replace("folder:", "");
            }
            else pool = false;

            skinString = Tools.fixFileSeparators(skinString.trim());

            File skinFile = new File(AW_SKIN_LIBRARY_DIR + skinString);

            if (!pool)
            {
                if (!skinFile.exists()) skinFile = new File(AW_SKIN_LIBRARY_DIR + skinString + ".armour");

                if (!skinFile.exists()) continue;

                skinSet.add(skinString);
            }
            else
            {
                if (!skinFile.isDirectory()) continue;

                File[] subFiles = skinFile.listFiles();
                if (subFiles == null || subFiles.length == 0) continue;


                String[] subSkinStrings = new String[subFiles.length];
                int i = 0;
                for (File subFile : subFiles)
                {
                    subSkinStrings[i++] = subFile.getAbsolutePath().replace(AW_SKIN_LIBRARY_DIR, "").replace(".armour", "");
                }

                loadSkins(skinSet, subSkinStrings);
            }
        }
    }
}
