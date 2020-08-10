package com.fantasticsource.faeruncharacters.nbt;

import com.fantasticsource.faeruncharacters.CRace;
import com.fantasticsource.faeruncharacters.Network;
import com.fantasticsource.faeruncharacters.config.FaerunCharactersConfig;
import com.fantasticsource.fantasticlib.api.FLibAPI;
import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.aw.AWSkinGenerator;
import com.fantasticsource.mctools.aw.RenderModes;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.LinkedHashMap;

import static com.fantasticsource.faeruncharacters.FaerunCharacters.MODID;

public class CharacterTags
{
    protected static final LinkedHashMap<String, SkinSlotting> SKIN_SLOTTINGS = new LinkedHashMap<>();
    protected static final LinkedHashMap<String, String> COLOR_KEYS = new LinkedHashMap<>();

    static
    {
        SKIN_SLOTTINGS.put("Markings", new SkinSlotting("Markings", "armourers:head", 0));
        SKIN_SLOTTINGS.put("Hair (Base)", new SkinSlotting("Hair (Base)", "armourers:head", 1));
        SKIN_SLOTTINGS.put("Eyes", new SkinSlotting("Eyes", "armourers:head", 2));
        SKIN_SLOTTINGS.put("Hair (Front)", new SkinSlotting("Hair (Front)", "armourers:head", 3));
        SKIN_SLOTTINGS.put("Hair (Back)", new SkinSlotting("Hair (Back)", "armourers:head", 4));
        SKIN_SLOTTINGS.put("Hair (Top/Overall 1)", new SkinSlotting("Hair (Top/Overall 1)", "armourers:head", 5));
        SKIN_SLOTTINGS.put("Hair (Top/Overall 2)", new SkinSlotting("Hair (Top/Overall 2)", "armourers:head", 6));
        SKIN_SLOTTINGS.put("Accessory (Head)", new SkinSlotting("Accessory (Head)", "armourers:head", 7));
        SKIN_SLOTTINGS.put("Accessory (Face)", new SkinSlotting("Accessory (Face)", "armourers:head", 8));

        SKIN_SLOTTINGS.put("Default Torso", new SkinSlotting("Default Torso", "armourers:chest", 0));
        SKIN_SLOTTINGS.put("Bare Arms", new SkinSlotting("Bare Arms", "armourers:chest", 2));

        SKIN_SLOTTINGS.put("Default Chest", new SkinSlotting("Default Chest", "armourers:wings", 0));
        SKIN_SLOTTINGS.put("Tail", new SkinSlotting("Tail", "armourers:wings", 6));

        SKIN_SLOTTINGS.put("Race Variant", new SkinSlotting("Race Variant", "armourers:outfit", 0));


        COLOR_KEYS.put("Skin Color", "skin");
        COLOR_KEYS.put("Eye Color", "eye");
        COLOR_KEYS.put("Hair Color", "hair");
        COLOR_KEYS.put("Color 1", "misc_1");
        COLOR_KEYS.put("Color 2", "misc_2");
        COLOR_KEYS.put("Undershirt Color", "misc_3");
    }

    public static void setCC(EntityLivingBase livingBase, NBTTagCompound characterCustomization)
    {
        FLibAPI.getNBTCap(livingBase).getCompound(MODID).setTag("CC", characterCustomization);
    }

    public static NBTTagCompound getCC(EntityLivingBase livingBase)
    {
        NBTTagCompound compound = FLibAPI.getNBTCap(livingBase).getCompound(MODID);
        if (!compound.hasKey("CC")) compound.setTag("CC", new NBTTagCompound());
        return compound.getCompoundTag("CC");
    }


