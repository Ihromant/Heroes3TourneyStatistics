package ua.ihromant.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.ihromant.analisys.RatingCalculator;
import ua.ihromant.analisys.UnconfirmedCollector;
import ua.ihromant.config.Config;
import ua.ihromant.data.GameResult;
import ua.ihromant.data.GlobalStatistics;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

public class GlobalStatisticsRetriever {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalStatisticsRetriever.class);
    private static UnconfirmedCollector uncCollector = new UnconfirmedCollector();

    public static GlobalStatistics retrieve() {
        long time = System.currentTimeMillis();
        LOGGER.info("Collecting statistics...");
        List<GameResult> results;
        try {
            results = Config.COLLECTOR.collect();
        } catch (IOException e) {
            LOGGER.error("Was unable to collect statistics. See reason below: ", e);
            return null;
        }
        LOGGER.info("Statistics collected. " + results.size()
                + " game records parsed. Time spent " + (System.currentTimeMillis() - time) + " ms.");
        return calculate(results);
    }

    public static GlobalStatistics calculate(List<GameResult> results) {
        GlobalStatistics statistics = new GlobalStatistics();
        statistics.setOverall(RatingCalculator.overall().calculate(results));
        ZonedDateTime now = ZonedDateTime.now();
        statistics.setCurrentSeason(RatingCalculator.inSeason(now.toLocalDate()).calculate(results));
        statistics.setPreviousSeason(RatingCalculator.inSeason(now.toLocalDate().minusMonths(6)).calculate(results));
        statistics.setUnconfirmed(uncCollector.unconfirmed(results));
        statistics.setLastUpdate(now);
        return statistics;
    }
}
