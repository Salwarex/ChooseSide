package ru.vitalex.chooseSide.service.pool;

import github.scarsz.discordsrv.util.PlayerUtil;
import org.bukkit.scheduler.BukkitRunnable;
import ru.vitalex.chooseSide.ChooseSide;
import ru.vitalex.chooseSide.service.PlayerData;
import ru.waxera.beeLib.BeeLib;
import ru.waxera.beeLib.utils.data.pools.map.IrreplaceableMapPool;
import ru.waxera.beeLib.utils.player.PlayerPool;

import java.util.Set;
import java.util.UUID;

public class PlayerDataPool extends IrreplaceableMapPool<UUID, PlayerData> {
    private static PlayerDataPool instance = null;
    private static boolean updateTaskStarted = false;

    public static PlayerDataPool getInstance(){
        instance = instance == null ? new PlayerDataPool() : instance;
        if(!updateTaskStarted) instance.startUpdateTask();
        return instance;
    }

    public int size(){
        return storage.size();
    }

    public Set<UUID> keys(){
        return storage.keySet();
    }

    private void startUpdateTask(){
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                for(PlayerData playerData : storage.values()){
                    if(!playerData.getLastSessionChanged().isEmpty())
                        ChooseSide.getInstance().getDataHandler().savePlayerData(playerData);
                }
            }
        };

        runnable.runTaskTimer(ChooseSide.getInstance(), 0L,
                20L * ChooseSide.getInstance().getConfig().getInt("database.synchronize-interval-seconds",
                        300));
        updateTaskStarted = true;
    }

    public static String getPlayerName(UUID uuid){
        return PlayerPool.getInstance().get(uuid).getName();
    }

//    public static String getUuidByPlayerName(String playerName){ //всё очень плохо в BeeLib, пофиксить потом
//        PlayerData resultPd;
//        for(String name : PlayerPool.getInstance().){
//            if(name.equalsIgnoreCase(playerName)){
//                resultPd = PlayerPool.getInstance().get()
//            }
//        }
//    }
}
