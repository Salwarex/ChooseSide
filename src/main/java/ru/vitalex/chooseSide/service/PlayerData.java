package ru.vitalex.chooseSide.service;

import org.bukkit.entity.Player;
import ru.vitalex.chooseSide.ChooseSide;
import ru.vitalex.chooseSide.service.pool.PlayerDataPool;
import ru.vitalex.chooseSide.service.variables.PlayerDataVariables;

import java.time.LocalDateTime;
import java.util.*;

public class PlayerData {
    private final UUID uuid;
    private Side side;
    private LocalDateTime choseAt;
    private static final PlayerDataPool pool = PlayerDataPool.getInstance();

    private final Set<PlayerDataVariables> lastSessionChanged = new HashSet<>();

    PlayerData(UUID uuid, Side side, LocalDateTime choseAt){
        this.uuid = uuid;
        this.side = side;
        this.choseAt = choseAt;

        pool.add(uuid, this);
    }

    public static PlayerData create(Player player, Side side){
        UUID playerUuid = player.getUniqueId();

        PlayerData result = pool.contains(playerUuid) ? pool.get(playerUuid) :
                new PlayerData(
                        player.getUniqueId(),
                        side,
                        side != null ? LocalDateTime.now() : null
                );
        ChooseSide.getInstance().getDataHandler().insertPlayerData(result);

        return result;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
        this.choseAt = LocalDateTime.now();

        lastSessionChanged.add(PlayerDataVariables.SIDE);
    }

    public LocalDateTime getChoseAt() {
        return choseAt;
    }

    public Set<PlayerDataVariables> getLastSessionChanged() {
        return lastSessionChanged;
    }
}
