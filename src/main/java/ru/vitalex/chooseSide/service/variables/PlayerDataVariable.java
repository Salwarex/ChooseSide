package ru.vitalex.chooseSide.service.variables;

import ru.vitalex.chooseSide.service.Side;
import ru.vitalex.chooseSide.service.variables.set.SetPlayerDataMethod;

public enum PlayerDataVariable implements VariableEnum{
    SIDE("side_uuid",
            (playerData, value) -> playerData.changeSide((Side) value)),
    TOKENS("change_side_tokens",
            (playerData, value) -> playerData.setChangeSideTokens((int) value)),
    ALREADY_TOOK_TOKENS("already_took_online_tokens",
            (playerData, value) -> playerData.setAlreadyTookOnlineTokens((int) value));

    final String techName;
    final SetPlayerDataMethod function;

    PlayerDataVariable(String techName, SetPlayerDataMethod function){
        this.techName = techName;
        this.function = function;
    }

    public String getTechName() {
        return techName;
    }

    public SetPlayerDataMethod function() {
        return function;
    }
}
