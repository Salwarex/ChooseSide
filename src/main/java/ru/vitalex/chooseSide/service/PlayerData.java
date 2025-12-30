package ru.vitalex.chooseSide.service;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.AuditableRestAction;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.vitalex.chooseSide.ChooseSide;
import ru.vitalex.chooseSide.service.pool.PlayerDataPool;
import ru.vitalex.chooseSide.service.variables.PlayerDataVariable;
import ru.waxera.beeLib.utils.message.Message;

import java.time.LocalDateTime;
import java.util.*;

public class PlayerData{
    private final UUID uuid;
    private Side side;
    private LocalDateTime choseAt;
    private int changeSideTokens;
    private int alreadyTookOnlineTokens;
    private static final PlayerDataPool pool = PlayerDataPool.getInstance();

    private final Set<PlayerDataVariable> lastSessionChanged = new HashSet<>();

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

        if(side != null) {
            side.teleportToBase(player);
            result.roleManagement(side);
        }
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

    private void setSide(Side side) {
        roleManagement(side);
        this.side = side;
        this.choseAt = LocalDateTime.now();

        lastSessionChanged.add(PlayerDataVariable.SIDE);

        Player player = Bukkit.getPlayer(uuid);
        if(side != null && player != null) {
            setPrefix(player, side);

            String description = side.getDescription();

            if(description == null || description.isEmpty()) return;
            String message = """
                       %s
                       
                       """.formatted(side.getDescription());
            Message.send(ChooseSide.getInstance(), player, message);
        }
    }

    private void roleManagement(Side side) {
        if(ChooseSide.getInstance().getDiscordSRV() == null) {
            System.out.println("DiscordSRV == null");
            return;
        }

        DiscordSRV dsrvManager = ChooseSide.getInstance().getDiscordSRV();
        String discordPlayerId = dsrvManager.getAccountLinkManager().getDiscordId(uuid);
        Guild guild = dsrvManager.getMainGuild();

        if(guild == null) {
            System.out.println("guild == null");
            return;
        }
        if(discordPlayerId == null) {
            System.out.println("Player discord == null");
            return;
        }

        if(side != null){
            String sideRole = side.getDsrvRoleId();
            Role role = guild.getRoleById(sideRole);
            if(role == null) {
                System.out.println("Role == null");
                if(!sideRole.isEmpty()) throw new RuntimeException("Side role is unknown!");
                return;
            }
            System.out.println("it's okay ADDED");
            AuditableRestAction<Void> result = guild.addRoleToMember(discordPlayerId, role);
            result.queue(
                    success -> System.out.println("Роль успешно выдана"),
                    error -> System.err.println("Ошибка при выдаче роли: " + error.getMessage())
            );
        }
        else{
            String sideRole = this.side.getDsrvRoleId();
            Role role = guild.getRoleById(sideRole);
            if(role == null) {
                System.out.println("Role == null");
                if(!sideRole.isEmpty()) throw new RuntimeException("Side role is unknown!");
                return;
            }
            System.out.println("it's okay REMOVED");
            AuditableRestAction<Void> result = guild.removeRoleFromMember(discordPlayerId, role);
            result.queue(
                    success -> System.out.println("Роль успешно убрана!"),
                    error -> System.err.println("Ошибка при забирании роли: " + error.getMessage())
            );
        }
    }

    public LocalDateTime getChoseAt() {
        return choseAt;
    }

    public Set<PlayerDataVariable> getLastSessionChanged() {
        return lastSessionChanged;
    }

    public int getChangeSideTokens() {
        return changeSideTokens;
    }

    public void changeSide(Side side){
        this.setSide(null);
        if(changeSideTokens > 0){
            setSide(side);
            addTokens(-1);
        }else{
            Player player = Bukkit.getPlayer(uuid);
            if(player != null) Message.send(ChooseSide.getInstance(), "&cУ вас отсутствуют токены на смену стороны!");
        }
    }

    public void addTokens(int dTokens){
        this.setChangeSideTokens(this.changeSideTokens + dTokens);
    }

    public void setChangeSideTokens(int changeSideTokens) {
        this.changeSideTokens = changeSideTokens;
        lastSessionChanged.add(PlayerDataVariable.TOKENS);
    }

    public int getAlreadyTookOnlineTokens() {
        return alreadyTookOnlineTokens;
    }

    public void setAlreadyTookOnlineTokens(int alreadyTookOnlineTokens) {
        this.alreadyTookOnlineTokens = alreadyTookOnlineTokens;
        lastSessionChanged.add(PlayerDataVariable.ALREADY_TOOK_TOKENS);
    }

    public static void setPrefix(Player player, Side side){
        LuckPerms luckPerms = ChooseSide.getInstance().getLuckPerms();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());

        assert user != null;
        ContextSet globalContext = ImmutableContextSet.builder()
                .add("server", "global")
                .build();

        user.data().clear(globalContext, node ->
                node.getKey().startsWith("suffix.")
        );

        if(side == null) {
            luckPerms.getUserManager().saveUser(user);
            return;
        }

        Node prefixNode = Node.builder("suffix.1.%s".formatted(side.getPrefix()))
                .withContext("server", "global")
                .build();

        user.data().add(prefixNode);
        luckPerms.getUserManager().saveUser(user);
    }
}
