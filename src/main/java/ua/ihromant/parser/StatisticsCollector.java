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

    private static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final String NEW_TOURNAMENTS = "http://forum.heroesworld.ru/forumdisplay.php?f=62";
    private static final String OLD_TOURNAMENTS = "http://forum.heroesworld.ru/forumdisplay.php?f=143";

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
                result.addAll(parseResults("http://forum.heroesworld.ru/" + theme));
                alreadyVisited.add(theme);
            }
        }

        return result;
    }

    private List<GameResult> parseResults(String url) throws IOException {
        Document doc = Jsoup.parse(new URL(url), 10000);
        Element statsTable = doc.body().getElementById("collapseobj_tournament_reports");
        if (statsTable == null) {
            return Collections.emptyList();
        }

        Elements records = statsTable.child(1).child(0).child(0).child(0).child(1).child(0).children();
        List<GameResult> results = new ArrayList<>();
        for (Element el : records) {
            if (el.children().size() < 2) {
                System.out.println("WTF???"); // TODO
                continue;
            }

            ReportStatus status = ReportStatus.parse(el.child(1).text());

            if (status == null) {
                System.out.println("Second WTF???"); // TODO
            }

            if (status == ReportStatus.CONFIRMED) {
                results.add(parseConfirmed(el.child(0).childNodes()));
            }

            if (status == ReportStatus.UNCONFIRMED) {
                results.add(parseUnconfirmed(el.child(0).childNodes()));
            }
        }
        return results;
    }

    private static GameResult parseConfirmed(List<Node> nodes) {
        GameResult result = new GameResult();
        result.setDate(LocalDate.parse(((TextNode) nodes.get(0)).text().trim(), DATE_FORMATTER));
        PlayerInfo firstInfo = new PlayerInfo();
        Element fstPl = (Element) nodes.get(1);
        firstInfo.setReportLink(fstPl.attr("href"));
        firstInfo.setName(fstPl.text());
        firstInfo.setColor(Color.parse(((Element) nodes.get(3)).text()));
        String firstPlayerPart = ((TextNode) nodes.get(4)).text();
        int separator = firstPlayerPart.indexOf(')');
        enchancePlayerResult(firstInfo, firstPlayerPart.substring(0, separator));
        result.setResult(Result.valueOf(firstPlayerPart.substring(separator + 1).toUpperCase().trim()));
        PlayerInfo secondInfo = new PlayerInfo();
        Element sndPl = (Element) nodes.get(5);
        secondInfo.setReportLink(sndPl.attr("href"));
        secondInfo.setName(sndPl.text());
        secondInfo.setColor(Color.parse(((Element) nodes.get(7)).text()));
        String secondPlayerPart = ((TextNode) nodes.get(8)).text();
        separator = secondPlayerPart.indexOf(')');
        enchancePlayerResult(secondInfo, secondPlayerPart.substring(0, separator));
        String templateAndTime = secondPlayerPart.substring(separator + 1).trim();
        separator = templateAndTime.lastIndexOf(' ');
        result.setTemplate(Template.parse(templateAndTime.substring(0, separator)));
        result.setTiming(templateAndTime.substring(separator + 1));
        result.setReporter(firstInfo);
        result.setConfirmer(secondInfo);
        return result;
    }

    private static void enchancePlayerResult(PlayerInfo info, String chunk) {
        String[] parts = chunk.split(", ");
        if ("Random".equals(parts[1])) {
            info.setCastle(Castle.parse(parts[2]));
            info.setStartH(parts[3]);
            info.setFinalH(parts[4]);
            return;
        }

        if ("Random".equals(parts[2])) {
            info.setCastle(Castle.parse(parts[1]));
            info.setStartH(parts[3]);
            info.setFinalH(parts[4]);
            return;
        }

        info.setCastle(Castle.parse(parts[1]));
        info.setStartH(parts[2]);
        info.setFinalH(parts[3]);
    }

    private static GameResult parseUnconfirmed(List<Node> nodes) {
        GameResult result = new GameResult();
        result.setDate(LocalDate.parse(((TextNode) nodes.get(0)).text().trim(), DATE_FORMATTER));
        PlayerInfo firstInfo = new PlayerInfo();
        Element fstPl = (Element) nodes.get(1);
        firstInfo.setReportLink(fstPl.attr("href"));
        firstInfo.setName(fstPl.text());
        firstInfo.setColor(Color.parse(((Element) nodes.get(3)).text()));
        String firstPlayerPart = ((TextNode) nodes.get(4)).text();
        int separator = firstPlayerPart.indexOf(')');
        enchancePlayerResult(firstInfo, firstPlayerPart.substring(0, separator));
        String firstPlayerAndResult = firstPlayerPart.substring(separator + 1, firstPlayerPart.length() - 2).trim();
        separator = firstPlayerAndResult.indexOf(' ');
        result.setResult(Result.valueOf(firstPlayerAndResult.substring(0, separator).toUpperCase().trim()));
        PlayerInfo secondInfo = new PlayerInfo();
        secondInfo.setName(firstPlayerAndResult.substring(separator + 1));
        secondInfo.setColor(Color.parse(((Element) nodes.get(5)).text()));
        String secondPlayerPart = ((TextNode) nodes.get(6)).text();
        separator = secondPlayerPart.indexOf(')');
        enchancePlayerResult(secondInfo, secondPlayerPart.substring(0, separator));
        String templateAndTime = secondPlayerPart.substring(separator + 1).trim();
        separator = templateAndTime.lastIndexOf(' ');
        result.setTemplate(Template.parse(templateAndTime.substring(0, separator)));
        result.setTiming(templateAndTime.substring(separator + 1));
        result.setReporter(firstInfo);
        result.setConfirmer(secondInfo);
        return result;
    }

    public static void main(String[] args) throws IOException {
        mapper.writeValue(new FileOutputStream("/home/ihromant/results.json"), new StatisticsCollector().collect());
    }
}