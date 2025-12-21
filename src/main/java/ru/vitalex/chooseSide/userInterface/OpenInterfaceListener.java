package ru.vitalex.chooseSide.userInterface;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.vitalex.chooseSide.service.pool.PlayerDataPool;

public class OpenInterfaceListener implements Listener {

    private final static PlayerDataPool POOL = PlayerDataPool.getInstance();

    @EventHandler
    public void onJoin(PlayerMoveEvent e){
        Player player = e.getPlayer();
        open(player);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player player = e.getPlayer();
        open(player);
    }

    private void open(Player player){
        if(POOL.get(player.getUniqueId()) == null){
            ChooseInterface.open(player);
        }
    }
}
