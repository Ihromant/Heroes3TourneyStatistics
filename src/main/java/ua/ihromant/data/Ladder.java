package ua.ihromant.data;

import java.util.Map;

public class Ladder {
    private String name;
    private Map<String, StatisticsItem> items;

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
}
