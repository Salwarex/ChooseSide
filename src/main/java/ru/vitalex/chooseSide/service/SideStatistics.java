package ru.vitalex.chooseSide.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SideStatistics {
    private final Side side;
    private final Map<RecordingStatistic, Double> values;

    private final Set<RecordingStatistic> lastSessionChanged = new HashSet<>();

    SideStatistics(Side side, Map<RecordingStatistic, Double> values){
        this.side = side;
        this.values = values;
    }

    public static SideStatistics create(Side side){
        SideStatistics result = new SideStatistics(side, new HashMap<>());
        result.initStatistics();
        return result;
    }

    private void initStatistics() {
        for(RecordingStatistic statistic : RecordingStatistic.values()){
            setValue(statistic, 0);
        }
    }

    public Double getValue(RecordingStatistic key){
        if(values.containsKey(key)) return values.get(key);
        return Double.NaN;
    }

    public void addValue(RecordingStatistic key, double lambda){
        if(!values.containsKey(key)) values.put(key, 0.0);
        values.replace(key, values.get(key) + lambda);

        lastSessionChanged.add(key);
    }

    public void setValue(RecordingStatistic key, double value){
        if(!values.containsKey(key)) values.put(key, value);
        else values.replace(key, value);

        lastSessionChanged.add(key);
    }

    public Side getSide() {
        return side;
    }

    public Set<RecordingStatistic> getLastSessionChanged() {
        return lastSessionChanged;
    }
}
