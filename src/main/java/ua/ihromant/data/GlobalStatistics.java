package ua.ihromant.data;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.EnumMap;
import java.util.Map;

public class GlobalStatistics {
    private static GlobalStatistics instance;

    private Ladder overall;
    private Ladder currentSeason;
    private Ladder previousSeason;
    private Ladder tourney;
    private Ladder currentTourney;
    private Ladder twoYears;

    private Unconfirmed unconfirmed;

    private ZonedDateTime lastUpdate;

    private final Map<Template, Ladder> templates = new EnumMap<>(Template.class);

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

    public Ladder getTourney() {
        return tourney;
    }

    public void setTourney(Ladder tourney) {
        this.tourney = tourney;
    }

    public Ladder getCurrentTourney() {
        return currentTourney;
    }

    public void setCurrentTourney(Ladder currentTourney) {
        this.currentTourney = currentTourney;
    }

    public Ladder getTwoYears() {
        return twoYears;
    }

    public void setTwoYears(Ladder twoYears) {
        this.twoYears = twoYears;
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

    public Map<Template, Ladder> getTemplates() {
        return templates;
    }
}
