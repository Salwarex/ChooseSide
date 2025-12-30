package ru.vitalex.chooseSide.userInterface;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.vitalex.chooseSide.ChooseSide;
import ru.vitalex.chooseSide.service.PlayerData;
import ru.vitalex.chooseSide.service.Side;
import ru.vitalex.chooseSide.service.pool.PlayerDataPool;
import ru.vitalex.chooseSide.service.pool.SidePool;
import ru.vitalex.chooseSide.service.variables.PlayerDataVariable;
import ru.vitalex.chooseSide.service.variables.SideVariable;
import ru.waxera.beeLib.utils.command.AbstractCommand;
import ru.waxera.beeLib.utils.message.Message;

import java.util.*;
import java.util.stream.Stream;

public class MainCommand extends AbstractCommand {
    private final static ChooseSide PLUGIN = ChooseSide.getInstance();

    public MainCommand() {super(PLUGIN, "choose",
            "chooseteam", "ct", "chooseside", "cs");}

    @Override
    public void execute(CommandSender sender, String s, String[] args){
        Player player;
        if(!(sender instanceof ConsoleCommandSender)) player = Bukkit.getPlayer(sender.getName());
        else player = null;

        if(args.length == 0){
            Message.send(null, sender, "&cWrite any arguments!");
            return;
        }

        if("change".equalsIgnoreCase(args[0])){
            if(player == null) throw new RuntimeException("Player is null!");
            PlayerData data = PlayerDataPool.getInstance().get(player.getUniqueId());
            int tokens = data.getChangeSideTokens();

            if(tokens == 0){
                Message.send(PLUGIN, player, "&cУ вас недостаточно токенов, чтобы сменить сторону!");
                return;
            }

            ChooseInterface.open(player);
            return;
        }

        if("identify".equalsIgnoreCase(args[0])){
            if(!sender.hasPermission("cs.admin")){
                Message.send(null, sender, "&cYou can't do it!");
                return;
            }

            if(args.length < 3){
                Message.send(null, sender, "&cThis commands needs 3 arguments: &eidentify <player/side> <uuid>");
                return;
            }

            String object = args[1];

            UUID uuid;
            try{
                uuid = UUID.fromString(args[2]);
            }catch (IllegalArgumentException e){
                Message.send(null, sender, "&cВведенный UUID не соответствует формату!");
                return;
            }

            String result = null;

            if("player".equalsIgnoreCase(object)){
                PlayerDataPool pool = PlayerDataPool.getInstance();
                if(!pool.contains(uuid)){
                    Message.send(null, sender, "&cИгрока с данным UUID не существует!");
                    return;
                }

                result = PlayerDataPool.getPlayerName(uuid);
            } else if ("side".equalsIgnoreCase(object)) {
                SidePool pool = SidePool.getInstance();
                if(!pool.contains(uuid)){
                    Message.send(null, sender, "&cСтороны с данным UUID не существует!");
                    return;
                }

                result = SidePool.getSideName(uuid);
            }else {
                Message.send(null, sender,
                        "&cВторой аргумент данной команды должен принимать \"group\" или \"player\"");
                return;
            }

            Message.send(PLUGIN, sender, "&eИмя рассматриваемого объекта: %s".formatted(result));
            return;
        }

        if("edit".equalsIgnoreCase(args[0])){
            if(!sender.hasPermission("cs.admin")){
                Message.send(null, sender, "&cYou can't do it!");
                return;
            }

            if(args.length < 4){
                Message.send(null, sender, "&cThis commands needs 4 arguments: &eedit <uuid> <attribute> <value>");
                return;
            }

            UUID uuid;

            try{
                uuid = UUID.fromString(args[1]);
            }catch (IllegalArgumentException e){
                Message.send(null, sender, "&cВведенный UUID не соответствует формату!");
                return;
            }

            if(!SidePool.getInstance().contains(uuid)){
                Message.send(null, sender, "&cСтороны с данным UUID не существует!");
                return;
            }
            Side side = SidePool.getInstance().get(uuid);

            SideVariable sideVariable;
            try{
                sideVariable = SideVariable.valueOf(args[2]);;
            }catch (IllegalArgumentException e){
                Message.send(null, sender, "&cВы ввели несуществующий аттрибут!");
                return;
            }

            String value;
            if(sideVariable != SideVariable.BASE_LOCATION){
                value = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

                sideVariable.function().set(side, value);
            }
            else{
                assert player != null;
                Location location = player.getLocation();

                value = "%s, %d, %d, %d".formatted(
                        location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());

                sideVariable.function().set(side, location);
            }


            Message.send(null, sender, "&cВы успешно установили значение для аттрибута %s = %s!"
                    .formatted(sideVariable.toString(), value));
            return;
        }

        if("player".equalsIgnoreCase(args[0])){
            if(!sender.hasPermission("cs.admin")){
                Message.send(null, sender, "&cYou can't do it!");
                return;
            }

            if(args.length < 4){
                Message.send(null, sender, "&cThis commands needs 4 arguments: &eplayer <uuid> <attribute> <value>");
                return;
            }

            UUID uuid;

            try{
                uuid = UUID.fromString(args[1]);
            }catch (IllegalArgumentException e){
                Message.send(null, sender, "&cВведенный UUID не соответствует формату!");
                return;
            }

            if(!SidePool.getInstance().contains(uuid)){
                Message.send(null, sender, "&cИгрока с данным UUID не существует!");
                return;
            }
            PlayerData side = PlayerDataPool.getInstance().get(uuid);

            PlayerDataVariable pdVariable;
            try{
                pdVariable = PlayerDataVariable.valueOf(args[2]);;
            }catch (IllegalArgumentException e){
                Message.send(null, sender, "&cВы ввели несуществующий аттрибут!");
                return;
            }
            String value = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

            pdVariable.function().set(side, value);

            Message.send(null, sender, "&aВы успешно установили значение для аттрибута %s = %s!"
                    .formatted(pdVariable.toString(), value));
            return;
        }

        if("add".equalsIgnoreCase(args[0])){
            if(!sender.hasPermission("cs.admin")){
                Message.send(null, sender, "&cYou can't do it!");
                return;
            }

            if(args.length < 3){
                Message.send(null, sender, "&cThis commands needs 3 arguments: &eadd <prefix> <name>");
                return;
            }

            assert player != null;
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            if(mainHand.getType() == Material.AIR){
                Message.send(null, sender, "&cIn your hand should be some item stack.");
                return;
            }
            String prefix = args[1];

            String name = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

            Side.create(name, " ", mainHand.getType(), prefix, 0.5, player.getLocation());
            Message.send(null, sender, "&aSide successfully created!");
            return;
        }

        if("pause".equalsIgnoreCase(args[0])){
            if(!sender.hasPermission("cs.admin")){
                Message.send(null, sender, "&cYou can't do it!");
                return;
            }

            Registrator.pause(player);
            Message.send(null, sender, "&aВы успешно поставили на паузу открытие выбора!");
            return;
        }

        if("unpause".equalsIgnoreCase(args[0])){
            if(!sender.hasPermission("cs.admin")){
                Message.send(null, sender, "&cYou can't do it!");
                return;
            }

            Registrator.unpause(player);
            Message.send(null, sender, "&aВы успешно сняли с паузы открытие выбора!");
            return;
        }

        Message.send(null, sender, "&cUnknown command!");
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args){
        if(args.length == 1 && sender.hasPermission("cs.admin"))
            return Lists.newArrayList("add", "change", "edit", "player", "identify", "pause", "unpause");
        if(args.length == 1) return Lists.newArrayList("change");
        if((args.length == 2 && "edit".equalsIgnoreCase(args[0]))
                || (args.length == 3 && "identify".equalsIgnoreCase(args[0]) && "side".equalsIgnoreCase(args[1])))
            return SidePool.getInstance().keys().stream()
                    .map(UUID::toString)
                    .toList();
        if(args.length == 3 && "edit".equalsIgnoreCase(args[0]))
            return Stream.of(SideVariable.values())
                    .map(SideVariable::toString)
                    .toList();

        if(args.length == 2 && "player".equalsIgnoreCase(args[0])
                || (args.length == 3 && "identify".equalsIgnoreCase(args[0]) && "player".equalsIgnoreCase(args[1])))
            return PlayerDataPool.getInstance().keys().stream()
                    .map(UUID::toString)
                    .toList();
        if(args.length == 3 && "player".equalsIgnoreCase(args[0]))
            return Stream.of(PlayerDataVariable.values())
                    .map(PlayerDataVariable::toString)
                    .toList();
        return Lists.newArrayList();
    }
}
