package com.fantasticsource.faeruncharacters;

import com.fantasticsource.faeruncharacters.blocksanditems.BlocksAndItems;
import com.fantasticsource.faeruncharacters.config.FaerunCharactersConfig;
import com.fantasticsource.faeruncharacters.gui.CharacterCustomizationGUI;
import com.fantasticsource.fantasticlib.api.FLibAPI;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Table;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

@Mod(modid = FaerunCharacters.MODID, name = FaerunCharacters.NAME, version = FaerunCharacters.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.043h,);required-after:instances@[1.12.2-001b,);required-after:tiamatinventory@[1.12.2-000q,);required-after:armourers_workshop@[1.12.2-0.50.5.636,)")
public class FaerunCharacters
{
    public static final String AW_SKIN_LIBRARY_DIR = MCTools.getConfigDir() + ".." + File.separator + "armourers_workshop" + File.separator + "skin-library" + File.separator;

    public static final String MODID = "faeruncharacters";
    public static final String NAME = "Faerun Characters";
    public static final String VERSION = "1.12.2.000s";

    public static final Class MCLINK_API_CLASS = ReflectionTool.getClassByName("net.dries007.mclink.api.API"), MCLINK_AUTHENTICATION_CLASS = ReflectionTool.getClassByName("net.dries007.mclink.api.Authentication");
    public static final Method MCLINK_API_GET_AUTHORIZATION_METHOD = MCLINK_API_CLASS == null ? null : ReflectionTool.getMethod(MCLINK_API_CLASS, new Class[]{Table.class, UUID[].class}, "getAuthorization");
    public static final Field MCLINK_AUTHENTICATION_EXTRA_FIELD = MCLINK_AUTHENTICATION_CLASS == null ? null : ReflectionTool.getField(MCLINK_AUTHENTICATION_CLASS, "extra");

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) throws IOException
    {
        Network.init();
        CharacterCustomization.init();

        FLibAPI.attachNBTCapToEntityIf(MODID, entity -> entity instanceof EntityLivingBase);
        MinecraftForge.EVENT_BUS.register(FaerunCharacters.class);
        VoiceSets.init(event);
        CRace.init(event);

        MinecraftForge.EVENT_BUS.register(BlocksAndItems.class);

        updateGlobalOptions();

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            MinecraftForge.EVENT_BUS.register(CCSounds.class);
            MinecraftForge.EVENT_BUS.register(CharacterCustomizationGUI.class);
        }
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (!event.getModID().equals(MODID)) return;

        ConfigManager.sync(MODID, Config.Type.INSTANCE);
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


        updateGlobalOptions();
    }


    protected static void updateGlobalOptions()
    {
        FaerunCharactersConfig.server.bareArmSkinSet.clear();
        loadSkinNames(FaerunCharactersConfig.server.bareArmSkinSet, FaerunCharactersConfig.server.bareArms, true);
        FaerunCharactersConfig.server.markingsSet.clear();
        loadSkinNames(FaerunCharactersConfig.server.markingsSet, FaerunCharactersConfig.server.markings, true);
        FaerunCharactersConfig.server.headAccessorySet.clear();
        loadSkinNames(FaerunCharactersConfig.server.headAccessorySet, FaerunCharactersConfig.server.headAccessories, true);
        FaerunCharactersConfig.server.faceAccessorySet.clear();
        loadSkinNames(FaerunCharactersConfig.server.faceAccessorySet, FaerunCharactersConfig.server.faceAccessories, true);
    }


    @SubscribeEvent(priority = EventPriority.LOW)
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (!(event.player instanceof EntityPlayerMP)) return;


        EntityPlayerMP player = (EntityPlayerMP) event.player;

        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        String name = player.getName();
        server.commandManager.executeCommand(server, "/armourers wardrobe set_unlocked_slots " + name + " armourers:head 10");
        server.commandManager.executeCommand(server, "/armourers wardrobe set_unlocked_slots " + name + " armourers:chest 10");
        server.commandManager.executeCommand(server, "/armourers wardrobe set_unlocked_slots " + name + " armourers:legs 10");
        server.commandManager.executeCommand(server, "/armourers wardrobe set_unlocked_slots " + name + " armourers:feet 10");
        server.commandManager.executeCommand(server, "/armourers wardrobe set_unlocked_slots " + name + " armourers:wings 10");
        CharacterCustomization.validate(player);
    }

    public static void loadSkinNames(LinkedHashSet<String> skinSet, String[] skinStrings, boolean addNoneOption)
    {
        if (addNoneOption) skinSet.add(FaerunCharactersConfig.server.noneFolder);

        boolean pool;
        for (String skinString : skinStrings)
        {
            skinString = skinString.trim();

            if (skinString.equals("")) continue;

            if (skinString.length() >= 7 && skinString.substring(0, 7).equals("folder:"))
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
                if (Tools.fixFileSeparators(skinString).equals(Tools.fixFileSeparators(FaerunCharactersConfig.server.noneFolder))) continue;

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

                loadSkinNames(skinSet, subSkinStrings, false);
            }
        }
    }

    @SubscribeEvent
    public static void renderExpBar(RenderGameOverlayEvent.Pre event)
    {
        if (Minecraft.getMinecraft().world.provider.getDimensionType() == CharacterCustomization.DIMTYPE_CHARACTER_CREATION) event.setCanceled(true);
    }

    @Mod.EventHandler
    public static void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new Commands());
    }


    public static int getPlayerPatreonCents(EntityPlayerMP player)
    {
        if (!Loader.isModLoaded("mclink")) return 0;

        //Used fully qualified references to prevent crash from import on client-side due to missing MCLink mod
        try
        {
            Table<String, String, List<String>> table = HashBasedTable.create();
            ArrayList<String> list = new ArrayList<>();
            list.add("0");
            table.put(FaerunCharactersConfig.server.patreonCreatorAccessToken, "Patreon", list);
            ImmutableMultimap map = (ImmutableMultimap) ReflectionTool.invoke(MCLINK_API_GET_AUTHORIZATION_METHOD, null, table, new UUID[]{player.getPersistentID()});
            if (map.isEmpty()) return 0;

            ImmutableMap<String, String> extra = (ImmutableMap<String, String>) ReflectionTool.get(MCLINK_AUTHENTICATION_EXTRA_FIELD, map.values().iterator().next());
            return Integer.parseInt(extra.values().iterator().next());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean isPlayerPremium(EntityPlayerMP player)
    {
        return getPlayerPatreonCents(player) >= 100;
    }
}
