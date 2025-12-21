package ru.vitalex.chooseSide.service.pool;

import org.bukkit.scheduler.BukkitRunnable;
import ru.vitalex.chooseSide.ChooseSide;
import ru.vitalex.chooseSide.service.Side;
import ru.waxera.beeLib.utils.data.pools.map.IrreplaceableMapPool;

import java.util.Set;
import java.util.UUID;

public class SidePool extends IrreplaceableMapPool<UUID, Side> {
    private static SidePool instance = null;
    private static boolean updateTaskStarted = false;

    public static SidePool getInstance(){
        instance = instance == null ? new SidePool() : instance;
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
                for(Side side : storage.values()){
                    if(!side.getLastSessionChanged().isEmpty() || !side.getStatistics().getLastSessionChanged().isEmpty())
                        ChooseSide.getInstance().getDataHandler().saveSide(side);
                }
            }
        };

        runnable.runTaskTimer(ChooseSide.getInstance(), 0L,
                20L * ChooseSide.getInstance().getConfig().getInt("database.synchronize-interval-seconds",
                        300));
        updateTaskStarted = true;
    }
}
