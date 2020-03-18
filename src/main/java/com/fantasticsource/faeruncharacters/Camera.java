package com.fantasticsource.faeruncharacters;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Camera
{
    static
    {
        MinecraftForge.EVENT_BUS.register(Camera.class);
    }

    public static boolean active = false;

    protected static boolean inPass = false;
    protected static OriginalData original = new OriginalData();

    @SubscribeEvent
    public static void fog(EntityViewRenderEvent.RenderFogEvent event)
    {
        if (active && !inPass)
        {
            inPass = true;

            Minecraft mc = Minecraft.getMinecraft();
            GameSettings gs = mc.gameSettings;
            original.cameraMode = gs.thirdPersonView;
            gs.thirdPersonView = 1;
        }
    }

    @SubscribeEvent
    public static void renderEntity(RenderLivingEvent.Pre<EntityPlayer> event)
    {
        if (inPass && event.getEntity() == Minecraft.getMinecraft().player)
        {
            inPass = false;

            Minecraft mc = Minecraft.getMinecraft();
            GameSettings gs = mc.gameSettings;
            gs.thirdPersonView = original.cameraMode;
        }
    }

    protected static class OriginalData
    {
        int cameraMode;
    }
}
