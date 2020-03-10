package com.fantasticsource.faeruncharacters;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CharacterCreationGUI extends GUIScreen
{
//    static
//    {
//        MinecraftForge.EVENT_BUS.register(CharacterCreationGUI.class);
//    }
//
//    public static void show(Network.PersonalPortalGUIPacket packet)
//    {
//        CharacterCreationGUI gui = new CharacterCreationGUI();
//
//        Minecraft.getMinecraft().displayGuiScreen(gui);
//    }


    @Override
    public String title()
    {
        return "Character Customization";
    }


    @Override
    protected void init()
    {
        //Background
        root.add(new GUIDarkenedBackground(this));
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
}
