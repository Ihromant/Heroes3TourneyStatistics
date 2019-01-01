package ua.ihromant.analisys.builder;

import ua.ihromant.data.GameResult;
import ua.ihromant.data.Ladder;
import ua.ihromant.data.PlayerStatisticsItem;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StatisticsItemsBuilder extends StatisticsBuilder<Map<String, PlayerStatisticsItem>, Map<String, PlayerStatisticsItem>> {
    public StatisticsItemsBuilder(Map<String, PlayerStatisticsItem> initial) {
        super(initial, Ladder::setItems);
    }

    @Override
    public void init(List<GameResult> results) {
        this.value = results.stream()
                .flatMap(res -> Stream.of(res.getConfirmer().getName(), res.getReporter().getName()))
                .distinct().collect(Collectors.toMap(Function.identity(), name -> {
            if (initial.containsKey(name)) {
                return initial.get(name).cloned();
            }
            PlayerStatisticsItem it = new PlayerStatisticsItem();
            it.setName(name);
            it.setRating(1500);
            return it;
        }));
    }

    @Override
    public void append(GameResult res) {
        PlayerStatisticsItem reporterStats = value.get(res.getReporter().getName());
        PlayerStatisticsItem confirmerStats = value.get(res.getConfirmer().getName());
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
    }

    @Override
    public Map<String, PlayerStatisticsItem> build() {
        List<PlayerStatisticsItem> ordered = value.entrySet().stream()
                .map(Map.Entry::getValue)
                .sorted(Comparator.comparing(PlayerStatisticsItem::getRating).reversed())
                .collect(Collectors.toList());
        return IntStream.range(0, ordered.size())
                .mapToObj(i -> {
                    PlayerStatisticsItem item = ordered.get(i);
                    item.setRank(i + 1);
                    return item;
                })
                .collect(Collectors.toMap(it -> it.getName().toLowerCase(),
                        Function.identity(),
                        (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); },
                        LinkedHashMap::new));
    }

    private void recalculateWin(PlayerStatisticsItem winner, PlayerStatisticsItem loser, GameResult result) {
        float f1 = Math.max(-1, Math.min(1.0f * (winner.getRating() - loser.getRating()) / 400, 1));
        float f2 = Math.max(-1, Math.min(1.0f * (loser.getRating() - winner.getRating()) / 400, 1));

        int wp2 = Math.max(1, Math.round(16 * (1 + f2)));
        int lp1 = Math.min(-1, Math.round(16 * (-1 + f1)));

        result.setPreviousReporter(winner.getRating());
        result.setPreviousConfirmer(loser.getRating());
        result.setReporterChange(wp2);
        result.setConfirmerChange(lp1);

        winner.setRating(winner.getRating() + wp2);
        winner.setWins(winner.getWins() + 1);
        winner.getResults().add(0, result);

        loser.setRating(loser.getRating() + lp1);
        loser.setLoses(loser.getLoses() + 1);
        loser.getResults().add(0, result.reversed());
    }

    private void recalculateDraw(PlayerStatisticsItem first, PlayerStatisticsItem second, GameResult result) {
        float f1 = Math.max(-1, Math.min(1.0f * (first.getRating() - second.getRating()) / 400, 1));
        float f2 = Math.max(-1, Math.min(1.0f * (second.getRating() - first.getRating()) / 400, 1));

        float wp1 = Math.max(1, 16 * (1 + f1));
        float wp2 = Math.max(1, 16 * (1 + f2));

        result.setReporterChange(Math.round(((wp2 - wp1) / 2 + 0.5f)));
        result.setConfirmerChange(Math.round(((wp1 - wp2) / 2 + 0.5f)));
        result.setPreviousReporter(first.getRating());
        result.setPreviousConfirmer(second.getRating());

        first.setRating(first.getRating() + result.getReporterChange());
        first.setDraws(first.getDraws() + 1);
        first.getResults().add(0, result);

        second.setRating(second.getRating() + result.getConfirmerChange());
        second.setDraws(second.getDraws() + 1);
        second.getResults().add(0, result.reversed());
    }
}
