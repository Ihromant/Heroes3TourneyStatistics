package ua.ihromant.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class StatisticsItem {
    private String name;
    private int rating;
    private int wins;
    private int loses;
    private int draws;
    @JsonIgnore
    private List<GameResult> playersResults = new ArrayList<>();

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

    public List<GameResult> getPlayersResults() {
        return playersResults;
    }

    public void setPlayersResults(List<GameResult> playersResults) {
        this.playersResults = playersResults;
    }

    @JsonIgnore
    public int getTotalGames() {
        return wins + loses + draws;
    }
}
