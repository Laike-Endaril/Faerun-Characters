package com.fantasticsource.faeruncharacters.gui;

import com.fantasticsource.faeruncharacters.CRace;
import com.fantasticsource.faeruncharacters.Camera;
import com.fantasticsource.faeruncharacters.CharacterCustomization;
import com.fantasticsource.faeruncharacters.Network;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import static com.fantasticsource.faeruncharacters.FaerunCharacters.MODID;

public class CharacterCustomizationGUI extends GUIScreen
{
    public static final int
            ELEMENT_W = 128, ELEMENT_H = 16,
            GAP_W = 4,

    //Definitions for what we want to allow for
    TOTAL_W = ELEMENT_W * 4 + GAP_W,
            TOTAL_H = ELEMENT_H * 15;


    public static final HashSet<String> bodyTypes = new HashSet<>();
    public static double internalScaling, buttonRelW, buttonRelH, gapRelW, sliderRelH;

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
            TEX_PREMIUM_BUTTON_IDLE = new ResourceLocation(MODID, "image/button_premium_idle.png"),
            TEX_PREMIUM_BUTTON_HOVER = new ResourceLocation(MODID, "image/button_premium_hover.png"),
            TEX_PREMIUM_BUTTON_ACTIVE = new ResourceLocation(MODID, "image/button_premium_active.png"),
            TEX_SLIDER_BAR = new ResourceLocation(MODID, "image/slider_bar.png"),
            TEX_SLIDER_KNOB = new ResourceLocation(MODID, "image/slider_knob.png"),
            TEX_SINGLE_COLOR = new ResourceLocation(MODID, "image/single_color.png"),
            TEX_SLIDER_HUE = new ResourceLocation(MODID, "image/slider_hue.png"),
            TEX_SLIDER_SATURATION = new ResourceLocation(MODID, "image/slider_saturation.png"),
            TEX_SLIDER_GRADIENT = new ResourceLocation(MODID, "image/slider_gradient.png");

    protected static final String[] TAB_NAMES = new String[]{"Body", "Head", "Accessories"};


    static
    {
        MinecraftForge.EVENT_BUS.register(CharacterCustomizationGUI.class);
        bodyTypes.add("Masculine");
        bodyTypes.add("Feminine");
    }


    protected Network.CharacterCustomizationGUIPacket packet;
    protected String selectedTab = "Body", selectedOption = null;
    protected NBTTagCompound ccCompound;
    protected CRace race;


    public CharacterCustomizationGUI(Network.CharacterCustomizationGUIPacket packet)
    {
        this.packet = packet;
        ccCompound = packet.ccCompound;

        Minecraft mc = Minecraft.getMinecraft();
        mc.displayGuiScreen(this);

        preCalc();
        addAll();


        //Set to camera view
        Camera.active = true;
    }


    protected void preCalc()
    {
        //Highest internal scaling that results in a full-pixel multiple when multiplied by the current MC gui scaling
        int mcScale = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        internalScaling = Tools.min(0.5d * pxWidth / (TOTAL_W * mcScale), (double) pxHeight / (TOTAL_H * mcScale));
        double internalScaling2 = Math.floor(mcScale * internalScaling) / mcScale;
        if (internalScaling2 != 0) internalScaling = internalScaling2;


        //Calc relative element sizes
        buttonRelW = (double) ELEMENT_W * internalScaling * mcScale / pxWidth;
        buttonRelH = (double) ELEMENT_H * internalScaling * mcScale / pxHeight;

        gapRelW = (double) GAP_W * internalScaling * mcScale / pxWidth;

        sliderRelH = (double) ELEMENT_H * internalScaling * mcScale / pxHeight;
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
        double buttonRelH = (double) ELEMENT_H * internalScaling * guiScale / pxHeight;

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
                        addStringSelector(selectedOption, true, packet.races.keySet(), packet.racesPremium.keySet());
                        break;

                    case "Race Variant":
                        if (race == null) break;
                        addStringSelector(selectedOption, true, race.raceVariants, race.premiumRaceVariants);
                        break;

                    case "Tail":
                        if (race == null) break;
                        addStringSelector(selectedOption, true, race.tails);
                        break;

                    case "Bare Arms":
                        if (race == null) break;
                        addStringSelector(selectedOption, true, packet.bareArms);
                        break;

                    case "Body Type":
                        if (race == null) break;
                        addStringSelector(selectedOption, false, bodyTypes);
                        break;

                    case "Chest":
                        if (race == null) break;
                        addStringSelector(selectedOption, false, race.chestSizes);
                        break;


                    //Color selectors or HSV sliders, depending on race
                    case "Skin Color":
                        if (race == null) break;
                        if (race.skinColors != null) addColorSelector(selectedOption, race.skinColors.toArray(new Color[0]));
                        else addHSVSliders(selectedOption);
                        break;


                    //Sliders
                    case "Scale":
                        if (race == null) break;
                        addSingleSliderDouble(selectedOption, race.renderScaleMin, race.renderScaleMax);
                        break;
                }
                break;


