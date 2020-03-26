package com.fantasticsource.faeruncharacters.nbt;

import com.fantasticsource.faeruncharacters.CRace;
import com.fantasticsource.fantasticlib.api.FLibAPI;
import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.aw.AWSkinGenerator;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.entity.EntityLivingBase;
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
        SKIN_SLOTTINGS.put("Race Variant", new SkinSlotting("Race Variant", "armourers:outfit", 0));
        SKIN_SLOTTINGS.put("Tail", new SkinSlotting("Tail", "armourers:wings", 5));
        SKIN_SLOTTINGS.put("Bare Arms", new SkinSlotting("Bare Arms", "armourers:chest", 1));
        SKIN_SLOTTINGS.put("Markings", new SkinSlotting("Markings", "armourers:head", 0));
        SKIN_SLOTTINGS.put("Hair (Base)", new SkinSlotting("Hair (Base)", "armourers:head", 1));
        SKIN_SLOTTINGS.put("Eyes", new SkinSlotting("Eyes", "armourers:head", 2));
        SKIN_SLOTTINGS.put("Hair (Front)", new SkinSlotting("Hair (Front)", "armourers:head", 3));
        SKIN_SLOTTINGS.put("Hair (Back)", new SkinSlotting("Hair (Back)", "armourers:head", 4));
        SKIN_SLOTTINGS.put("Hair (Top/Overall 1)", new SkinSlotting("Hair (Top/Overall 1)", "armourers:head", 5));
        SKIN_SLOTTINGS.put("Hair (Top/Overall 2)", new SkinSlotting("Hair (Top/Overall 2)", "armourers:head", 6));
        SKIN_SLOTTINGS.put("Accessory (Head)", new SkinSlotting("Accessory (Head)", "armourers:head", 7));
        SKIN_SLOTTINGS.put("Accessory (Face)", new SkinSlotting("Accessory (Face)", "armourers:head", 8));

        COLOR_KEYS.put("Skin Color", "skin");
        COLOR_KEYS.put("Eye Color", "eye");
        COLOR_KEYS.put("Hair Color", "hair");
        COLOR_KEYS.put("Color 1", "misc_1");
        COLOR_KEYS.put("Color 2", "misc_2");
        COLOR_KEYS.put("Color 3", "misc_3");
        COLOR_KEYS.put("Color 4", "misc_4");
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
        server.commandManager.executeCommand(server, "/armourers wardrobe setColour " + livingBase.getName() + " " + cmdKey + " " + cmdHex);

        getCC(livingBase).setInteger(key, value.color());
    }

    public static void setCCSkin(EntityLivingBase livingBase, String key, String value)
    {
        if (key.equals("Race"))
        {
            CRace race = CRace.RACES.get(value);
            if (race == null) race = CRace.RACES_PREMIUM.get(value);
            if (race == null)
            {
                System.err.println(TextFormatting.RED + "Tried to set race to non-existing race: (" + value + ")");
                System.err.println(TextFormatting.RED + "...for entity..." + livingBase.getName() + " (" + livingBase.getClass().getName() + ")");
                return;
            }


            key = "Race Variant";
            value = race.raceVariants.size() > 0 ? race.raceVariants.iterator().next() : race.premiumRaceVariants.iterator().next();
        }


        SkinSlotting skinSlotting = SKIN_SLOTTINGS.get(key);
        if (skinSlotting == null)
        {
            System.err.println(TextFormatting.RED + "Tried to set invalid slotting (" + key + ") to " + value);
            System.err.println(TextFormatting.RED + "...for entity..." + livingBase.getName() + " (" + livingBase.getClass().getName() + ")");
            return;
        }


        ItemStack newSkin = AWSkinGenerator.generate(value, skinSlotting.skinType);
        applyCCTag(newSkin, skinSlotting.name);

        ItemStack oldSkin = GlobalInventory.getAWSkin(livingBase, skinSlotting.skinType, skinSlotting.indexWithinType);

        if (oldSkin != null && !oldSkin.isEmpty() && !ccTagMatches(oldSkin, newSkin))
        {
            System.err.println(TextFormatting.RED + "Tried to set CC skin in wardrobe slot already filled with non-CC skin!");
            System.err.println(TextFormatting.RED + "Entity: " + livingBase.getName() + " (" + livingBase.getClass().getName() + ")");
            System.err.println(TextFormatting.RED + "Slot; " + skinSlotting.skinType + " #" + skinSlotting.indexWithinType);
            System.err.println(TextFormatting.RED + "Existing item in slot: " + oldSkin.getDisplayName());
        }


        GlobalInventory.setAWSkin(livingBase, skinSlotting.skinType, skinSlotting.indexWithinType, newSkin);
        GlobalInventory.syncAWWardrobeSkins(livingBase, true, true);

        getCC(livingBase).setString(key, value);
    }


    protected static boolean ccTagMatches(ItemStack stack1, ItemStack stack2)
    {
        if (!stack1.hasTagCompound() || !stack2.hasTagCompound()) return false;
        NBTTagCompound compound1 = stack1.getTagCompound(), compound2 = stack2.getTagCompound();

        if (!compound1.hasKey(MODID) || !compound2.hasKey(MODID)) return false;
        compound1 = compound1.getCompoundTag(MODID);
        compound2 = compound2.getCompoundTag(MODID);

        if (!compound1.hasKey("CC") || !compound2.hasKey("CC")) return false;
        return compound1.getString("CC").equals(compound2.getString("CC"));
    }

    protected static void applyCCTag(ItemStack stack, String skinSlottingName)
    {
        //Mark as CC skin
        NBTTagCompound compound = stack.getTagCompound();

        compound.setTag(MODID, new NBTTagCompound());
        compound = compound.getCompoundTag(MODID);

        compound.setString("CC", skinSlottingName);
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
