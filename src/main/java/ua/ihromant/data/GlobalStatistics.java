package ua.ihromant.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ua.ihromant.analisys.RatingCalculator;
import ua.ihromant.analisys.UnconfirmedCollector;

import java.io.IOException;
import java.util.List;

public class GlobalStatistics {
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    private static GlobalStatistics statistics;

    private static RatingCalculator calculator = new RatingCalculator();
    private static UnconfirmedCollector uncCollector = new UnconfirmedCollector();

    private Ladder overall;
    private Ladder currentSeason;
    private Ladder previousSeason;

    private Unconfirmed unconfirmed;

    public static GlobalStatistics instance() {
        if (statistics == null) {
            statistics = new GlobalStatistics();
            Ladder overall = new Ladder();
            overall.setName("Overall rating");
            List<GameResult> items = null;
            try {
                items = mapper.readValue(GlobalStatistics.class.getResourceAsStream("/source/results1.json"),
                        new TypeReference<List<GameResult>>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
            overall.setItems(calculator.calculateOverall(items));
            statistics.setOverall(overall);
            statistics.setUnconfirmed(uncCollector.unconfirmed(items));
        }
        return statistics;
    }

    public Ladder getOverall() {
        return overall;
    }

    public void setOverall(Ladder overall) {
        this.overall = overall;
    }

    public Ladder getCurrentSeason() {
        return currentSeason;
    }

    public void setCurrentSeason(Ladder currentSeason) {
        this.currentSeason = currentSeason;
    }

    public Ladder getPreviousSeason() {
        return previousSeason;
    }

    public void setPreviousSeason(Ladder previousSeason) {
        this.previousSeason = previousSeason;
    }

    public Unconfirmed getUnconfirmed() {
        return unconfirmed;
    }

    public void setUnconfirmed(Unconfirmed unconfirmed) {
        this.unconfirmed = unconfirmed;
    }
}
