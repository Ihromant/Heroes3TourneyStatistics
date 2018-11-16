package ua.ihromant.data;

import java.util.Map;
import java.util.TreeMap;

public class Ladder {
    private String name;
    private Map<String, StatisticsItem> items;
    private final Map<String, Integer> timingMap = new TreeMap<>();
    private int totalGames;

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

    public int getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(int totalGames) {
        this.totalGames = totalGames;
    }
}
