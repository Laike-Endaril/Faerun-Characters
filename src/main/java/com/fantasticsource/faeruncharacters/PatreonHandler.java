package com.fantasticsource.faeruncharacters;

import com.fantasticsource.faeruncharacters.config.FaerunCharactersConfig;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Table;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PatreonHandler
{
    public static final Class MCLINK_API_CLASS = ReflectionTool.getClassByName("net.dries007.mclink.api.API"), MCLINK_AUTHENTICATION_CLASS = ReflectionTool.getClassByName("net.dries007.mclink.api.Authentication");
    public static final Method MCLINK_API_GET_AUTHORIZATION_METHOD = MCLINK_API_CLASS == null ? null : ReflectionTool.getMethod(MCLINK_API_CLASS, new Class[]{Table.class, UUID[].class}, "getAuthorization");
    public static final Field MCLINK_AUTHENTICATION_EXTRA_FIELD = MCLINK_AUTHENTICATION_CLASS == null ? null : ReflectionTool.getField(MCLINK_AUTHENTICATION_CLASS, "extra");

    protected static HashMap<UUID, Integer> playerCents = new HashMap<>();

    public static int getPlayerPatreonCents(EntityPlayerMP player)
    {
        if (!Loader.isModLoaded("mclink")) return 0;


        Integer result = playerCents.get(player.getPersistentID());
        if (result != null) return result;


        //Used fully qualified references to prevent crash from import on client-side due to missing MCLink mod
        try
        {
            Table<String, String, List<String>> table = HashBasedTable.create();
            ArrayList<String> list = new ArrayList<>();
            list.add("0");
            table.put(FaerunCharactersConfig.server.patreonCreatorAccessToken, "Patreon", list);
            ImmutableMultimap map = (ImmutableMultimap) ReflectionTool.invoke(MCLINK_API_GET_AUTHORIZATION_METHOD, null, table, new UUID[]{player.getPersistentID()});
            if (map.isEmpty())
            {
                playerCents.put(player.getPersistentID(), 0);
                return 0;
            }

            ImmutableMap<String, String> extra = (ImmutableMap<String, String>) ReflectionTool.get(MCLINK_AUTHENTICATION_EXTRA_FIELD, map.values().iterator().next());
            if (extra.isEmpty())
            {
                playerCents.put(player.getPersistentID(), 0);
                return 0;
            }

            result = Integer.parseInt(extra.values().iterator().next());
            playerCents.put(player.getPersistentID(), result);
            return result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean isPlayerPremium(EntityPlayerMP player)
    {
        if (Tools.contains(FaerunCharactersConfig.server.additionalPremiumPlayers, player.getPersistentID().toString())) return true;
        return getPlayerPatreonCents(player) >= FaerunCharactersConfig.server.centsForPremium;
    }

    @SubscribeEvent
    public static void PlayerLogoff(PlayerEvent.PlayerLoggedOutEvent event)
    {
        playerCents.remove(event.player.getPersistentID());
    }
}
