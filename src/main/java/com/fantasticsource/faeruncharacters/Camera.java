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
    public static double x = 0, y = 0, z = 0;

    protected static boolean inPass = false;
    protected static OriginalData original = new OriginalData();

    @SubscribeEvent
    public static void fog(EntityViewRenderEvent.RenderFogEvent event)
    {
        if (active && !inPass)
        {
            inPass = true;


            //Mode
            Minecraft mc = Minecraft.getMinecraft();
            GameSettings gs = mc.gameSettings;
            original.cameraMode = gs.thirdPersonView;
            gs.thirdPersonView = 1;


            //Position
            EntityPlayer player = mc.player;
            original.posX = player.posX;
            original.posY = player.posY;
            original.posZ = player.posZ;
            original.prevPosX = player.prevPosX;
            original.prevPosY = player.prevPosY;
            original.prevPosZ = player.prevPosZ;
            player.posX = x;
            player.prevPosX = x;
            player.posY = y;
            player.prevPosY = y;
            player.posZ = z;
            player.prevPosZ = z;
        }
    }

    @SubscribeEvent
    public static void renderEntity(RenderLivingEvent.Pre<EntityPlayer> event)
    {
        if (inPass && event.getEntity() == Minecraft.getMinecraft().player)
        {
            inPass = false;

            //Mode
            Minecraft mc = Minecraft.getMinecraft();
            GameSettings gs = mc.gameSettings;
            gs.thirdPersonView = original.cameraMode;


            //Position
            EntityPlayer player = mc.player;
            player.posX = original.posX;
            player.prevPosX = original.prevPosX;
            player.posY = original.posY;
            player.prevPosY = original.prevPosY;
            player.posZ = original.posZ;
            player.prevPosZ = original.prevPosZ;
        }
    }

    protected static class OriginalData
    {
        int cameraMode;
        double posX, posY, posZ, prevPosX, prevPosY, prevPosZ;
    }
}
