package ua.ihromant.data;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Template {
    JEBUS_CROSS("Jebus Cross"); // TODO add others
    private String name;

    private static Map<String, Template> NAME_TO_TEMPLATE = Stream.of(Template.values()).collect(Collectors.toMap(Template::getName, Function.identity()));

    Template(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Template parse(String from) {
        return NAME_TO_TEMPLATE.get(from);
    }
}
