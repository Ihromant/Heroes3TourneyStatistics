package ua.ihromant.analisys;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ua.ihromant.data.GameResult;
import ua.ihromant.data.StatisticsItem;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RatingCalculator {
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static void main(String[] args) throws IOException {
        mapper.writeValue(new FileOutputStream("/home/ihromant/statistics.json"),
                new RatingCalculator().calculate(mapper.readValue(RatingCalculator.class.getResourceAsStream("/results.json"),
                        new TypeReference<List<GameResult>>() {
                        })));
    }

    public List<StatisticsItem> calculate(List<GameResult> results) {
        Map<String, StatisticsItem> statistics = results.stream()
                .flatMap(res -> Stream.of(res.getConfirmer().getName(), res.getReporter().getName()))
                .distinct().collect(Collectors.toMap(Function.identity(), name -> {
                    StatisticsItem it = new StatisticsItem();
                    it.setName(name);
                    it.setRating(1500);
                    return it;
                }));
        results.stream()
                .filter(res -> res.getConfirmer().getReportLink() != null)
                .sorted(Comparator.comparing(GameResult::getDate))
                .forEach(res -> {
                    StatisticsItem reporterStats = statistics.get(res.getReporter().getName());
                    StatisticsItem confirmerStats = statistics.get(res.getConfirmer().getName());
                    switch (res.getResult()) {
                        case DEF:
                            recalculateWin(reporterStats, confirmerStats);
                            break;
                        case LOSE:
                            recalculateWin(confirmerStats, reporterStats);
                            break;
                        default:
                            recalculateDraw(reporterStats, confirmerStats);
                            break;
                    }
                });
        return statistics.values().stream().sorted(Comparator.comparing(StatisticsItem::getRating).reversed()).collect(Collectors.toList());
    }

    private void recalculateWin(StatisticsItem winner, StatisticsItem loser) {
        double f1 = Math.max(-1, Math.min(1.0 * (winner.getRating() - loser.getRating()) / 400, 1));
        double f2 = Math.max(-1, Math.min(1.0 * (loser.getRating() - winner.getRating()) / 400, 1));

        int wp2 = Math.max(1, (int) Math.round(16 * (1 + f2)));
        int lp1 = Math.min(-1, (int) Math.round(16 * (-1 + f1)));

        winner.setRating(winner.getRating() + wp2);
        loser.setRating(loser.getRating() + lp1);

        winner.setWins(winner.getWins() + 1);
        loser.setLoses(loser.getLoses() + 1);
    }

    private void recalculateDraw(StatisticsItem first, StatisticsItem second) {
        double f1 = Math.max(-1, Math.min(1.0 * (first.getRating() - second.getRating()) / 400, 1));
        double f2 = Math.max(-1, Math.min(1.0 * (second.getRating() - first.getRating()) / 400, 1));

        double wp1 = Math.max(1, 16 * (1 + f1));
        double wp2 = Math.max(1, 16 * (1 + f2));

        first.setRating(first.getRating() + (int) ((wp2 - wp1) / 2 + 0.5));
        second.setRating(second.getRating() + (int) ((wp1 - wp2) / 2 + 0.5));
        first.setDraws(first.getDraws() + 1);
        second.setDraws(second.getDraws() + 1);
    }
}
