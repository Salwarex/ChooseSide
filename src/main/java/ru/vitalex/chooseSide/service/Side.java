package ru.vitalex.chooseSide.service;

import org.bukkit.Material;
import ru.vitalex.chooseSide.ChooseSide;
import ru.vitalex.chooseSide.service.pool.SidePool;
import ru.vitalex.chooseSide.service.variables.SideVariable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Side {
    private final UUID uuid;
    private String name;
    private String description;
    private Material symbol;
    private String prefix;
    private boolean active;
    private final LocalDateTime createdAt;
    private final SideStatistics statistics;
    private String dsrvRoleId;
    private String welcomeMessage;
    private double balanceCoef;

    private static final SidePool pool = SidePool.getInstance();

    private final Set<SideVariable> lastSessionChanged = new HashSet<>();

    Side(UUID uuid,
         String name,
         String description,
         Material symbol,
         String prefix,
         boolean active,
         LocalDateTime createdAt,
         SideStatistics statistics,
         String dsrvRoleId,
         String welcomeMessage,
         double balanceCoef
    ) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.symbol = symbol;
        this.prefix = prefix;
        this.active = active;
        this.createdAt = createdAt;
        this.statistics = (statistics == null ? SideStatistics.create(this) : statistics);
        this.dsrvRoleId = dsrvRoleId;
        this.welcomeMessage = welcomeMessage;
        this.balanceCoef = balanceCoef;

        pool.add(uuid, this);
    }

    public static Side create(String name, String description, Material symbol, String prefix, double balanceCoef){
        UUID uuid = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();

        Side result = new Side(uuid, name, description, symbol, prefix, true, time, null, null, null, balanceCoef);
        ChooseSide.getInstance().getDataHandler().insertSide(result);

        return result;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

        lastSessionChanged.add(SideVariable.NAME);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;

        lastSessionChanged.add(SideVariable.DESCRIPTION);
    }

    public Material getSymbol() {
        return symbol;
    }

    public void setSymbol(Material symbol) {
        this.symbol = symbol;

        lastSessionChanged.add(SideVariable.SYMBOL);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;

        lastSessionChanged.add(SideVariable.ACTIVE);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public SideStatistics getStatistics() {
        return statistics;
    }

    public Set<SideVariable> getLastSessionChanged() {
        return lastSessionChanged;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getDsrvRoleId() {
        return dsrvRoleId;
    }

    public void setDsrvRoleId(String dsrvRoleId) {
        this.dsrvRoleId = dsrvRoleId;
        lastSessionChanged.add(SideVariable.DSRV_ROLE_ID);
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
        lastSessionChanged.add(SideVariable.WELCOME_MESSAGE);
    }

    public double getBalanceCoef() {
        return balanceCoef;
    }

    public void setBalanceCoef(double balanceCoef) {
        this.balanceCoef = balanceCoef;
        lastSessionChanged.add(SideVariable.BALANCE_COEF);
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
