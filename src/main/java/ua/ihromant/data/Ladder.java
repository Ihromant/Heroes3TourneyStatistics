package ua.ihromant.data;

import java.time.LocalDate;
import java.util.Map;

public class Ladder {
    private String name;
    private Map<String, PlayerStatisticsItem> items;
    private Map<String, Integer> timingMap;
    private int totalGames;
    private Map<LocalDate, Integer> activities;
    private Map<LocalDate, Integer> playerActivities;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, PlayerStatisticsItem> getItems() {
        return items;
    }

    public void setItems(Map<String, PlayerStatisticsItem> items) {
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

    public Map<LocalDate, Integer> getPlayerActivities() {
        return playerActivities;
    }

    public void setPlayerActivities(Map<LocalDate, Integer> playerActivities) {
        this.playerActivities = playerActivities;
    }
}
