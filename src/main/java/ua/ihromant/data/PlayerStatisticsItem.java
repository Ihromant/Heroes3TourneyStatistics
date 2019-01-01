package ua.ihromant.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class PlayerStatisticsItem extends StatisticsItem implements Cloneable {
    private String name;
    private int rating;
    private int rank;
    @JsonIgnore
    private List<GameResult> results = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public List<GameResult> getResults() {
        return results;
    }

    public void setResults(List<GameResult> results) {
        this.results = results;
    }

    public PlayerStatisticsItem cloned() {
        try {
            PlayerStatisticsItem result = (PlayerStatisticsItem) this.clone();
            result.results = new ArrayList<>(this.results);
            return result;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e); // never happens
        }
    }
}
