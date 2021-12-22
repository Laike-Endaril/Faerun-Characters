package com.fantasticsource.faeruncharacters;

import com.fantasticsource.faeruncharacters.config.FaerunCharactersConfig;
import com.fantasticsource.faeruncharacters.nbt.CharacterTags;
import com.fantasticsource.instances.InstanceData;
import com.fantasticsource.instances.Instances;
import com.fantasticsource.instances.server.Teleport;
import com.fantasticsource.instances.world.InstanceHandler;
import com.fantasticsource.instances.world.dimensions.template.WorldProviderTemplate;
import com.fantasticsource.mctools.aw.RenderModes;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
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
        if (!hasValidCharacter(player)) goToCC(player);
    }


    public static boolean hasValidCharacter(EntityPlayerMP player)
    {
        if (PatreonHandler.isPlayerPremium(player)) return hasValidCharacter(player, true);

        if (hasValidCharacter(player, false)) return true;

        if (hasValidCharacter(player, true) && player.world.provider.getDimensionType() == CharacterCustomization.DIMTYPE_CHARACTER_CREATION)
        {
            player.sendMessage(new TextComponentString(TextFormatting.RED + "You have a premium option selected, but do not currently have premium status!"));
            player.sendMessage(new TextComponentString(TextFormatting.RED + "Please change any premium options to non-premium options, or leave the game and obtain premium status."));
            player.sendMessage(new TextComponentString(TextFormatting.RED + "If you believe you should already have premium status, please contact a server admin!"));
        }
        return false;
    }

    public static boolean hasValidCharacter(EntityPlayerMP player, boolean isPremium)
    {
        NBTTagCompound ccCompound = CharacterTags.getCC(player);

        if (isPremium) //Premium
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

            //Other
            if (race.voiceSets.size() > 0)
            {
                if (!ccCompound.hasKey("Voice") || !checkMultiHashSet(ccCompound.getString("Voice"), race.voiceSets, race.premiumVoiceSets)) return false;
                d = ccCompound.getDouble("Voice Pitch");
                if (d < race.pitchMin || d > race.pitchMax) return false;
            }
            else ccCompound.removeTag("Voice");
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

            //Other
            if (race.voiceSets.size() > 0)
            {
                if (!ccCompound.hasKey("Voice") || !checkMultiHashSet(ccCompound.getString("Voice"), race.voiceSets)) return false;
                d = ccCompound.getDouble("Voice Pitch");
                if (d < race.pitchMin || d > race.pitchMax) return false;
            }
            else ccCompound.removeTag("Voice");
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


    public static void goToCC(EntityPlayerMP player)
    {
        if (CRace.RACES.size() == 0)
        {
            System.err.println(TextFormatting.RED + "No races are set up; not sending " + player.getName() + " to character creation");
            return;
        }


        //Defaults (writing both notes for ctrl+f) also see CharacterTags.setRace()!
        //Set defaults (writing both notes for ctrl+f) also see CharacterTags.setRace()!
        if (RenderModes.getRenderMode(player, "Body") == null) RenderModes.setRenderMode(player, "Body", "M");
        if (RenderModes.getRenderMode(player, "Chest") == null) RenderModes.setRenderMode(player, "Chest", "Flat");

        if (!CharacterTags.getCC(player).hasKey("Race")) CharacterTags.setCCSkin(player, "Race", Tools.choose(CRace.RACES.keySet().toArray(new String[0])), false);


        //Get reference to existing CC instance, or create a new one
        InstanceData data = InstanceData.get(true, DIMTYPE_CHARACTER_CREATION, "Character_Creation");
        if (!data.exists())
        {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            InstanceHandler.loadOrCreateInstance(server, data.getFullName());
        }


        //Go to CC instance
        Teleport.joinTempCopy(player, data.getFullName());
        Teleport.teleport(player, player.dimension, player.posX, player.posY, player.posZ, 0, 0);
        Network.WRAPPER.sendTo(new Network.CharacterCustomizationGUIPacket(player), player);
    }
}
