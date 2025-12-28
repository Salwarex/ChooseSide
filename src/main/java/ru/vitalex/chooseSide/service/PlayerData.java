package ru.vitalex.chooseSide.service;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
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
    private int changeSideTokens;
    private int alreadyTookOnlineTokens;
    private static final PlayerDataPool pool = PlayerDataPool.getInstance();

    private final Set<PlayerDataVariables> lastSessionChanged = new HashSet<>();

    PlayerData(UUID uuid, Side side, LocalDateTime choseAt, int changeSideTokens, int alreadyTookOnlineTokens){
        this.uuid = uuid;
        this.side = side;
        this.choseAt = choseAt;
        this.changeSideTokens = changeSideTokens;
        this.alreadyTookOnlineTokens = alreadyTookOnlineTokens;

        pool.add(uuid, this);
    }

    public static PlayerData create(Player player, Side side){
        UUID playerUuid = player.getUniqueId();

        PlayerData result = pool.contains(playerUuid) ? pool.get(playerUuid) :
                new PlayerData(
                        player.getUniqueId(),
                        side,
                        side != null ? LocalDateTime.now() : null,
                        0, 0
                );
        ChooseSide.getInstance().getDataHandler().insertPlayerData(result);
        setPrefix(player, side);
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

    public int getChangeSideTokens() {
        return changeSideTokens;
    }

    public void addTokens(int dTokens){
        this.setChangeSideTokens(this.changeSideTokens + dTokens);
    }

    public void setChangeSideTokens(int changeSideTokens) {
        this.changeSideTokens = changeSideTokens;
        lastSessionChanged.add(PlayerDataVariables.TOKENS);
    }

    public int getAlreadyTookOnlineTokens() {
        return alreadyTookOnlineTokens;
    }

    public void setAlreadyTookOnlineTokens(int alreadyTookOnlineTokens) {
        this.alreadyTookOnlineTokens = alreadyTookOnlineTokens;
        lastSessionChanged.add(PlayerDataVariables.ALREADY_TOOK_TOKENS);
    }

    public static void setPrefix(Player player, Side side){
        LuckPerms luckPerms = ChooseSide.getInstance().getLuckPerms();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        Node prefixNode = Node.builder("suffix.1.%s".formatted(side.getPrefix()))
                .withContext("server", "global")
                .build();
        assert user != null;
        user.data().add(prefixNode);
        luckPerms.getUserManager().saveUser(user);
    }
}
