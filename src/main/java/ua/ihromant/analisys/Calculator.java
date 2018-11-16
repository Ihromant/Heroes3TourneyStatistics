package ua.ihromant.analisys;

import ua.ihromant.data.GameResult;
import ua.ihromant.data.GlobalStatistics;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Calculator<T> implements BiConsumer<GlobalStatistics, List<GameResult>> {
    private final BiConsumer<GlobalStatistics, T> setter;
    private final Function<List<GameResult>, T> calculation;

    public Calculator(BiConsumer<GlobalStatistics, T> setter, Function<List<GameResult>, T> calculation) {
        this.setter = setter;
        this.calculation = calculation;
    }

    @Override
    public void accept(GlobalStatistics globalStatistics, List<GameResult> gameResults) {
        setter.accept(globalStatistics, calculation.apply(gameResults));
    }
}
