package com.fantasticsource.faeruncharacters.blocksanditems.blocks;

import com.fantasticsource.faeruncharacters.CharacterCustomization;
import com.fantasticsource.instances.blocksanditems.BlocksAndItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.fantasticsource.faeruncharacters.FaerunCharacters.MODID;

public class BlockCCPortal extends Block
{
    public BlockCCPortal()
    {
        super(Material.ROCK);
        setSoundType(SoundType.STONE);

        setBlockUnbreakable();
        setResistance(Float.MAX_VALUE);

        setCreativeTab(BlocksAndItems.creativeTab);

        setUnlocalizedName(MODID + ":ccportal");
        setRegistryName("ccportal");
    }


    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!(player instanceof EntityPlayerMP)) return true;

        CharacterCustomization.goToCC((EntityPlayerMP) player);
        return true;
    }
}
