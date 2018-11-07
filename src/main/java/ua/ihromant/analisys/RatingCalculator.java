package ua.ihromant.analisys;

import ua.ihromant.data.GameResult;
import ua.ihromant.data.Ladder;
import ua.ihromant.data.StatisticsItem;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RatingCalculator {
    private static final Predicate<GameResult> CONFIRMED = res -> res.getConfirmer().getReportLink() != null;
    private final String name;
    private final Predicate<GameResult> filter;

    private static Predicate<GameResult> themeFilter(String topicId) {
        return res -> {
            int counter = res.getTourneyLink().lastIndexOf('=');
            return topicId.equals(res.getTourneyLink().substring(counter + 1));
        };
    }

    public static RatingCalculator forTheme(String name, int id) {
        return new RatingCalculator(name, themeFilter(String.valueOf(id)));
    }

    public RatingCalculator() {
        this("Overall rating", r -> true);
    }

    public RatingCalculator(String name, Predicate<GameResult> filter) {
        this.name = name;
        this.filter = filter;
    }

    public Ladder calculate(List<GameResult> results) {
        Ladder ladder = new Ladder();
        ladder.setName(name);
        Map<String, StatisticsItem> statistics = results.stream()
                .filter(CONFIRMED.and(filter))
                .flatMap(res -> Stream.of(res.getConfirmer().getName(), res.getReporter().getName()))
                .distinct().collect(Collectors.toMap(Function.identity(), name -> {
                    StatisticsItem it = new StatisticsItem();
                    it.setName(name);
                    it.setRating(1500);
                    return it;
                }));
        results.stream()
                .filter(CONFIRMED.and(filter))
                .sorted(Comparator.comparing(GameResult::getDate))
                .forEach(res -> {
                    StatisticsItem reporterStats = statistics.get(res.getReporter().getName());
                    StatisticsItem confirmerStats = statistics.get(res.getConfirmer().getName());
                    switch (res.getResult()) {
                        case DEF:
                            recalculateWin(reporterStats, confirmerStats, res.cloned());
                            break;
                        case LOSE:
                            recalculateWin(confirmerStats, reporterStats, res.reversed());
                            break;
                        default:
                            recalculateDraw(reporterStats, confirmerStats, res.cloned());
                            break;
                    }
                });
        ladder.setItems(statistics.entrySet().stream()
                .sorted(Comparator.<Map.Entry<String, StatisticsItem>, Integer>comparing(e1 -> e1.getValue().getRating())
                        .reversed())
                .collect(Collectors.toMap(e -> e.getKey().toLowerCase(),
                        Map.Entry::getValue,
                        (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); },
                        LinkedHashMap::new)));
        return ladder;
    }

    private void recalculateWin(StatisticsItem winner, StatisticsItem loser, GameResult result) {
        float f1 = Math.max(-1, Math.min(1.0f * (winner.getRating() - loser.getRating()) / 400, 1));
        float f2 = Math.max(-1, Math.min(1.0f * (loser.getRating() - winner.getRating()) / 400, 1));

        int wp2 = Math.max(1, Math.round(16 * (1 + f2)));
        int lp1 = Math.min(-1, Math.round(16 * (-1 + f1)));

        result.setPreviousReporter(winner.getRating());
        result.setPreviousConfirmer(loser.getRating());
        result.setReporterChange(wp2);
        result.setConfirmerChange(lp1);

        winner.setRating(winner.getRating() + result.getReporterChange());
        winner.setWins(winner.getWins() + 1);
        winner.getResults().add(0, result);

        loser.setRating(loser.getRating() + result.getConfirmerChange());
        loser.setLoses(loser.getLoses() + 1);
        loser.getResults().add(0, result.reversed());
    }

    private void recalculateDraw(StatisticsItem first, StatisticsItem second, GameResult result) {
        float f1 = Math.max(-1, Math.min(1.0f * (first.getRating() - second.getRating()) / 400, 1));
        float f2 = Math.max(-1, Math.min(1.0f * (second.getRating() - first.getRating()) / 400, 1));

        float wp1 = Math.max(1, 16 * (1 + f1));
        float wp2 = Math.max(1, 16 * (1 + f2));

        result.setReporterChange(Math.round(((wp2 - wp1) / 2 + 0.5f)));
        result.setConfirmerChange(Math.round(((wp1 - wp2) / 2 + 0.5f)));
        result.setPreviousReporter(first.getRating());
        result.setPreviousConfirmer(second.getRating());

        first.setRating(first.getRating() + result.getConfirmerChange());
        first.setDraws(first.getDraws() + 1);
        first.getResults().add(0, result);

        second.setRating(second.getRating() + result.getConfirmerChange());
        second.setDraws(second.getDraws() + 1);
        second.getResults().add(0, result.reversed());
    }
}