    public static void setCCColor(EntityLivingBase livingBase, String key, Color value)
    {
        //These are wardrobe color channels, and should always have a paint type of 255 (alpha channel used for paint type)
        String cmdKey = COLOR_KEYS.get(key);
        if (cmdKey == null)
        {
            System.err.println(TextFormatting.RED + "Tried to set invalid color key (" + key + ")");
            System.err.println(TextFormatting.RED + "...for entity..." + livingBase.getName() + " (" + livingBase.getClass().getName() + ")");
            return;
        }

        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        int cmdColor = (value.r() << 16) | (value.g() << 8) | value.b();
        String cmdColorHex = Integer.toHexString(cmdColor);
        StringBuilder cmdHex = new StringBuilder("#");
        for (int i = 6 - cmdColorHex.length(); i > 0; i--) cmdHex.append("0");
        cmdHex.append(cmdColorHex);

        if (key.equals("Hair Color") || key.equals("Skin Color"))
        {
            String raceString = getCC(livingBase).getString("Race");
            CRace race = CRace.RACES.get(raceString);
            if (race == null) race = CRace.RACES_PREMIUM.get(raceString);
            if (race == null)
            {
                System.err.println(TextFormatting.RED + "Tried to set " + key + " without race, or couldn't find race (Race = " + raceString + ")");
                System.err.println(TextFormatting.RED + "...for entity..." + livingBase.getName() + " (" + livingBase.getClass().getName() + ")");
                return;
            }

            if (race.skinColorSetsHairColor)
            {
                if (key.equals("Skin Color"))
                {
                    server.commandManager.executeCommand(server, "/armourers wardrobe setColour " + livingBase.getName() + " " + COLOR_KEYS.get("Hair Color") + " " + cmdHex);
                    getCC(livingBase).setInteger("Hair Color", value.color());
                }
                else
                {
                    server.commandManager.executeCommand(server, "/armourers wardrobe setColour " + livingBase.getName() + " " + COLOR_KEYS.get("Skin Color") + " " + cmdHex);
                    getCC(livingBase).setInteger("Skin Color", value.color());
                }
            }
        }

        server.commandManager.executeCommand(server, "/armourers wardrobe setColour " + livingBase.getName() + " " + cmdKey + " " + cmdHex);
        getCC(livingBase).setInteger(key, value.color());
    }

    public static void setCCSkin(EntityLivingBase livingBase, String key, String value, boolean refresh)
    {
        if (value != null) value = value.trim();
        if (value == null || value.equals(FaerunCharactersConfig.server.noneFolder))
        {
            value = FaerunCharactersConfig.server.noneFolder;
        }

        if (key.equals("Race"))
        {
            setRace(livingBase, value, refresh);
            return;
        }


        SkinSlotting skinSlotting = SKIN_SLOTTINGS.get(key);
        if (skinSlotting == null)
        {
            System.err.println(TextFormatting.RED + "Tried to set invalid slotting (" + key + ") to " + value);
            System.err.println(TextFormatting.RED + "...for entity..." + livingBase.getName() + " (" + livingBase.getClass().getName() + ")");
            return;
        }


        ItemStack oldSkin = GlobalInventory.getAWSkin(livingBase, skinSlotting.skinType, skinSlotting.indexWithinType);

        if (oldSkin != null && !oldSkin.isEmpty() && !skinSlotting.name.equals(getCCStackTag(oldSkin)))
        {
            switch (skinSlotting.name)
            {
                case "Default Torso":
                    //Prevent error from default torso, because this will happen anytime they have a normal chest armor on
                    break;

                default:
                    System.err.println(TextFormatting.RED + "Tried to set CC skin in wardrobe slot already filled with non-CC skin!");
                    System.err.println(TextFormatting.RED + "Entity: " + livingBase.getName() + " (" + livingBase.getClass().getName() + ")");
                    System.err.println(TextFormatting.RED + "Slot; " + skinSlotting.skinType + " #" + skinSlotting.indexWithinType);
                    System.err.println(TextFormatting.RED + "Existing item in slot: " + oldSkin.getDisplayName());
            }
        }
        else
        {
            ItemStack newSkin;
            if (Tools.fixFileSeparators(value).equals(Tools.fixFileSeparators(FaerunCharactersConfig.server.noneFolder)))
            {
                newSkin = ItemStack.EMPTY;
            }
            else newSkin = AWSkinGenerator.generate(value, skinSlotting.skinType);

            if (!newSkin.isEmpty()) applyCCStackTag(newSkin, skinSlotting.name);

            GlobalInventory.setAWSkin(livingBase, skinSlotting.skinType, skinSlotting.indexWithinType, newSkin);
            GlobalInventory.syncAWWardrobeSkins(livingBase, true, true);
        }


        getCC(livingBase).setString(key, value);
    }


    protected static String getCCStackTag(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return null;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(MODID)) return null;

        compound = compound.getCompoundTag(MODID);
        if (!compound.hasKey("CC")) return null;

