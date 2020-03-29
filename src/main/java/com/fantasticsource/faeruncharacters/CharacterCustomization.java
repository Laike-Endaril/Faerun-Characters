package com.fantasticsource.faeruncharacters;

import com.fantasticsource.faeruncharacters.config.FaerunCharactersConfig;
import com.fantasticsource.faeruncharacters.nbt.CharacterTags;
import com.fantasticsource.instances.InstanceData;
import com.fantasticsource.instances.Instances;
import com.fantasticsource.instances.server.Teleport;
import com.fantasticsource.instances.world.InstanceHandler;
import com.fantasticsource.instances.world.dimensions.template.WorldProviderTemplate;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.aw.RenderModes;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.LinkedHashSet;

public class CharacterCustomization
{
    public static final DimensionType DIMTYPE_CHARACTER_CREATION = DimensionType.register("Character_Creation", "_character_creation", Instances.nextFreeDimTypeID(), WorldProviderTemplate.class, false);

    public static void init()
    {
        //Indirectly initializes the field above
    }


    public static void validate(EntityPlayerMP player)
    {
        if (!hasValidCharacter(player)) go(player);
    }


    public static boolean hasValidCharacter(EntityPlayerMP player)
    {
        NBTTagCompound ccCompound = CharacterTags.getCC(player);

        if (MCTools.isWhitelisted(player)) //Premium
        {
            //Body
            String s = ccCompound.getString("Race");
            CRace race = CRace.RACES.get(s);
            if (race == null) race = CRace.RACES_PREMIUM.get(s);
            if (race == null) return false;
            if (!checkMultiHashSet(ccCompound.getString("Race Variant"), race.raceVariants, race.premiumRaceVariants)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Tail"), race.tails)) return false;
            if (ccCompound.hasKey("Bare Arms") && !checkMultiHashSet(ccCompound.getString("Bare Arms"), FaerunCharactersConfig.server.bareArmSkinSet)) return false;
            if (!checkMultiHashSet(new Color(ccCompound.getInteger("Skin Color")), race.skinColors)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Default Torso"), race.defaultTorsos)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Default Chest"), race.defaultChests)) return false;
            if (RenderModes.getRenderMode(player, "Body") == null) return false;
            if (RenderModes.getRenderMode(player, "Chest") == null) return false;
            if (RenderModes.getRenderMode(player, "CapeInv") == null) return false;
            if (!checkMultiHashSet(RenderModes.getRenderMode(player, "Chest"), race.chestSizes)) return false;
            double d = ccCompound.getDouble("Scale");
            if (d < race.renderScaleMin || d > race.renderScaleMax) return false;


            //Head
            if (ccCompound.hasKey("Hair (Base)") && !checkMultiHashSet(ccCompound.getString("Hair (Base)"), race.hairBase, race.premiumHairBase)) return false;
            if (ccCompound.hasKey("Hair (Front)") && !checkMultiHashSet(ccCompound.getString("Hair (Front)"), race.hairFront, race.premiumHairFront)) return false;
            if (ccCompound.hasKey("Hair (Back)") && !checkMultiHashSet(ccCompound.getString("Hair (Back)"), race.hairBack, race.premiumHairBack)) return false;
            if (ccCompound.hasKey("Hair (Top/Overall 1)") && !checkMultiHashSet(ccCompound.getString("Hair (Top/Overall 1)"), race.hairTop, race.premiumHairTop)) return false;
            if (ccCompound.hasKey("Hair (Top/Overall 2)") && !checkMultiHashSet(ccCompound.getString("Hair (Top/Overall 2)"), race.hairTop, race.premiumHairTop)) return false;
            if (!checkMultiHashSet(new Color(ccCompound.getInteger("Hair Color")), race.hairColors)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Eyes"), race.eyes, race.premiumEyes)) return false;
            if (!checkMultiHashSet(new Color(ccCompound.getInteger("Eye Color")), race.eyeColors)) return false;


            //Accessories
            if (ccCompound.hasKey("Markings") && !checkMultiHashSet(ccCompound.getString("Markings"), FaerunCharactersConfig.server.markingsSet)) return false;
            if (ccCompound.hasKey("Accessory (Head)") && !checkMultiHashSet(ccCompound.getString("Accessory (Head)"), FaerunCharactersConfig.server.headAccessorySet)) return false;
            if (ccCompound.hasKey("Accessory (Face)") && !checkMultiHashSet(ccCompound.getString("Accessory (Face)"), FaerunCharactersConfig.server.faceAccessorySet)) return false;
            if (!ccCompound.hasKey("Color 1") || !ccCompound.hasKey("Color 2")) return false;
        }
        else //Non-premium
        {
            //Body
            String s = ccCompound.getString("Race");
            CRace race = CRace.RACES.get(s);
            if (race == null) return false;
            if (!checkMultiHashSet(ccCompound.getString("Race Variant"), race.raceVariants)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Tail"), race.tails)) return false;
            if (ccCompound.hasKey("Bare Arms") && !checkMultiHashSet(ccCompound.getString("Bare Arms"), FaerunCharactersConfig.server.bareArmSkinSet)) return false;
            if (!checkMultiHashSet(new Color(ccCompound.getInteger("Skin Color")), race.skinColors)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Default Torso"), race.defaultTorsos)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Default Chest"), race.defaultChests)) return false;
            if (RenderModes.getRenderMode(player, "Body") == null) return false;
            if (RenderModes.getRenderMode(player, "Chest") == null) return false;
            if (RenderModes.getRenderMode(player, "CapeInv") == null) return false;
            if (!checkMultiHashSet(RenderModes.getRenderMode(player, "Chest"), race.chestSizes)) return false;
            double d = ccCompound.getDouble("Scale");
            if (d < race.renderScaleMin || d > race.renderScaleMax) return false;


            //Head
            if (ccCompound.hasKey("Hair (Base)") && !checkMultiHashSet(ccCompound.getString("Hair (Base)"), race.hairBase)) return false;
            if (ccCompound.hasKey("Hair (Front)") && !checkMultiHashSet(ccCompound.getString("Hair (Front)"), race.hairFront)) return false;
            if (ccCompound.hasKey("Hair (Back)") && !checkMultiHashSet(ccCompound.getString("Hair (Back)"), race.hairBack)) return false;
            if (ccCompound.hasKey("Hair (Top/Overall 1)") && !checkMultiHashSet(ccCompound.getString("Hair (Top/Overall 1)"), race.hairTop)) return false;
            if (ccCompound.hasKey("Hair (Top/Overall 2)") && !checkMultiHashSet(ccCompound.getString("Hair (Top/Overall 2)"), race.hairTop)) return false;
            if (!checkMultiHashSet(new Color(ccCompound.getInteger("Hair Color")), race.hairColors)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Eyes"), race.eyes)) return false;
            if (!checkMultiHashSet(new Color(ccCompound.getInteger("Eye Color")), race.eyeColors)) return false;


            //Accessories
            if (ccCompound.hasKey("Markings") && !checkMultiHashSet(ccCompound.getString("Markings"), FaerunCharactersConfig.server.markingsSet)) return false;
            if (ccCompound.hasKey("Accessory (Head)") && !checkMultiHashSet(ccCompound.getString("Accessory (Head)"), FaerunCharactersConfig.server.headAccessorySet)) return false;
            if (ccCompound.hasKey("Accessory (Face)") && !checkMultiHashSet(ccCompound.getString("Accessory (Face)"), FaerunCharactersConfig.server.faceAccessorySet)) return false;
            if (!ccCompound.hasKey("Color 1") || !ccCompound.hasKey("Color 2")) return false;
        }

        return true;
    }


