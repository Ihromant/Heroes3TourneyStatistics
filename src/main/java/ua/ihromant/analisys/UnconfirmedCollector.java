package ua.ihromant.analisys;

import ua.ihromant.data.GameResult;
import ua.ihromant.data.Unconfirmed;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UnconfirmedCollector {
    public Unconfirmed unconfirmed(List<GameResult> items) {
        Unconfirmed result = new Unconfirmed();
        result.setGames(items.stream()
                .filter(it -> it.getConfirmer().getReportLink() == null)
                .sorted(Comparator.comparing(GameResult::getDate))
                .collect(Collectors.toList()));
        Map<String, Long> lazybones = result.getGames().stream()
                .collect(Collectors.groupingBy(it -> it.getConfirmer().getName(), Collectors.counting()));
        result.setLazybones(lazybones.entrySet().stream()
                .sorted(Comparator.<Map.Entry<String, Long>, Long>comparing(Map.Entry::getValue).reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); },
                        LinkedHashMap::new)));
        return result;
    }
}
