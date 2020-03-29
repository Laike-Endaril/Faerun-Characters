package com.fantasticsource.faeruncharacters.blocksanditems;

import com.fantasticsource.faeruncharacters.blocksanditems.blocks.BlockCCPortal;
import com.fantasticsource.faeruncharacters.blocksanditems.items.ItemCCPortal;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import static com.fantasticsource.faeruncharacters.FaerunCharacters.MODID;

public class BlocksAndItems
{
    @GameRegistry.ObjectHolder(MODID + ":ccportal")
    public static BlockCCPortal blockCCPortal;
    @GameRegistry.ObjectHolder(MODID + ":ccportal")
    public static ItemCCPortal itemCCPortal;


    @SubscribeEvent
    public static void blockRegistry(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();

        registry.register(new BlockCCPortal());
    }

    @SubscribeEvent
    public static void itemRegistry(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(new ItemCCPortal());
    }

    @SubscribeEvent
    public static void modelRegistry(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(itemCCPortal, 0, new ModelResourceLocation(MODID + ":ccportal", "inventory"));
    }
}