            case "Head":
                switch (selectedOption)
                {
                    //String selectors
                    case "Hair (Base)":
                        if (race == null) break;
                        addStringSelector(selectedOption, true, race.hairBase, race.premiumHairBase);
                        break;

                    case "Hair (Front)":
                        if (race == null) break;
                        addStringSelector(selectedOption, true, race.hairFront, race.premiumHairFront);
                        break;

                    case "Hair (Back)":
                        if (race == null) break;
                        addStringSelector(selectedOption, true, race.hairBack, race.premiumHairBack);
                        break;

                    case "Hair (Top/Overall 1)":
                    case "Hair (Top/Overall 2)":
                        if (race == null) break;
                        addStringSelector(selectedOption, true, race.hairBase, race.premiumHairBase);
                        break;

                    case "Eyes":
                        if (race == null) break;
                        addStringSelector(selectedOption, true, race.eyes, race.premiumEyes);
                        break;


                    //Color selectors or HSV sliders, depending on race
                    case "Hair Color":
                        if (race == null) break;
                        if (race.skinColorSetsHairColor)
                        {
                            if (race.skinColors != null) addColorSelector("Skin Color", race.skinColors.toArray(new Color[0]));
                            else addHSVSliders("Skin Color");
                        }
                        else
                        {
                            if (race.hairColors != null) addColorSelector(selectedOption, race.hairColors.toArray(new Color[0]));
                            else addHSVSliders(selectedOption);
                        }
                        break;

                    case "Eye Color":
                        if (race == null) break;
                        if (race.eyeColors != null) addColorSelector(selectedOption, race.eyeColors.toArray(new Color[0]));
                        else addHSVSliders(selectedOption);
                        break;
                }
                break;


