package ua.ihromant.data;

import java.time.LocalDate;
import java.util.Map;

public class Ladder {
    private String name;
    private Map<String, StatisticsItem> items;
    private Map<String, Integer> timingMap;
    private int totalGames;
    private Map<LocalDate, Integer> activities;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, StatisticsItem> getItems() {
        return items;
    }

    public void setItems(Map<String, StatisticsItem> items) {
        this.items = items;
    }

    public Map<String, Integer> getTimingMap() {
        return timingMap;
    }

    public void setTimingMap(Map<String, Integer> timingMap) {
        this.timingMap = timingMap;
    }

    public int getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(int totalGames) {
        this.totalGames = totalGames;
    }

    public Map<LocalDate, Integer> getActivities() {
        return activities;
    }

    public void setActivities(Map<LocalDate, Integer> activities) {
        this.activities = activities;
    }
}
