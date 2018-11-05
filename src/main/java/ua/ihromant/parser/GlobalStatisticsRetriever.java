package ua.ihromant.parser;

import ua.ihromant.analisys.RatingCalculator;
import ua.ihromant.analisys.UnconfirmedCollector;
import ua.ihromant.config.Config;
import ua.ihromant.data.GameResult;
import ua.ihromant.data.GlobalStatistics;
import ua.ihromant.data.Ladder;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

public class GlobalStatisticsRetriever {
    private static RatingCalculator calculator = new RatingCalculator();
    private static UnconfirmedCollector uncCollector = new UnconfirmedCollector();

    public static GlobalStatistics retrieve() {
        long time = System.currentTimeMillis();
        System.out.println("Collecting statistics...");
        List<GameResult> results;
        try {
            results = Config.COLLECTOR.collect();
        } catch (IOException e) {
            System.out.println("Was unable to collect statistics. See reason below: ");
            e.printStackTrace();
            return null;
        }
        System.out.println("Statistics collected. " + results.size()
                + " game records parsed. Time spent " + (System.currentTimeMillis() - time) + " ms.");
        return calculate(results);
    }

    public static GlobalStatistics calculate(List<GameResult> results) {
        GlobalStatistics statistics = new GlobalStatistics();
        Ladder overall = new Ladder();
        overall.setName("Overall rating");
        overall.setItems(calculator.calculateOverall(results));
        statistics.setOverall(overall);
        statistics.setUnconfirmed(uncCollector.unconfirmed(results));
        statistics.setLastUpdate(ZonedDateTime.now());
        return statistics;
    }
}
