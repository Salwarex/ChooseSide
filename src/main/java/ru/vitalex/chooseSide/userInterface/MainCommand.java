package ru.vitalex.chooseSide.userInterface;

import com.google.common.collect.Lists;
import org.bukkit.command.CommandSender;
import ru.vitalex.chooseSide.ChooseSide;
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

        if(args.length == 0){
            Message.send(null, sender, "&cWrite any arguments!");
            return;
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args){
        //if(args.length == 1 && sender.hasPermission("cs.command")) return Lists.newArrayList("perms");
        return Lists.newArrayList();
    }
}
