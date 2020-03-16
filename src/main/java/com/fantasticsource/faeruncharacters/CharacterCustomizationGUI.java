package com.fantasticsource.faeruncharacters;

import com.fantasticsource.faeruncharacters.config.FaerunCharactersConfig;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.textured.GUIImage;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.Collection;

import static com.fantasticsource.faeruncharacters.FaerunCharacters.MODID;

public class CharacterCustomizationGUI extends GUIScreen
{
    public static final int BUTTON_W = 128, BUTTON_H = 16;

    public static double internalScaling, buttonRelW, buttonRelH;

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
            TEX_PREMIUM_BUTTON_IDLE = new ResourceLocation(MODID, "image/premium_button_idle.png"),
            TEX_PREMIUM_BUTTON_HOVER = new ResourceLocation(MODID, "image/premium_button_hover.png"),
            TEX_PREMIUM_BUTTON_ACTIVE = new ResourceLocation(MODID, "image/premium_button_active.png");

    protected static final String[] TAB_NAMES = new String[]{"Body", "Head", "Accessories"};


    static
    {
        MinecraftForge.EVENT_BUS.register(CharacterCustomizationGUI.class);
    }


    protected Network.CharacterCustomizationGUIPacket packet;
    protected String selectedTab = "Body", selectedOption = null;
    protected NBTTagCompound ccCompound;
    protected CRace race;


    public CharacterCustomizationGUI(Network.CharacterCustomizationGUIPacket packet)
    {
        this.packet = packet;
        ccCompound = packet.ccCompound;

        Minecraft.getMinecraft().displayGuiScreen(this);

        preCalc();
        addAll();
    }


    protected void preCalc()
    {
        //Definitions for what we want to allow for
        int totalW = BUTTON_W * 4;
        int totalH = BUTTON_H * 15;


        //Highest internal scaling that results in a full-pixel multiple when multiplied by the current MC gui scaling
        int mcScale = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        internalScaling = Tools.min(0.5d * pxWidth / (totalW * mcScale), (double) pxHeight / (totalH * mcScale));
        double internalScaling2 = Math.floor(mcScale * internalScaling) / mcScale;
        if (internalScaling2 != 0) internalScaling = internalScaling2;


        //Calc relative button size
        buttonRelW = (double) BUTTON_W * internalScaling * mcScale / pxWidth;
        buttonRelH = (double) BUTTON_H * internalScaling * mcScale / pxHeight;
    }

    protected void addAll()
    {
        String raceString = ccCompound.getString("Race");
        race = packet.races.get(raceString);
        if (race == null) race = packet.racesPremium.get(raceString);

        addTabs();
        addOptions();
        addControls();
    }


    protected void addTabs()
    {
        int guiScale = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        double buttonRelH = (double) BUTTON_H * internalScaling * guiScale / pxHeight;

        double yy = (1 - buttonRelH * TAB_NAMES.length) / 2;
        for (int i = 0; i < TAB_NAMES.length; i++)
        {
            String tabName = TAB_NAMES[i];
            GUIButton button = makeButton(0, yy, tabName);
            button.addClickActions(() ->
            {
                selectedTab = tabName;
                selectedOption = null;
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
                //String selectors
                switch (selectedOption)
                {
                    case "Race":
                        addStringSelector("Race", packet.races.keySet(), packet.racesPremium.keySet());
                        break;

                    case "Race Variant":
                        if (race == null) break;
                        addStringSelector("Race Variant", race.raceVariants, race.premiumRaceVariants);
                        break;

                    case "Tail":
                        if (race == null) break;
                        addStringSelector("Tail", race.tails);
                        break;

                    case "Bare Arms":
                        if (race == null) break;
                        addStringSelector("Bare Arms", packet.bareArms);
                        break;

                    case "Body Type":
                        //TODO
                        break;

                    case "Chest":
                        //TODO
                        break;


                    //Color selectors or HSV sliders, depending on race
                    case "Skin Color":
                        //TODO
                        break;


                    //Sliders
                    case "Scale":
                        //TODO
                        break;
                }
                break;


            case "Head":
                switch (selectedOption)
                {
                    //String selectors
                    case "Hair (Base)":
                        //TODO
                        break;

                    case "Hair (Front)":
                        //TODO
                        break;

                    case "Hair (Back)":
                        //TODO
                        break;

                    case "Hair (Top/Overall 1)":
                        //TODO
                        break;

                    case "Hair (Top/Overall 2)":
                        //TODO
                        break;

                    case "Eyes":
                        //TODO
                        break;


                    //Color selectors or HSV sliders, depending on race
                    case "Hair Color":
                        //TODO
                        break;

                    case "Eye Color":
                        //TODO
                        break;
                }
                break;


            case "Accessories":
                switch (selectedOption)
                {
                    //String selectors
                    case "Markings":
                        //TODO
                        break;

                    case "Accessory (Head)":
                        //TODO
                        break;

                    case "Accessory (Face)":
                        //TODO
                        break;


                    //Color selectors or HSV sliders, depending on race
                    case "Color 1":
                        //TODO
                        break;

                    case "Color 2":
                        //TODO
                        break;
                }
                break;
        }
    }

    protected void addStringSelector(String key, Collection<String>... selections)
    {
        String current = ccCompound.getString(key);
        ArrayList<String> options = new ArrayList<>();
        for (Collection<String> selectionSet : selections) options.addAll(selectionSet);


        double yy = (1 - buttonRelH * Math.ceil(options.size() / 2d)) / 2;
        for (int i = 0; i < options.size(); i++)
        {
            String buttonText = options.get(i);
            GUIButton button = makeButton(i % 2 == 0 ? buttonRelW * 2 : buttonRelW * 3, yy, buttonText);
            button.addClickActions(() ->
            {
                if (buttonText.equals(current)) ccCompound.removeTag(key);
                else ccCompound.setString(key, buttonText);
                recalc();
            });
            if (buttonText.equals(current)) button.setActive(true);

            root.add(button);
            if (i % 2 == 1) yy += buttonRelH;
        }
    }


    protected GUIButton makeButton(double x, double y, String text)
    {
        return makeButton(x, y, text, false);
    }

    protected GUIButton makeButton(double x, double y, String text, boolean premium)
    {
        GUIImage active = new GUIImage(this, BUTTON_W * internalScaling, BUTTON_H * internalScaling, premium ? TEX_PREMIUM_BUTTON_ACTIVE : TEX_BUTTON_ACTIVE);
        active.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        active.add(new GUIText(this, text, premium ? activePremiumButtonColor : activeButtonColor, internalScaling));

        GUIImage hover = new GUIImage(this, BUTTON_W * internalScaling, BUTTON_H * internalScaling, premium ? TEX_PREMIUM_BUTTON_HOVER : TEX_BUTTON_HOVER);
        hover.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        hover.add(new GUIText(this, text, premium ? hoverPremiumButtonColor : hoverButtonColor, internalScaling));

        GUIImage idle = new GUIImage(this, BUTTON_W * internalScaling, BUTTON_H * internalScaling, premium ? TEX_PREMIUM_BUTTON_IDLE : TEX_BUTTON_IDLE);
        idle.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        idle.add(new GUIText(this, text, premium ? idlePremiumButtonColor : idleButtonColor, internalScaling));

        return new GUIButton(this, x, y, idle, hover, active, true);
    }


    @Override
    protected void recalc()
    {
        root.clear();
        super.recalc();

        preCalc();
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
