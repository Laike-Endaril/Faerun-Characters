package com.fantasticsource.faeruncharacters;

import com.fantasticsource.faeruncharacters.config.FaerunCharactersConfig;
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

public class CharacterCustomizationGUI extends GUIScreen
{
    public static final double INTERNAL_SCALING = 0.5;

    public static Color
            activeButtonColor = new Color(FaerunCharactersConfig.client.activeButtonColor, true),
            hoverButtonColor = new Color(FaerunCharactersConfig.client.hoverButtonColor, true),
            idleButtonColor = new Color(FaerunCharactersConfig.client.idleButtonColor, true),
            activePremiumButtonColor = new Color(FaerunCharactersConfig.client.activePremiumButtonColor, true),
            hoverPremiumButtonColor = new Color(FaerunCharactersConfig.client.hoverPremiumButtonColor, true),
            idlePremiumButtonColor = new Color(FaerunCharactersConfig.client.idlePremiumButtonColor, true);

    public static final ResourceLocation
            TEX_BUTTON_IDLE = new ResourceLocation(MODID, "image/button_idle.png"),
            TEX_BUTTON_HOVER = new ResourceLocation(MODID, "image/button_hover.png"),
            TEX_BUTTON_ACTIVE = new ResourceLocation(MODID, "image/button_active.png"),
            TEX_PREMIUMBUTTON_IDLE = new ResourceLocation(MODID, "image/premium_button_idle.png"),
            TEX_PREMIUMBUTTON_HOVER = new ResourceLocation(MODID, "image/premium_button_hover.png"),
            TEX_PREMIUMBUTTON_ACTIVE = new ResourceLocation(MODID, "image/premium_button_active.png");

    public static final int BUTTON_TEX_W = 128, BUTTON_TEX_H = 16;

    protected static final String[] TAB_NAMES = new String[]{"Body", "Head", "Accessories"};


    static
    {
        MinecraftForge.EVENT_BUS.register(CharacterCustomizationGUI.class);
    }


    protected Network.CharacterCustomizationGUIPacket packet;
    protected String selectedTab = "Body", selectedOption = "Race";


    public CharacterCustomizationGUI(Network.CharacterCustomizationGUIPacket packet)
    {
        this.packet = packet;

        Minecraft.getMinecraft().displayGuiScreen(this);

        addAll();
    }


    protected void addAll()
    {
        addTabs();
        addOptions();
        addControls();
    }


    protected void addTabs()
    {
        int guiScale = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        double buttonRelH = (double) BUTTON_TEX_H * INTERNAL_SCALING * guiScale / pxHeight;

        double yy = (1 - buttonRelH * TAB_NAMES.length) / 2;
        for (int i = 0; i < TAB_NAMES.length; i++)
        {
            String tabName = TAB_NAMES[i];
            GUIButton button = makeButton(0, yy, tabName);
            button.addClickActions(() ->
            {
                selectedTab = tabName;
                recalc();
            });
            if (tabName.equals(selectedTab)) button.setActive(true);

            root.add(button);
            yy += buttonRelH;
        }
    }


    protected void addOptions()
    {
        if (selectedTab == null) return;


        int guiScale = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        double buttonRelW = (double) BUTTON_TEX_W * INTERNAL_SCALING * guiScale / pxWidth;
        double buttonRelH = (double) BUTTON_TEX_H * INTERNAL_SCALING * guiScale / pxHeight;
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


        double yy = (1 - buttonRelH * options.size()) / 2;
        for (int i = 0; i < options.size(); i++)
        {
            String optionName = options.get(i);
            GUIButton button = makeButton(buttonRelW, yy, optionName);
            button.addClickActions(() ->
            {
                if (optionName.equals(selectedOption)) selectedOption = null;
                else selectedOption = optionName;
                recalc();
            });
            if (optionName.equals(selectedOption)) button.setActive(true);

            root.add(button);
            yy += buttonRelH;
        }
    }


    protected void addControls()
    {
        if (selectedTab == null || selectedOption == null) return;


        switch (selectedTab)
        {
            case "Body":
                switch (selectedOption)
                {
                    case "Race":
                    case "Race Variant":
                    case "Tail":
                    case "Bare Arms":
                    case "Body Type":
                    case "Chest":
                        //TODO selector
                        break;

                    case "Skin Color":
                        //TODO selector or HSV sliders, depending on race
                        break;

                    case "Scale":
                        //TODO slider
                        break;
                }
                break;


            case "Head":
                switch (selectedOption)
                {
                    case "Hair (Base)":
                    case "Hair (Front)":
                    case "Hair (Back)":
                    case "Hair (Top/Overall 1)":
                    case "Hair (Top/Overall 2)":
                    case "Eyes":
                        //TODO selector
                        break;

                    case "Hair Color":
                    case "Eye Color":
                        //TODO selector or HSV sliders, depending on race
                        break;
                }
                break;


            case "Accessories":
                switch (selectedOption)
                {
                    case "Markings":
                    case "Accessory (Head)":
                    case "Accessory (Face)":
                        //TODO selector
                        break;

                    case "Color 1":
                    case "Color 2":
                        //TODO selector or HSV sliders, depending on race
                        break;
                }
                break;
        }
    }


    protected GUIButton makeButton(double x, double y, String text)
    {
        GUIImage active = new GUIImage(this, BUTTON_TEX_W * INTERNAL_SCALING, BUTTON_TEX_H * INTERNAL_SCALING, TEX_BUTTON_ACTIVE);
        active.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        active.add(new GUIText(this, text, activeButtonColor, INTERNAL_SCALING));

        GUIImage hover = new GUIImage(this, BUTTON_TEX_W * INTERNAL_SCALING, BUTTON_TEX_H * INTERNAL_SCALING, TEX_BUTTON_HOVER);
        hover.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        hover.add(new GUIText(this, text, hoverButtonColor, INTERNAL_SCALING));

        GUIImage idle = new GUIImage(this, BUTTON_TEX_W * INTERNAL_SCALING, BUTTON_TEX_H * INTERNAL_SCALING, TEX_BUTTON_IDLE);
        idle.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        idle.add(new GUIText(this, text, idleButtonColor, INTERNAL_SCALING));

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
        if (Minecraft.getMinecraft().world.provider.getDimensionType() == CharacterCustomization.DIMTYPE_CHARACTER_CREATION) Minecraft.getMinecraft().displayGuiScreen(this);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
