package ua.ihromant.analisys.builder;

import ua.ihromant.data.GameResult;
import ua.ihromant.data.Ladder;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerActivityBuilder extends StatisticsBuilder<Map<LocalDate, Set<String>>, Map<LocalDate, Integer>> {
    private LocalDate current;
    public PlayerActivityBuilder(LocalDate current) {
        super(new TreeMap<>(), Ladder::setPlayerActivities);
        this.current = current;
    }

    @Override
    public void append(GameResult res) {
        LocalDate date = res.getDate().with(TemporalAdjusters.firstDayOfMonth());
        if (date.isBefore(current)) {
            value.compute(date, (key, old) -> append(old, res.getConfirmer().getName(), res.getReporter().getName()));
        }
    }

    private Set<String> append(Set<String> old, String... players) {
        Set<String> result = old != null ? old : new HashSet<>();
        result.addAll(Arrays.asList(players));
        return result;
    }

    @Override
    protected Map<LocalDate, Integer> build() {
        return value.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size(), (a, b) -> a, TreeMap::new));
    }
}
