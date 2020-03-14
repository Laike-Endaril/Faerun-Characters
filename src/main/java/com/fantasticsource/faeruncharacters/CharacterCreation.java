package com.fantasticsource.faeruncharacters;

import com.fantasticsource.instances.InstanceData;
import com.fantasticsource.instances.server.Teleport;
import com.fantasticsource.instances.world.dimensions.InstanceTypes;
import com.fantasticsource.mctools.aw.RenderModes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;

public class CharacterCreation
{
    public static void validate(EntityPlayerMP player)
    {
        if (!hasValidCharacter(player)) go(player);
    }


    public static boolean hasValidCharacter(EntityPlayerMP player)
    {
        if (RenderModes.getRenderMode(player, "Body") == null) return false;
        //TODO add more conditions
        return true;
    }


    public static void go(EntityPlayerMP player)
    {
        InstanceData data = InstanceData.get(true, InstanceTypes.TEMPLATE, "Character_Creation");
        if (!data.exists())
        {
            System.err.println(TextFormatting.RED + "No character creation instance was found!  Need a character creation instance saved to '" + data.getFullName() + "'");
            return;
        }


        Teleport.joinTempCopy(player, data.getFullName());
        Network.WRAPPER.sendTo(new Network.CharacterCreationGUIPacket(), player);
    }
}
