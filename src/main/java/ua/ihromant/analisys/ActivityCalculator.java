package ua.ihromant.analisys;

import ua.ihromant.data.GameResult;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ActivityCalculator implements Function<List<GameResult>, Map<LocalDate, Long>> {
    @Override
    public Map<LocalDate, Long> apply(List<GameResult> results) {
        return results.stream()
                .collect(Collectors.groupingBy(res -> res.getDate().with(TemporalAdjusters.firstDayOfMonth()),
                        TreeMap::new,
                        Collectors.counting()));
    }
}
