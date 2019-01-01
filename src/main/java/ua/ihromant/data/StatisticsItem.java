package ua.ihromant.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class StatisticsItem {
    private int wins;
    private int loses;
    private int draws;

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

    @JsonIgnore
    public int getTotalGames() {
        return wins + loses + draws;
    }
}
