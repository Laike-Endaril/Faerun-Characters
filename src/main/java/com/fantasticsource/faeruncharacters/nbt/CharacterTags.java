package com.fantasticsource.faeruncharacters.nbt;

import com.fantasticsource.fantasticlib.api.FLibAPI;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

import static com.fantasticsource.faeruncharacters.FaerunCharacters.MODID;

public class CharacterTags
{
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
}
