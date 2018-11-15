package ua.ihromant.analisys;

import ua.ihromant.data.GameResult;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ActivityCalculator {
    public Map<LocalDate, Long> frequencies(List<GameResult> results) {
        return results.stream()
                .collect(Collectors.groupingBy(res -> res.getDate().with(TemporalAdjusters.firstDayOfMonth()),
                        TreeMap::new,
                        Collectors.counting()));
    }
}
