package ru.vitalex.chooseSide.service.variables.set;

import ru.vitalex.chooseSide.service.PlayerData;

@FunctionalInterface
public interface SetPlayerDataMethod {
    void set(PlayerData playerData, Object value);
}