            case "Accessories":
                switch (selectedOption)
                {
                    //String selectors
                    case "Markings":
                        if (race == null) break;
                        addStringSelector(selectedOption, true, packet.markings);
                        break;

                    case "Accessory (Head)":
                        if (race == null) break;
                        addStringSelector(selectedOption, true, packet.headAccessories);
                        break;

                    case "Accessory (Face)":
                        if (race == null) break;
                        addStringSelector(selectedOption, true, packet.faceAccessories);
                        break;


                    //Color selectors or HSV sliders, depending on race
                    case "Color 1":
                        if (race == null) break;
                        addHSVSliders(selectedOption);
                        break;

                    case "Color 2":
                        if (race == null) break;
                        addHSVSliders(selectedOption);
                        break;
                }
                break;
        }
    }


    protected void addStringSelector(String key, boolean fileNames, Collection<String> selections)
    {
        addStringSelector(key, fileNames, selections, new HashSet<>());
    }

    protected void addStringSelector(String key, boolean fileNames, Collection<String> selections, Collection<String> premiumSelections)
    {
        String current = ccCompound.getString(key);
        ArrayList<String> options = new ArrayList<>();
        options.addAll(selections);
        options.addAll(premiumSelections);


        double yy = (1 - buttonRelH * Math.ceil(options.size() / 2d)) / 2;
        for (int i = 0; i < options.size(); i++)
        {
            String buttonText = options.get(i);
            String buttonShortText = buttonText;
            if (fileNames)
            {
                buttonShortText = Tools.fixFileSeparators(buttonShortText);
                buttonShortText = buttonShortText.substring(buttonShortText.lastIndexOf(File.separator) + 1);
            }

            GUIButton button = makeButton(i % 2 == 0 ? buttonRelW * 2 + gapRelW : buttonRelW * 3 + gapRelW, yy, buttonShortText, i >= selections.size());
            button.addClickActions(() ->
            {
                if (buttonText.equals(current)) ccCompound.removeTag(key);
                else ccCompound.setString(key, buttonText);
                switch (key)
                {
                    case "Body Type":
                        Network.WRAPPER.sendToServer(new Network.SetBodyTypePacket(buttonText));
                        break;

                    case "Chest Type":
                        Network.WRAPPER.sendToServer(new Network.SetChestTypePacket(buttonText));
                        break;

                    default:
                        Network.WRAPPER.sendToServer(new Network.SetCCStringPacket(key, buttonText));
                }

                recalc();
            });
            if (buttonText.equals(current)) button.setActive(true);

            root.add(button);
            if (i % 2 == 1) yy += buttonRelH;
        }
    }


    protected void addSingleSliderDouble(String key, double min, double max)
    {
        GUIHorizontalSlider slider = new GUIHorizontalSlider(this, buttonRelW * 2 + gapRelW, (1 - sliderRelH) / 2, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, min, max, TEX_SLIDER_BAR, TEX_SLIDER_KNOB);
        slider.setValue(ccCompound.getDouble(key));
        slider.addDragActions(() ->
        {
            ccCompound.setDouble(key, slider.getValue());
            Network.WRAPPER.sendToServer(new Network.SetCCDoublePacket(key, slider.getValue()));
        });

        root.add(slider);
    }


    protected void addColorSelector(String key, Color... colors)
    {
        Color current = new Color(ccCompound.getInteger(key));


        double yy = (1 - buttonRelH * Math.ceil(colors.length / 2d)) / 2;
        for (int i = 0; i < colors.length; i++)
        {
            Color buttonColor = colors[i];

            GUIButton button = makeColorButton(i % 2 == 0 ? buttonRelW * 2 + gapRelW : buttonRelW * 3 + gapRelW, yy, buttonColor);
            button.addClickActions(() ->
            {
                Camera.active = false;
                if (buttonColor.equals(current)) ccCompound.removeTag(key);
                else ccCompound.setInteger(key, buttonColor.color());
                Network.WRAPPER.sendToServer(new Network.SetCCIntPacket(key, buttonColor.color()));

                recalc();
            });
            if (buttonColor.equals(current)) button.setActive(true);

            root.add(button);
            if (i % 2 == 1) yy += buttonRelH;
        }
    }


    protected void addHSVSliders(String key)
    {
        Color color = new Color(ccCompound.getInteger(key));

        GUIHorizontalSlider hueSlider = new GUIHorizontalSlider(this, buttonRelW * 2 + gapRelW, (1 - sliderRelH) / 2 - sliderRelH, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, 0, 255, TEX_SLIDER_HUE, TEX_SLIDER_KNOB);
        hueSlider.setValue(color.h());

        GUIHorizontalSlider satSlider = new GUIHorizontalSlider(this, buttonRelW * 2 + gapRelW, (1 - sliderRelH) / 2, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, 0, 255, TEX_SLIDER_SATURATION, TEX_SLIDER_KNOB);
        satSlider.setValue(color.s());
        GUIImage satOverlay = new GUIImage(this, 0, 0, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, TEX_SLIDER_GRADIENT, new Color(0).setColorHSV(color.h(), 255, 255, 255));
        satSlider.add(0, satOverlay);

        GUIHorizontalSlider valSlider = new GUIHorizontalSlider(this, buttonRelW * 2 + gapRelW, (1 - sliderRelH) / 2 + sliderRelH, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, 0, 255, TEX_BUTTON_ACTIVE, TEX_SLIDER_KNOB);
        valSlider.setValue(color.v());
        GUIImage valOverlay = new GUIImage(this, 0, 0, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, TEX_SLIDER_GRADIENT, new Color(0).setColorHSV(color.h(), color.s(), 255, 255));
        valSlider.add(0, valOverlay);

        hueSlider.addDragActions(() ->
        {
            Color c = new Color(0).setColorHSV((int) hueSlider.getValue(), (int) satSlider.getValue(), (int) valSlider.getValue(), color.a());
            satOverlay.setColor(new Color(0).setColorHSV((int) hueSlider.getValue(), 255, 255, 255));
            valOverlay.setColor(new Color(0).setColorHSV((int) hueSlider.getValue(), (int) satSlider.getValue(), 255, 255));
            ccCompound.setInteger(key, c.color());
            Network.WRAPPER.sendToServer(new Network.SetCCIntPacket(key, c.color()));
        });

        satSlider.addDragActions(() ->
        {
            Color c = new Color(0).setColorHSV((int) hueSlider.getValue(), (int) satSlider.getValue(), (int) valSlider.getValue(), color.a());
            valOverlay.setColor(new Color(0).setColorHSV((int) hueSlider.getValue(), (int) satSlider.getValue(), 255, 255));
            ccCompound.setInteger(key, c.color());
            Network.WRAPPER.sendToServer(new Network.SetCCIntPacket(key, c.color()));
        });

        valSlider.addDragActions(() ->
        {
            Color c = new Color(0).setColorHSV((int) hueSlider.getValue(), (int) satSlider.getValue(), (int) valSlider.getValue(), color.a());
            ccCompound.setInteger(key, c.color());
            Network.WRAPPER.sendToServer(new Network.SetCCIntPacket(key, c.color()));
        });

        root.addAll(hueSlider, satSlider, valSlider);
    }


    protected GUIButton makeButton(double x, double y, String text)
    {
        return makeButton(x, y, text, false);
    }

    protected GUIButton makeButton(double x, double y, String text, boolean premium)
    {
        GUIImage active = new GUIImage(this, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, premium ? TEX_PREMIUM_BUTTON_ACTIVE : TEX_BUTTON_ACTIVE);
        active.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        active.add(new GUIText(this, text, premium ? activePremiumButtonColor : activeButtonColor, internalScaling));

        GUIImage hover = new GUIImage(this, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, premium ? TEX_PREMIUM_BUTTON_HOVER : TEX_BUTTON_HOVER);
        hover.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        hover.add(new GUIText(this, text, premium ? hoverPremiumButtonColor : hoverButtonColor, internalScaling));

        GUIImage idle = new GUIImage(this, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, premium ? TEX_PREMIUM_BUTTON_IDLE : TEX_BUTTON_IDLE);
        idle.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        idle.add(new GUIText(this, text, premium ? idlePremiumButtonColor : idleButtonColor, internalScaling));

        return new GUIButton(this, x, y, idle, hover, active, true);
    }


    protected GUIButton makeColorButton(double x, double y, Color color)
    {
        GUIImage active = new GUIImage(this, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, TEX_BUTTON_ACTIVE);
        active.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        active.add(new GUIImage(this, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, TEX_SINGLE_COLOR, color));

        GUIImage hover = new GUIImage(this, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, TEX_BUTTON_HOVER);
        hover.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        hover.add(new GUIImage(this, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, TEX_SINGLE_COLOR, color));

        GUIImage idle = new GUIImage(this, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, TEX_BUTTON_IDLE);
        idle.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        idle.add(new GUIImage(this, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, TEX_SINGLE_COLOR, color));

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
