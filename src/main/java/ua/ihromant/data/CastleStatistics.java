package ua.ihromant.data;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CastleStatistics {
    private final CastleStatisticsItem[][] castlesMatrix = new CastleStatisticsItem[Castle.values().length][Castle.values().length];

    public CastleStatistics() {
        for (int i = 0; i < Castle.values().length; i++) {
            for (int j = 0; j < Castle.values().length; j++) {
                castlesMatrix[i][j] = new CastleStatisticsItem();
            }
        }
    }

    public StatisticsItem[][] getCastlesMatrix() {
        return castlesMatrix;
    }

    public StatisticsItem getPair(Castle first, Castle second) {
        return castlesMatrix[first.ordinal()][second.ordinal()];
    }

    public Map<Castle, StatisticsItem> castleMap() {
        return Arrays.stream(Castle.values())
                .map(castle -> Arrays.stream(Castle.values()).filter(opp -> opp != castle)
                        .reduce(new CastleStatisticsItem(),
                                (item, opp) -> {
                                    CastleStatisticsItem stat = castlesMatrix[castle.ordinal()][opp.ordinal()];
                                    item.setCastle(castle);
                                    item.setWins(item.getWins() + stat.getWins());
                                    item.setLoses(item.getLoses() + stat.getLoses());
                                    item.setDraws(item.getDraws() + stat.getDraws());
                                    return item;
                                },
                                (item1, item2) -> {
                                    item1.setWins(item1.getWins() + item2.getWins());
                                    item1.setLoses(item1.getLoses() + item2.getLoses());
                                    item1.setDraws(item1.getDraws() + item2.getDraws());
                                    return item1;
                                })).collect(Collectors.toMap(CastleStatisticsItem::getCastle, Function.identity()));
    }
}