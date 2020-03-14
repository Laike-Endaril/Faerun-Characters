package com.fantasticsource.faeruncharacters;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CharacterCreationGUI extends GUIScreen
{
    static
    {
        MinecraftForge.EVENT_BUS.register(CharacterCreationGUI.class);
    }

    public CharacterCreationGUI(Network.CharacterCreationGUIPacket packet)
    {
        Minecraft.getMinecraft().displayGuiScreen(this);
    }


    @Override
    public String title()
    {
        return "Character Customization";
    }


    @SubscribeEvent
    public static void mouseClick(GUILeftClickEvent event)
    {
        GUIScreen gui = event.getScreen();
        if (gui instanceof CharacterCreationGUI)
        {
            GUIElement element = event.getElement();
            if (element instanceof GUIText)
            {
                String name = element.toString().trim().replace("Visit ", "");
                if (!name.equals(""))
                {
                    gui.close();
                }
            }
        }
    }


    @Override
    public void onClosed()
    {
        super.onClosed();
        if (Minecraft.getMinecraft().world.provider.getDimensionType() == CharacterCreation.DIMTYPE_CHARACTER_CREATION) Minecraft.getMinecraft().displayGuiScreen(this);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
