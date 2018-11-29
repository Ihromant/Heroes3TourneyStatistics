package ua.ihromant.analisys.builder;

import ua.ihromant.data.GameResult;
import ua.ihromant.data.Ladder;

import java.util.List;
import java.util.function.BiConsumer;

public abstract class StatisticsBuilder<A, R> {
    protected final A initial;
    private final BiConsumer<Ladder, R> setter;
    protected A value;

    protected StatisticsBuilder(A initial, BiConsumer<Ladder, R> setter) {
        this.initial = initial;
        this.setter = setter;
    }

    public void init(List<GameResult> results) {
        this.value = initial;
    }

    public abstract void append(GameResult res);

    protected abstract R build();

    public void set(Ladder ladder) {
        setter.accept(ladder, build());
    }
}
