package ru.vitalex.chooseSide.userInterface;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.vitalex.chooseSide.ChooseSide;
import ru.vitalex.chooseSide.service.pool.PlayerDataPool;

import java.util.HashMap;
import java.util.Map;

public class Registrator {
    private static Map<Player, Boolean> sessions = new HashMap<>();

    public static void start(){
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                Map<Player, Boolean> updated = new HashMap<>();
                for(Player player : sessions.keySet()){
                    if(!player.isOnline() || PlayerDataPool.getInstance().contains(player.getUniqueId())) continue;
                    boolean pause = sessions.get(player);
                    updated.put(player, pause);

                    if(!pause) ChooseInterface.open(player);
                }
                sessions = updated;
            }
        };

        runnable.runTaskTimer(ChooseSide.getInstance(), 0L,
                20L * ChooseSide.getInstance().getConfig().getInt("registration-task-interval-seconds",
                        5));
    }

    public static void addPlayer(Player player){
        sessions.put(player, false);
    }

    public static void pause(Player player){
        if(!sessions.containsKey(player)) return;
        sessions.replace(player, true);
    }

    public static void unpause(Player player){
        if(!sessions.containsKey(player)) return;
        sessions.replace(player, false);
    }
}
