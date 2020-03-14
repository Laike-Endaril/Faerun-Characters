package com.fantasticsource.faeruncharacters;

import com.fantasticsource.instances.InstanceData;
import com.fantasticsource.instances.Instances;
import com.fantasticsource.instances.server.Teleport;
import com.fantasticsource.instances.world.dimensions.template.WorldProviderTemplate;
import com.fantasticsource.mctools.aw.RenderModes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;

public class CharacterCreation
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
        if (RenderModes.getRenderMode(player, "Body") == null) return false;
        //TODO add more conditions
        return true;
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
        Network.WRAPPER.sendTo(new Network.CharacterCreationGUIPacket(), player);
    }
}
