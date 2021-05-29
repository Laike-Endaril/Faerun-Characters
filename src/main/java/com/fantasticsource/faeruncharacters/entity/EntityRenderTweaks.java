package com.fantasticsource.faeruncharacters.entity;

import com.fantasticsource.faeruncharacters.Network;
import com.fantasticsource.mctools.MCTools;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.UUID;

public class EntityRenderTweaks
{
    public static final HashMap<UUID, Double> ENTITY_SCALES = new HashMap<>();


    public static void refreshScale(EntityLivingBase entity)
    {
        MCTools.sendToAllTracking(Network.WRAPPER, new Network.CharacterScalePacket(entity), entity);
    }


    @SubscribeEvent
    public static void startTracking(PlayerEvent.StartTracking event)
    {
        Entity entity = event.getTarget();
        if (!(entity instanceof EntityLivingBase)) return;

        Network.WRAPPER.sendTo(new Network.CharacterScalePacket((EntityLivingBase) entity), (EntityPlayerMP) event.getEntityPlayer());
    }

    @SubscribeEvent
    public static void entityJoinWorld(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (!(entity instanceof EntityPlayerMP)) return;

        Network.WRAPPER.sendTo(new Network.CharacterScalePacket((EntityLivingBase) entity), (EntityPlayerMP) entity);
    }


    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void renderLiving(RenderLivingEvent.Pre event)
    {
        EntityLivingBase entity = event.getEntity();
        if (!(entity instanceof EntityPlayer)) return;

        double scale = ENTITY_SCALES.getOrDefault(entity.getUniqueID(), 1d);
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void renderLiving2(RenderLivingEvent.Pre event)
    {
        EntityLivingBase entity = event.getEntity();
        if (!(entity instanceof EntityPlayer) || !event.isCanceled()) return;

        GlStateManager.popMatrix();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderLiving3(RenderLivingEvent.Post event)
    {
        EntityLivingBase entity = event.getEntity();
        if (!(entity instanceof EntityPlayer)) return;

        GlStateManager.popMatrix();
    }
}
