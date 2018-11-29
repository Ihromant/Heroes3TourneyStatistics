package ua.ihromant.analisys.builder;

import ua.ihromant.data.GameResult;
import ua.ihromant.data.Ladder;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.TreeMap;

public class ActivityBuilder extends StatisticsBuilder<Map<LocalDate, Integer>, Map<LocalDate, Integer>> {
    public ActivityBuilder() {
        super(new TreeMap<>(), Ladder::setActivities);
    }

    @Override
    public void append(GameResult res) {
        value.compute(res.getDate().with(TemporalAdjusters.firstDayOfMonth()), (key, old) -> old != null ? old + 1 : 1);
    }

    @Override
    protected Map<LocalDate, Integer> build() {
        return value;
    }
}
