package com.fantasticsource.faeruncharacters;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.textured.GUIImage;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;

import static com.fantasticsource.faeruncharacters.FaerunCharacters.MODID;

public class CharacterCreationGUI extends GUIScreen
{
    public static final double INTERNAL_SCALING = 0.5;

    public static final ResourceLocation
            TEX_BUTTON_IDLE = new ResourceLocation(MODID, "image/button_idle.png"),
            TEX_BUTTON_HOVER = new ResourceLocation(MODID, "image/button_hover.png"),
            TEX_BUTTON_ACTIVE = new ResourceLocation(MODID, "image/button_active.png");

    public static final int BUTTON_TEX_W = 128, BUTTON_TEX_H = 16;

    protected static final String[] TAB_NAMES = new String[]{"Body", "Head", "Accessories"};


    static
    {
        MinecraftForge.EVENT_BUS.register(CharacterCreationGUI.class);
    }


    protected Network.CharacterCreationGUIPacket packet;
    protected String selectedTab = "Body", selectedOption = "Race";


    public CharacterCreationGUI(Network.CharacterCreationGUIPacket packet)
    {
        this.packet = packet;

        Minecraft.getMinecraft().displayGuiScreen(this);

        addAll();
    }


    protected void addAll()
    {
        addTabs();
        addOptions();
    }


    protected void addTabs()
    {
        int guiScale = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        double tabRelH = (double) BUTTON_TEX_H * INTERNAL_SCALING * guiScale / pxHeight;

        double yy = (1 - tabRelH * TAB_NAMES.length) / 2;
        for (int i = 0; i < TAB_NAMES.length; i++)
        {
            String tabName = TAB_NAMES[i];
            GUIButton button = makeButton(0, yy, tabName, Color.ORANGE);
            button.addClickActions(() ->
            {
                selectedTab = tabName;
                recalc();
                button.setActive(true);
            });

            root.add(button);
            yy += tabRelH;
        }
    }


    protected void addOptions()
    {
        int guiScale = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        double tabRelW = (double) BUTTON_TEX_W * INTERNAL_SCALING * guiScale / pxWidth;
        double tabRelH = (double) BUTTON_TEX_H * INTERNAL_SCALING * guiScale / pxHeight;
        ArrayList<String> options = new ArrayList<>();


        switch (selectedTab)
        {
            case "Body":
                options.add("Race");
                options.add("Race Variant");
                options.add("Tail");
                options.add("Bare Arms");
                options.add("Skin Color");
                options.add("Body Type");
                options.add("Chest");
                options.add("Scale");
                break;


            case "Head":
                options.add("Hair (Base)");
                options.add("Hair (Front)");
                options.add("Hair (Back)");
                options.add("Hair (Top/Overall 1)");
                options.add("Hair (Top/Overall 2)");
                options.add("Hair Color");
                options.add("Eyes");
                options.add("Eye Color");
                break;


            case "Accessories":
                options.add("Markings");
                options.add("Accessory (Head)");
                options.add("Accessory (Face)");
                options.add("Color 1");
                options.add("Color 2");
                break;
        }


        double yy = (1 - tabRelH * options.size()) / 2;
        for (int i = 0; i < options.size(); i++)
        {
            String optionName = options.get(i);
            GUIButton button = makeButton(tabRelW, yy, optionName, Color.ORANGE);
            button.addClickActions(() ->
            {
                selectedOption = optionName;
                recalc();
                button.setActive(true);
            });

            root.add(button);
            yy += tabRelH;
        }
    }


    protected GUIButton makeButton(double x, double y, String text, Color activeColor)
    {
        GUIImage idle = new GUIImage(this, BUTTON_TEX_W * INTERNAL_SCALING, BUTTON_TEX_H * INTERNAL_SCALING, TEX_BUTTON_IDLE);
        idle.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        idle.add(new GUIText(this, text, getIdleColor(activeColor), INTERNAL_SCALING));

        GUIImage hover = new GUIImage(this, BUTTON_TEX_W * INTERNAL_SCALING, BUTTON_TEX_H * INTERNAL_SCALING, TEX_BUTTON_HOVER);
        hover.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        hover.add(new GUIText(this, text, getHoverColor(activeColor), INTERNAL_SCALING));

        GUIImage active = new GUIImage(this, BUTTON_TEX_W * INTERNAL_SCALING, BUTTON_TEX_H * INTERNAL_SCALING, TEX_BUTTON_ACTIVE);
        active.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        active.add(new GUIText(this, text, activeColor, INTERNAL_SCALING));

        return new GUIButton(this, x, y, idle, hover, active, true);
    }


    @Override
    protected void recalc()
    {
        root.clear();
        super.recalc();

        addAll();

        root.recalc(0);
    }


    @Override
    public String title()
    {
        return "Character Customization";
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
