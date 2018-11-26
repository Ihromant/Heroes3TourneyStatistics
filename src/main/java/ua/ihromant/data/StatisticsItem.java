package ua.ihromant.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class StatisticsItem implements Cloneable {
    private String name;
    private int rating;
    private int rank;
    private int wins;
    private int loses;
    private int draws;
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

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLoses() {
        return loses;
    }

    public void setLoses(int loses) {
        this.loses = loses;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public List<GameResult> getResults() {
        return results;
    }

    public void setResults(List<GameResult> results) {
        this.results = results;
    }

    @JsonIgnore
    public int getTotalGames() {
        return wins + loses + draws;
    }

    public StatisticsItem cloned() {
        try {
            return (StatisticsItem) this.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e); // never happens
        }
    }
}
