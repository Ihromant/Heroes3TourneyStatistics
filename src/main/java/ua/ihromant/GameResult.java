package ua.ihromant;

import java.util.Objects;

public class GameResult {
    private final PlayerInfo winner;
    private final PlayerInfo loser;

    public GameResult(PlayerInfo winner, PlayerInfo loser) {
        this.winner = winner;
        this.loser = loser;
    }

    public PlayerInfo getWinner() {
        return winner;
    }

    public PlayerInfo getLoser() {
        return loser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameResult that = (GameResult) o;
        return Objects.equals(winner, that.winner) &&
                Objects.equals(loser, that.loser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(winner, loser);
    }

    @Override
    public String toString() {
        return "GameResult{" +
                "winner=" + winner +
                ", loser=" + loser +
                '}';
    }
}
