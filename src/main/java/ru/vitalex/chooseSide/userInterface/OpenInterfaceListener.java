package ru.vitalex.chooseSide.userInterface;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.vitalex.chooseSide.service.PlayerData;
import ru.vitalex.chooseSide.service.pool.PlayerDataPool;

public class OpenInterfaceListener implements Listener {

    private final static PlayerDataPool POOL = PlayerDataPool.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        PlayerData playerData = POOL.get(player.getUniqueId());
        if(playerData == null) Registrator.addPlayer(player);
        else PlayerData.setPrefix(player, playerData.getSide());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player player = e.getPlayer();
        PlayerData playerData = POOL.get(player.getUniqueId());
        if(playerData == null) e.setCancelled(true);
    }

}
