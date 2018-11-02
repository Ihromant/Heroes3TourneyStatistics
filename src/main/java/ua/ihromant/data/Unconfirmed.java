package ua.ihromant.data;

import java.util.List;
import java.util.Map;

public class Unconfirmed {
    private List<StatisticsItem> unconfirmed;
    private Map<String, Integer> lazybones;

    public List<StatisticsItem> getUnconfirmed() {
        return unconfirmed;
    }

    public void setUnconfirmed(List<StatisticsItem> unconfirmed) {
        this.unconfirmed = unconfirmed;
    }

    public Map<String, Integer> getLazybones() {
        return lazybones;
    }

    public void setLazybones(Map<String, Integer> lazybones) {
        this.lazybones = lazybones;
    }
}