        return compound.getString("CC");
    }

    protected static void applyCCStackTag(ItemStack stack, String skinSlottingName)
    {
        //Mark as CC skin
        NBTTagCompound compound = stack.getTagCompound();

        compound.setTag(MODID, new NBTTagCompound());
        compound = compound.getCompoundTag(MODID);

        compound.setString("CC", skinSlottingName);
    }


    protected static void setRace(EntityLivingBase livingBase, String raceName, boolean refresh)
    {
        CRace race = CRace.RACES.get(raceName);
        if (race == null) race = CRace.RACES_PREMIUM.get(raceName);
        if (race == null)
        {
            System.err.println(TextFormatting.RED + "Tried to set race to non-existing race: (" + raceName + ")");
            System.err.println(TextFormatting.RED + "...for entity..." + livingBase.getName() + " (" + livingBase.getClass().getName() + ")");
            return;
        }


        NBTTagCompound ccCompound = getCC(livingBase);
        boolean noPreviousRace = !ccCompound.hasKey("Race");


        //Body

        String key = "Race";
        String value = raceName;
        ccCompound.setString(key, value);

        key = "Race Variant";
        value = race.raceVariants.size() > 0 ? Tools.choose(race.raceVariants.toArray(new String[0])) : Tools.choose(race.premiumRaceVariants.toArray(new String[0]));
        setCCSkin(livingBase, key, value, false);

        key = "Tail";
        value = race.tails.size() > 0 ? Tools.choose(race.tails.toArray(new String[0])) : FaerunCharactersConfig.server.noneFolder;
        setCCSkin(livingBase, key, value, false);

        key = "Bare Arms";
        value = Tools.choose(FaerunCharactersConfig.server.bareArmSkinSet.toArray(new String[0]));
        setCCSkin(livingBase, key, value, false);

        key = "Skin Color";
        Color color = ccCompound.hasKey(key) ? new Color(ccCompound.getInteger(key)) : new Color(Tools.random(256), Tools.random(256), Tools.random(256), 255);
        if (race.skinColors != null && !race.skinColors.contains(color))
        {
            color = Tools.choose(race.skinColors.toArray(new Color[0]));
        }
        setCCColor(livingBase, key, color);

        key = "Default Torso";
        value = ccCompound.getString(key);
        if (!race.defaultTorsos.contains(value))
        {
            value = Tools.choose(race.defaultTorsos.toArray(new String[0]));
            setCCSkin(livingBase, key, value, false);
        }

        key = "Default Chest";
        value = ccCompound.getString(key);
        if (!race.defaultChests.contains(value))
        {
            value = Tools.choose(race.defaultChests.toArray(new String[0]));
            setCCSkin(livingBase, key, value, false);
        }

        key = "Chest";
        value = RenderModes.getRenderMode(livingBase, key);
        if (!race.chestSizes.contains(value))
        {
            value = Tools.choose(race.chestSizes.toArray(new String[0]));
            RenderModes.setRenderMode(livingBase, key, value);
        }

        if (noPreviousRace) ccCompound.setDouble("Scale", (race.renderScaleMin + race.renderScaleMax) / 2);
        else ccCompound.setDouble("Scale", Tools.min(Tools.max(ccCompound.getDouble("Scale"), race.renderScaleMin), race.renderScaleMax));

        key = "Undershirt Color";
        color = ccCompound.hasKey(key) ? new Color(ccCompound.getInteger(key)) : new Color(Tools.random(256), Tools.random(256), Tools.random(256), 255);
        setCCColor(livingBase, key, color);


        //Hair

        key = "Hair (Base)";
        value = ccCompound.getString(key);
        if (!race.hairBase.contains(value) && !race.premiumHairBase.contains(value))
        {
            if (race.hairBase.size() > 0) value = Tools.choose(race.hairBase.toArray(new String[0]));
            else if (race.premiumHairBase.size() > 0) value = Tools.choose(race.premiumHairBase.toArray(new String[0]));
            else value = FaerunCharactersConfig.server.noneFolder;
            setCCSkin(livingBase, key, value, false);
        }

        key = "Hair (Front)";
        value = ccCompound.getString(key);
        if (!race.hairFront.contains(value) && !race.premiumHairFront.contains(value))
        {
            setCCSkin(livingBase, key, FaerunCharactersConfig.server.noneFolder, false);
        }

        key = "Hair (Back)";
        value = ccCompound.getString(key);
        if (!race.hairBack.contains(value) && !race.premiumHairBack.contains(value))
        {
            setCCSkin(livingBase, key, FaerunCharactersConfig.server.noneFolder, false);
        }

        key = "Hair (Top/Overall 1)";
        value = ccCompound.getString(key);
        if (!race.hairTop.contains(value) && !race.premiumHairTop.contains(value))
        {
            setCCSkin(livingBase, key, FaerunCharactersConfig.server.noneFolder, false);
        }

        key = "Hair (Top/Overall 2)";
        value = ccCompound.getString(key);
        if (!race.hairTop.contains(value) && !race.premiumHairTop.contains(value))
        {
            setCCSkin(livingBase, key, FaerunCharactersConfig.server.noneFolder, false);
        }

        if (!race.skinColorSetsHairColor)
        {
            key = "Hair Color";
            color = ccCompound.hasKey(key) ? new Color(ccCompound.getInteger(key)) : new Color(Tools.random(256), Tools.random(256), Tools.random(256), 255);
            if (race.hairColors != null && !race.hairColors.contains(color))
            {
                color = Tools.choose(race.hairColors.toArray(new Color[0]));
            }
            setCCColor(livingBase, key, color);
        }

        key = "Eyes";
        value = ccCompound.getString(key);
        if (!race.eyes.contains(value) && !race.premiumEyes.contains(value))
        {
            if (race.eyes.size() > 0) value = Tools.choose(race.eyes.toArray(new String[0]));
            else if (race.premiumEyes.size() > 0) value = Tools.choose(race.premiumEyes.toArray(new String[0]));
            else value = FaerunCharactersConfig.server.noneFolder;
            setCCSkin(livingBase, key, value, false);
        }

        key = "Eye Color";
        color = ccCompound.hasKey(key) ? new Color(ccCompound.getInteger(key)) : new Color(Tools.random(256), Tools.random(256), Tools.random(256), 255);
        if (race.eyeColors != null && !race.eyeColors.contains(color))
        {
            color = Tools.choose(race.eyeColors.toArray(new Color[0]));
        }
        setCCColor(livingBase, key, color);


        //Accessories

        key = "Markings";
        value = ccCompound.getString(key);
        if (!FaerunCharactersConfig.server.markingsSet.contains(value))
        {
            setCCSkin(livingBase, key, FaerunCharactersConfig.server.noneFolder, false);
        }

        key = "Accessory (Head)";
        value = ccCompound.getString(key);
        if (!FaerunCharactersConfig.server.headAccessorySet.contains(value))
        {
            setCCSkin(livingBase, key, FaerunCharactersConfig.server.noneFolder, false);
        }

        key = "Accessory (Face)";
        value = ccCompound.getString(key);
        if (!FaerunCharactersConfig.server.faceAccessorySet.contains(value))
        {
            setCCSkin(livingBase, key, FaerunCharactersConfig.server.noneFolder, false);
        }

        key = "Color 1";
        color = ccCompound.hasKey(key) ? new Color(ccCompound.getInteger(key)) : new Color(Tools.random(256), Tools.random(256), Tools.random(256), 255);
        setCCColor(livingBase, key, color);

        key = "Color 2";
        color = ccCompound.hasKey(key) ? new Color(ccCompound.getInteger(key)) : new Color(Tools.random(256), Tools.random(256), Tools.random(256), 255);
        setCCColor(livingBase, key, color);


        //Race-Set Render Modes
        if (race.renderCape) RenderModes.setRenderMode(livingBase, "CapeRace", "On");
        else RenderModes.setRenderMode(livingBase, "CapeRace", "Off");

        if (race.renderLegArmor) RenderModes.setRenderMode(livingBase, "LegRace", "On");
        else RenderModes.setRenderMode(livingBase, "LegRace", "Off");

        if (race.renderFootArmor) RenderModes.setRenderMode(livingBase, "FootRace", "On");
        else RenderModes.setRenderMode(livingBase, "FootRace", "Off");


        //Player-Set Render Modes
        value = RenderModes.getRenderMode(livingBase, "Body");
        if (value == null) RenderModes.setRenderMode(livingBase, "Body", Tools.choose("M", "F"));

        value = RenderModes.getRenderMode(livingBase, "Chest");
        if (!race.chestSizes.contains(value)) RenderModes.setRenderMode(livingBase, "Chest", Tools.choose(race.chestSizes.toArray(new String[0])));


        //Refresh GUI
        if (refresh) Network.WRAPPER.sendTo(new Network.CharacterCustomizationGUIPacket((EntityPlayerMP) livingBase), (EntityPlayerMP) livingBase);
    }


    protected static class SkinSlotting
    {
        protected SkinSlotting(String name, String skinType, int indexWithinType)
        {
            this.name = name;
            this.skinType = skinType;
            this.indexWithinType = indexWithinType;
        }

        String name, skinType;
        int indexWithinType;
    }
}
