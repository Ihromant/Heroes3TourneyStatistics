package ua.ihromant.analisys;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ua.ihromant.data.GameResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class RatingCalculator {
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    public static void main(String[] args) throws IOException {
        new RatingCalculator().calculate(mapper.readValue(RatingCalculator.class.getResourceAsStream("/results.json"),
                new TypeReference<List<GameResult>>() {}));
    }

    public Map<String, Double> calculate(List<GameResult> results) {
        return null;
    }
}
