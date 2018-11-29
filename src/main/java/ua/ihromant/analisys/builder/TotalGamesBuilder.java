package ua.ihromant.analisys.builder;

import ua.ihromant.data.GameResult;
import ua.ihromant.data.Ladder;

public class TotalGamesBuilder extends StatisticsBuilder<Integer, Integer> {
    public TotalGamesBuilder() {
        super(0, Ladder::setTotalGames);
    }

    @Override
    public void append(GameResult res) {
        this.value = value + 1;
    }

    @Override
    public Integer build() {
        return value;
    }
}
