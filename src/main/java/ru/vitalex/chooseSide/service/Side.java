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
    private boolean active;
    private final LocalDateTime createdAt;
    private final SideStatistics statistics;

    private static final SidePool pool = SidePool.getInstance();

    private final Set<SideVariable> lastSessionChanged = new HashSet<>();

    Side(UUID uuid,
         String name,
         String description,
         Material symbol,
         boolean active,
         LocalDateTime createdAt,
         SideStatistics statistics
    ) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.symbol = symbol;
        this.active = active;
        this.createdAt = createdAt;
        this.statistics = (statistics == null ? SideStatistics.create(this) : statistics);

        pool.add(uuid, this);
    }

    public static Side create(String name, String description, Material symbol){
        UUID uuid = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();

        Side result = new Side(uuid, name, description, symbol, true, time, null);
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
}
