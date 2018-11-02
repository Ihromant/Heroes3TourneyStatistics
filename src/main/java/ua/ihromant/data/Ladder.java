package ua.ihromant.data;

import java.util.List;

public class Ladder {
    private String name;
    private List<StatisticsItem> items;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StatisticsItem> getItems() {
        return items;
    }

    public void setItems(List<StatisticsItem> items) {
        this.items = items;
    }
}
