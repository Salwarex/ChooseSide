package ru.vitalex.chooseSide.service.variables.set;

import ru.vitalex.chooseSide.service.Side;

@FunctionalInterface
public interface SetSideMethod {
    void set(Side side, Object value);
}
