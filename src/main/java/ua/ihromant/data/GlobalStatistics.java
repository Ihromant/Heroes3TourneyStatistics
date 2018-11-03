package ua.ihromant.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ua.ihromant.analisys.RatingCalculator;

import java.io.IOException;
import java.util.List;

public class GlobalStatistics {
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    private static GlobalStatistics statistics;

    private Ladder overall;
    private Ladder currentSeason;
    private Ladder previousSeason;

    private List<StatisticsItem> unconfirmed;

    public static GlobalStatistics instance() {
        if (statistics == null) {
            statistics = new GlobalStatistics();
            Ladder overall = new Ladder();
            overall.setName("Overall rating");
            try {
                overall.setItems(new RatingCalculator().calculate(mapper.readValue(RatingCalculator.class.getResourceAsStream("/source/results1.json"),
                        new TypeReference<List<GameResult>>() {
                        })));
            } catch (IOException e) {
                e.printStackTrace();
            }
            statistics.setOverall(overall);
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

    public List<StatisticsItem> getUnconfirmed() {
        return unconfirmed;
    }

    public void setUnconfirmed(List<StatisticsItem> unconfirmed) {
        this.unconfirmed = unconfirmed;
    }
}
