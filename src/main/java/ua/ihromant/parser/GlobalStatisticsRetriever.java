package ua.ihromant.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.ihromant.analisys.Calculator;
import ua.ihromant.analisys.RatingCalculator;
import ua.ihromant.analisys.UnconfirmedCollector;
import ua.ihromant.config.Config;
import ua.ihromant.data.GameResult;
import ua.ihromant.data.GlobalStatistics;
import ua.ihromant.data.Template;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

public class GlobalStatisticsRetriever {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalStatisticsRetriever.class);
    private static UnconfirmedCollector uncCollector = new UnconfirmedCollector();

    private static Stream<Calculator> calculators() {
        ZonedDateTime now = ZonedDateTime.now();
        Stream.Builder<Calculator> builder = Stream.builder();
        builder.add(new Calculator<>(GlobalStatistics::setOverall, RatingCalculator.overall()));
        builder.add(new Calculator<>(GlobalStatistics::setCurrentSeason, RatingCalculator.inSeason(now.toLocalDate())));
        builder.add(new Calculator<>(GlobalStatistics::setPreviousSeason, RatingCalculator.inSeason(now.toLocalDate().minusMonths(6))));
        builder.add(new Calculator<>(GlobalStatistics::setUnconfirmed, uncCollector));
        builder.add(new Calculator<>(GlobalStatistics::setLastUpdate, res -> now));
        builder.add(new Calculator<>(GlobalStatistics::setTourney, RatingCalculator.tourneys()));
        builder.add(new Calculator<>(GlobalStatistics::setCurrentTourney, RatingCalculator.inTourneySeason()));
        builder.add(new Calculator<>(GlobalStatistics::setTwoYears, RatingCalculator.last2Years()));
        Stream.of(Template.values())
                .filter(t -> t.getParent() == null && t != Template.RANDOM)
                .forEach(temp ->
                        builder.add(
                                new Calculator<>(
                                        (stat, ladder) -> stat.getTemplates().put(temp, ladder),
                                        RatingCalculator.templateCalculator(temp))));
        return builder.build();
    }

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

    private static GlobalStatistics calculate(List<GameResult> results) {
        GlobalStatistics stat = new GlobalStatistics();
        calculators().parallel().forEach(calc -> calc.accept(stat, results));
        return stat;
    }
}
