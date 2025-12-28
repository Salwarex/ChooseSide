package ru.vitalex.chooseSide.userInterface;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.vitalex.chooseSide.ChooseSide;
import ru.vitalex.chooseSide.service.Side;
import ru.waxera.beeLib.utils.command.AbstractCommand;
import ru.waxera.beeLib.utils.message.Message;

import java.util.*;

public class MainCommand extends AbstractCommand {
    private final static ChooseSide PLUGIN = ChooseSide.getInstance();

    public MainCommand() {super(PLUGIN, "choose",
            "chooseteam", "ct", "chooseside", "cs");}

    @Override
    public void execute(CommandSender sender, String s, String[] args){
        if(!sender.hasPermission("chooseside.command")){
            Message.send(null, sender, "&cYou can't do it!");
            return;
        }

        Player player;
        if(!(sender instanceof ConsoleCommandSender)) player = Bukkit.getPlayer(sender.getName());
        else player = null;

        if(args.length == 0){
            Message.send(null, sender, "&cWrite any arguments!");
            return;
        }

        if(args[0].equalsIgnoreCase("add")){
            if(args.length < 3){
                Message.send(null, sender, "&cThis commands needs 2 arguments: &eadd <prefix> <name>");
                return;
            }

            ItemStack mainHand = player.getInventory().getItemInMainHand();
            if(mainHand.getType() == Material.AIR){
                Message.send(null, sender, "&cIn your hand should be some itemstack.");
                return;
            }
            String prefix = args[1];

            String name = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

            Side.create(name, " ", mainHand.getType(), prefix, 0.5);
            Message.send(null, sender, "&aSide successfully created!");
            return;
        }

        Message.send(null, sender, "&cUnknown command!");
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args){
        if(args.length == 1 && sender.hasPermission("cs.command")) return Lists.newArrayList("add");
        return Lists.newArrayList();
    }
}
