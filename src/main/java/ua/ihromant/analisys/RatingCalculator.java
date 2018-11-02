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
        List<StatisticsItem> items = new RatingCalculator().calculate(
                mapper.readValue(RatingCalculator.class.getResourceAsStream("/results1.json"),
                new TypeReference<List<GameResult>>() {
                }));
        mapper.writeValue(new FileOutputStream("/home/ihromant/statistics.json"), items);
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
        return statistics.values().stream().sorted(Comparator.comparing(StatisticsItem::getRating).reversed()).collect(Collectors.toList());
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
        winner.getPlayersResults().add(result);

        loser.setRating(loser.getRating() + result.getConfirmerChange());
        loser.setLoses(loser.getLoses() + 1);
        loser.getPlayersResults().add(result.reversed());
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
        first.getPlayersResults().add(result);

        second.setRating(second.getRating() + result.getConfirmerChange());
        second.setDraws(second.getDraws() + 1);
        second.getPlayersResults().add(result.reversed());
    }
}
