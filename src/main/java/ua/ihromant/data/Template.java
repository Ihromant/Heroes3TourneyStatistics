package ua.ihromant.data;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Template {
    _2SM4D_3("2sm4d(3)"),
    JEBUS_CROSS("Jebus Cross"),
    _8MM6A("8mm6a"),
    _8MM6("8mm6", _8MM6A),
    _6LM10A("6lm10a"),
    _6LM10("6lm10", _6LM10A),
    _8XM12A("8xm12a"),
    _8XM12("8xm12", _8XM12A),
    _2SM4D_2("2sm4d(2)", _2SM4D_3),
    _2SM4D("2sm4d", _2SM4D_3),
    NOSTALGIA("Nostalgia"),
    SPIDER("Spider"),
    MINI_NOSTALGIA("Mini-Nostalgia"),
    ANARCHY("Anarchy"),
    BLOCKBUSTER("Blockbuster"),
    H3DM1("h3dm1"),
    RING("Ring"),
    HYPERCUBE("Hypercube"),
    CHRISTMAS_TREE("Ёлка"),
    DIAMOND("Diamond"),
    READY_OR_NOT("Ready or Not"),
    SPEED_1("Speed-1"),
    SPEED_2("Speed-2"),
    KITE("Kite"),
    _8XM8A("8xm8a"),
    _8XM8("8xm8", _8XM8A),
    FIXED_MAP("Fixed Map"),
    RANDOM("Random"),
    _2SM2C("2sm2c"),
    SKIRMISH("Skirmish"),
    TERRA_DEI_DEMONI("Terra dei Demoni"),
    MIRROR_DIAMOND("Mirror Diamond"),
    MIRROR_SKIRMISH("Mirror Skirmish"),
    MIRROR_JEBUS_CROSS("Mirror Jebus Cross"),
    BALANCE("Balance"),
    PANIC("Panic"),
    BALANCE_M("Balance M", BALANCE),
    WHEEL("Wheel"),
    BOOMERANG("Boomerang"),
    APOCALYPSE("Apocalypse"),
    OTHER_TEMPLATE("Другой шаблон");
    private final String templateName;
    private final Template parent;

    private static Map<String, Template> NAME_TO_TEMPLATE = Stream.of(Template.values()).collect(Collectors.toMap(Template::getTemplateName, Function.identity()));

    Template(String templateName) {
        this(templateName, null);
    }

    Template(String templateName, Template parent) {
        this.templateName = templateName;
        this.parent = parent;
    }

    public String getTemplateName() {
        return templateName;
    }

    public Template getParent() {
        return parent;
    }

    public static Template parse(String from) {
        return NAME_TO_TEMPLATE.get(from);
    }
}
