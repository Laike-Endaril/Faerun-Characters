package com.fantasticsource.faeruncharacters;

import com.fantasticsource.mctools.PlayerData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nullable;
import java.util.*;

import static com.fantasticsource.faeruncharacters.FaerunCharacters.MODID;
import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.WHITE;

public class Commands extends CommandBase
{
    private static LinkedHashMap<String, Integer> subcommands = new LinkedHashMap<>();

    static
    {
        subcommands.put("cc", 2);
    }


    @Override
    public String getName()
    {
        return MODID;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        if (sender.canUseCommand(2, getName()))
        {
            return AQUA + "/" + getName() + " cc [playername]" + WHITE + " - " + I18n.translateToLocalFormatted(MODID + ".cmd.cc.comment");
        }

        return I18n.translateToLocalFormatted("commands.generic.permission");
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        if (args.length == 0) sender.getCommandSenderEntity().sendMessage(new TextComponentString(getUsage(sender)));
        else subCommand(sender, args);
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        ArrayList<String> result = new ArrayList<>();

        String partial = args[args.length - 1];
        if (args.length == 1)
        {
            for (Map.Entry<String, Integer> entry : subcommands.entrySet())
            {
                if (sender.canUseCommand(entry.getValue(), getName())) result.add(entry.getKey());
            }
        }
        else if (args.length == 2)
        {
            switch (args[0])
            {
                case "cc":
                    result.addAll(Arrays.asList(server.getPlayerList().getOnlinePlayerNames()));
                    break;
            }
        }

        if (partial.length() != 0) result.removeIf(k -> partial.length() > k.length() || !k.substring(0, partial.length()).equalsIgnoreCase(partial));
        return result;
    }

    private void subCommand(ICommandSender sender, String[] args)
    {
        String cmd = args[0];

        if (!sender.canUseCommand(subcommands.get(cmd), getName()))
        {
            notifyCommandListener(sender, this, "commands.generic.permission");
            return;
        }

        switch (cmd)
        {
            case "cc":
                if (args.length > 2)
                {
                    notifyCommandListener(sender, this, getUsage(sender));
                    return;
                }
                if (args.length == 1 && !(sender instanceof EntityPlayerMP))
                {
                    notifyCommandListener(sender, this, MODID + ".error.notPlayer");
                    return;
                }

                EntityPlayerMP target = args.length == 1 ? (EntityPlayerMP) sender : (EntityPlayerMP) PlayerData.getEntity(args[1]);
                if (target == null)
                {
                    notifyCommandListener(sender, this, getUsage(sender));
                    return;
                }

                CharacterCustomization.goToCC(target);

                break;


            default:
                notifyCommandListener(sender, this, getUsage(sender));
                break;
        }
    }
}
