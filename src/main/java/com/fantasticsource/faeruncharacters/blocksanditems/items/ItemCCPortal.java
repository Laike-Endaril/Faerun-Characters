package com.fantasticsource.faeruncharacters.blocksanditems.items;

import com.fantasticsource.faeruncharacters.blocksanditems.BlocksAndItems;
import net.minecraft.item.ItemBlock;

import static com.fantasticsource.faeruncharacters.FaerunCharacters.MODID;

public class ItemCCPortal extends ItemBlock
{
    public ItemCCPortal()
    {
        super(BlocksAndItems.blockCCPortal);

        setUnlocalizedName(MODID + ":ccportal");
        setRegistryName("ccportal");
    }
}
