package ua.ihromant.analisys.builder;

import ua.ihromant.analisys.builder.StatisticsBuilder;
import ua.ihromant.data.GameResult;
import ua.ihromant.data.Ladder;

import java.util.Map;
import java.util.TreeMap;

public class TimingBuilder extends StatisticsBuilder<Map<String, Integer>, Map<String, Integer>> {
    public TimingBuilder() {
        super(new TreeMap<>(), Ladder::setTimingMap);
    }

    @Override
    public void append(GameResult res) {
        value.compute(timingKey(res.getTiming()), (key, old) -> old != null ? old + 1 : 1);
    }

    @Override
    public Map<String, Integer> build() {
        return value;
    }

    private static String timingKey(String timing) {
        if ("114".compareTo(timing) >= 0) {
            return "114-";
        }
        if ("221".compareTo(timing) <= 0 && "227".compareTo(timing) >= 0) {
            return "221-227";
        }
        if ("231".compareTo(timing) <= 0) {
            return "231+";
        }
        return timing;
    }
}
