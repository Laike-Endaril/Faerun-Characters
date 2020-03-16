package com.fantasticsource.faeruncharacters;

import com.fantasticsource.faeruncharacters.config.FaerunCharactersConfig;
import com.fantasticsource.faeruncharacters.nbt.CharacterTags;
import com.fantasticsource.instances.InstanceData;
import com.fantasticsource.instances.Instances;
import com.fantasticsource.instances.server.Teleport;
import com.fantasticsource.instances.world.dimensions.template.WorldProviderTemplate;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.aw.RenderModes;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;

import java.util.HashSet;

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
            if (!Tools.contains(FaerunCharactersConfig.server.bareArms, ccCompound.getString("Bare Arms"))) return false;
            if (!checkMultiHashSet(new Color(ccCompound.getInteger("Skin Color")), race.skinColors)) return false;
            if (RenderModes.getRenderMode(player, "Body") == null) return false;
            if (RenderModes.getRenderMode(player, "Chest") == null) return false;
            if (!checkMultiHashSet(ccCompound.getString("Chest"), race.chestSizes)) return false;
            double d = ccCompound.getDouble("Scale");
            if (d < race.renderScaleMin || d > race.renderScaleMax) return false;


            //Head
            if (!checkMultiHashSet(ccCompound.getString("Hair (Base)"), race.hairBase, race.premiumHairBase)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Hair (Front)"), race.hairFront, race.premiumHairFront)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Hair (Back)"), race.hairBack, race.premiumHairBack)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Hair (Top/Overall 1)"), race.hairTop, race.premiumHairTop)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Hair (Top/Overall 2)"), race.hairTop, race.premiumHairTop)) return false;
            if (!checkMultiHashSet(new Color(ccCompound.getInteger("Hair Color")), race.hairColors)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Eyes"), race.eyes, race.premiumEyes)) return false;
            if (!checkMultiHashSet(new Color(ccCompound.getInteger("Eye Color")), race.eyeColors)) return false;


            //Accessories
            if (!checkMultiHashSet(ccCompound.getString("Markings"), FaerunCharactersConfig.server.bareArmSkinSet)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Accessory (Head)"), FaerunCharactersConfig.server.headAccessorySet)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Accessory (Face)"), FaerunCharactersConfig.server.faceAccessorySet)) return false;
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
            if (!Tools.contains(FaerunCharactersConfig.server.bareArms, ccCompound.getString("Bare Arms"))) return false;
            if (!checkMultiHashSet(new Color(ccCompound.getInteger("Skin Color")), race.skinColors)) return false;
            if (RenderModes.getRenderMode(player, "Body") == null) return false;
            if (RenderModes.getRenderMode(player, "Chest") == null) return false;
            if (!checkMultiHashSet(ccCompound.getString("Chest"), race.chestSizes)) return false;
            double d = ccCompound.getDouble("Scale");
            if (d < race.renderScaleMin || d > race.renderScaleMax) return false;


            //Head
            if (!checkMultiHashSet(ccCompound.getString("Hair (Base)"), race.hairBase)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Hair (Front)"), race.hairFront)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Hair (Back)"), race.hairBack)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Hair (Top/Overall 1)"), race.hairTop)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Hair (Top/Overall 2)"), race.hairTop)) return false;
            if (!checkMultiHashSet(new Color(ccCompound.getInteger("Hair Color")), race.hairColors)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Eyes"), race.eyes)) return false;
            if (!checkMultiHashSet(new Color(ccCompound.getInteger("Eye Color")), race.eyeColors)) return false;


            //Accessories
            if (!checkMultiHashSet(ccCompound.getString("Markings"), FaerunCharactersConfig.server.bareArmSkinSet)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Accessory (Head)"), FaerunCharactersConfig.server.headAccessorySet)) return false;
            if (!checkMultiHashSet(ccCompound.getString("Accessory (Face)"), FaerunCharactersConfig.server.faceAccessorySet)) return false;
            if (!ccCompound.hasKey("Color 1") || !ccCompound.hasKey("Color 2")) return false;
        }

        return true;
    }


    protected static <T> boolean checkMultiHashSet(T value, HashSet<T>... sets)
    {
        boolean noChoices = true;
        for (HashSet<T> set : sets)
        {
            if (set == null || set.size() == 0) continue;

            if (set.contains(value)) return true;
            noChoices = false;
        }

        return noChoices;
    }


    public static void go(EntityPlayerMP player)
    {
        InstanceData data = InstanceData.get(true, DIMTYPE_CHARACTER_CREATION, "Character_Creation");
        if (!data.exists())
        {
            System.err.println(TextFormatting.RED + "No character creation instance was found!  Need a character creation instance saved to '" + data.getFullName() + "'");
            return;
        }


        Teleport.joinTempCopy(player, data.getFullName());
        Network.WRAPPER.sendTo(new Network.CharacterCustomizationGUIPacket(), player);
    }
}
