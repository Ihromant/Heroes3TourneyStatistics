package ua.ihromant.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import ua.ihromant.data.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticsCollector {
    private static ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private static final String NEW_TOURNAMENTS = "http://forum.heroesworld.ru/forumdisplay.php?f=62";
    private static final String OLD_TOURNAMENTS = "http://forum.heroesworld.ru/forumdisplay.php?f=143";
    private final ThemeParserNew themeParser = new ThemeParserNew();

    public List<GameResult> collect() throws IOException {
        Set<String> alreadyVisited = new HashSet<>();
        return parsePages(alreadyVisited, NEW_TOURNAMENTS, OLD_TOURNAMENTS);
    }

    public List<GameResult> parsePages(Set<String> alreadyVisited, String... links) throws IOException {
        List<GameResult> result = new ArrayList<>();

        for (String link : links) {
            Document oldTourneys = Jsoup.parse(new URL(link), 10000);
            String pagesCaption = oldTourneys.select("td.vbmenu_control").stream()
                    .map(Element::text)
                    .filter(s -> s.startsWith("Страница"))
                    .findFirst().orElse(null);
            int pages = 1;
            if (pagesCaption != null) {
                String[] caption = pagesCaption.split(" ");
                pages = Integer.parseInt(caption[caption.length - 1]);
            }

            for (int i = 0; i < pages; i++) {
                result.addAll(parseThemes(alreadyVisited, link + String.format("&page=%s&order=desc", i + 1)));
            }
        }

        return result;
    }

    public List<GameResult> parseThemes(Set<String> alreadyVisited, String link) throws IOException {
        List<GameResult> result = new ArrayList<>();
        Document page = Jsoup.parse(new URL(link), 10000);
        List<String> themes = page.select("td.alt1").stream()
                .filter(el -> el.id() != null && el.id().startsWith("td_title"))
                .map(el -> el.child(0).child(1).attr("href")).collect(Collectors.toList());

        for (String theme : themes) {
            if (!alreadyVisited.contains(theme)) {
                result.addAll(themeParser.parseResults("http://forum.heroesworld.ru/" + theme));
                alreadyVisited.add(theme);
            }
        }

        return result;
    }

    public static void main(String[] args) throws IOException {
        long time = System.currentTimeMillis();
        mapper.writeValue(new FileOutputStream("/home/ihromant/results.json"), new StatisticsCollector().collect());
        System.out.println("Time spent " + (System.currentTimeMillis() - time) / 1000.0);
    }
}