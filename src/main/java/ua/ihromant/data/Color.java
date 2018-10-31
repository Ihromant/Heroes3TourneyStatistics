package ua.ihromant.data;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Color {
    RED("Красный"), BLUE("Синий"); // TODO add all colors

    private final String russian;

    private static Map<String, Color> TRANSLATION_TO_COLOR = Stream.of(Color.values()).collect(Collectors.toMap(Color::getRussian, Function.identity()));

    Color(String russian) {
        this.russian = russian;
    }

    public String getRussian() {
        return russian;
    }

    public static Color parse(String from) {
        return TRANSLATION_TO_COLOR.get(from);
    }
}
