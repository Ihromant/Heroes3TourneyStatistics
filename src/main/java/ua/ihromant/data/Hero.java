package ua.ihromant.data;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Hero {
    VALESKA("Валеска", Castle.CASTLE); // TODO add others 143 heroes :D

    private final String russian;
    private final Castle castle;

    private static Map<String, Hero> TRANSLATION_TO_COLOR = Stream.of(Hero.values()).collect(Collectors.toMap(Hero::getRussian, Function.identity()));

    Hero(String russian, Castle castle) {
        this.russian = russian;
        this.castle = castle;
    }

    public String getRussian() {
        return russian;
    }

    public Castle getCastle() {
        return castle;
    }

    public static Hero parse(String from) {
        return TRANSLATION_TO_COLOR.get(from);
    }
}
