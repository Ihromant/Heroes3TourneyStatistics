package ua.ihromant.data;

import java.util.List;
import java.util.Map;

public class Unconfirmed {
    private List<GameResult> games;
    private Map<String, Long> lazybones;

    public List<GameResult> getGames() {
        return games;
    }

    public void setGames(List<GameResult> games) {
        this.games = games;
    }

    public Map<String, Long> getLazybones() {
        return lazybones;
    }

    public void setLazybones(Map<String, Long> lazybones) {
        this.lazybones = lazybones;
    }
}