    protected static <T> boolean checkMultiHashSet(T value, LinkedHashSet<T>... sets)
    {
        boolean noChoices = true;
        for (LinkedHashSet<T> set : sets)
        {
            if (set == null || set.size() == 0) continue;

            if (set.contains(value)) return true;
            noChoices = false;
        }

        return noChoices;
    }


    public static void go(EntityPlayerMP player)
    {
        if (RenderModes.getRenderMode(player, "Body") == null) RenderModes.setRenderMode(player, "Body", "M");
        if (RenderModes.getRenderMode(player, "Chest") == null) RenderModes.setRenderMode(player, "Chest", "Flat");
        if (RenderModes.getRenderMode(player, "CapeInv") == null) RenderModes.setRenderMode(player, "CapeInv", "Off");


        if (!CharacterTags.getCC(player).hasKey("Race")) CharacterTags.setCCSkin(player, "Race", Tools.choose(CRace.RACES.keySet().toArray(new String[0])));


        InstanceData data = InstanceData.get(true, DIMTYPE_CHARACTER_CREATION, "Character_Creation");
        if (!data.exists())
        {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            InstanceHandler.loadOrCreateInstance(server, data.getFullName());
        }


        Teleport.joinTempCopy(player, data.getFullName());
        Teleport.teleport(player, player.dimension, player.posX, player.posY, player.posZ, 0, 0);
        Network.WRAPPER.sendTo(new Network.CharacterCustomizationGUIPacket(player), player);
    }
}
