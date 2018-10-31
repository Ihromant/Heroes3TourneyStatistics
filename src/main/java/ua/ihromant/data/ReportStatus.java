package ua.ihromant.data;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ReportStatus {
    CONFIRMED("подтвержденный"), UNCONFIRMED("неподтвержденный"), CANCELLED("аннулирован");

    private final String russian;

    private static Map<String, ReportStatus> TRANSLATION_TO_STATUS = Stream.of(ReportStatus.values()).collect(Collectors.toMap(ReportStatus::getRussian, Function.identity()));

    ReportStatus(String russian) {
        this.russian = russian;
    }

    public String getRussian() {
        return russian;
    }

    public static ReportStatus parse(String from) {
        return TRANSLATION_TO_STATUS.get(from);
    }
}
