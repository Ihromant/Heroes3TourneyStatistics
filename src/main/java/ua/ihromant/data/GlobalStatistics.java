package ua.ihromant.data;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Map;

public class GlobalStatistics {
    private static GlobalStatistics instance;

    private Ladder overall;
    private Ladder currentSeason;
    private Ladder previousSeason;

    private Map<LocalDate, Long> frequencies;

    private Unconfirmed unconfirmed;

    private ZonedDateTime lastUpdate;

    public static GlobalStatistics getInstance() {
        return instance;
    }

    public static void setInstance(GlobalStatistics newStat) {
        instance = newStat;
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

    public Map<LocalDate, Long> getFrequencies() {
        return frequencies;
    }

    public void setFrequencies(Map<LocalDate, Long> frequencies) {
        this.frequencies = frequencies;
    }

    public Unconfirmed getUnconfirmed() {
        return unconfirmed;
    }

    public void setUnconfirmed(Unconfirmed unconfirmed) {
        this.unconfirmed = unconfirmed;
    }

    public ZonedDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(ZonedDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
