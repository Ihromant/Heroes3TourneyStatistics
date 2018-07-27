package ua.ihromant;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Castle {
    CASTLE("Замок"), RAMPART("Оплот"), TOWER("Башня"),
    CITADEL("Цитадель"), FORTRESS("Крепость"), CONFLUX("Сопряжение"),
    DUNGEON("Подземелье"), INFERNO("Инферно"), NECROPOLIS("Некрополь");

    private final String russian;

    private static Map<String, Castle> TRANSLATION_TO_COLOR = Stream.of(Castle.values()).collect(Collectors.toMap(Castle::getRussian, Function.identity()));

    Castle(String russian) {
        this.russian = russian;
    }

    public String getRussian() {
        return russian;
    }

    public static Castle parse(String from) {
        return TRANSLATION_TO_COLOR.get(from);
    }
}
