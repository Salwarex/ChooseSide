package ru.vitalex.chooseSide.service.variables;

import org.bukkit.Location;
import org.bukkit.Material;
import ru.vitalex.chooseSide.service.variables.set.SetSideMethod;

public enum SideVariable implements VariableEnum{
    NAME("name", (side, value) -> side.setName((String) value)),
    DESCRIPTION("description", (side, value) -> side.setDescription((String) value)),
    SYMBOL("symbol", (side, value) -> side.setSymbol((Material) value)),
    ACTIVE("active", (side, value) -> side.setActive((boolean) value)),
    PREFIX("prefix", (side, value) -> side.setPrefix((String) value)),
    DSRV_ROLE_ID("dsrv_role_id", (side, value) -> side.setDsrvRoleId((String) value)),
    WELCOME_MESSAGE("welcome_message", (side, value) -> side.setWelcomeMessage((String) value)),
    BALANCE_COEF("balance_coef", (side, value) -> side.setBalanceCoef((double) value)),
    BASE_LOCATION("base_location", (side, value) -> side.setBaseLocation((Location) value));

    final String techName;
    final SetSideMethod function;

    SideVariable(String techName, SetSideMethod function){
        this.techName = techName;
        this.function = function;
    }

    public String getTechName() {
        return techName;
    }

    public SetSideMethod function() {
        return function;
    }
}
