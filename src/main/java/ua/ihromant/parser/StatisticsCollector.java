package ua.ihromant.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.ihromant.data.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StatisticsCollector {
    private static final Logger LOG = LoggerFactory.getLogger(StatisticsCollector.class);

    private static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final String NEW_TOURNAMENTS = "http://forum.heroesworld.ru/forumdisplay.php?f=62";
    private static final String OLD_TOURNAMENTS = "http://forum.heroesworld.ru/forumdisplay.php?f=143";

    public static List<GameResult> collect() {
        List<String> pages = Arrays.stream(new String[] {NEW_TOURNAMENTS, OLD_TOURNAMENTS})
                .parallel()
                .flatMap(StatisticsCollector::parsePages).collect(Collectors.toList());
        List<String> themes = pages.parallelStream()
                .flatMap(StatisticsCollector::parseThemes)
                .distinct()
                .collect(Collectors.toList());

        return themes.parallelStream()
                .flatMap(StatisticsCollector::parseResults1)
                .collect(Collectors.toList());
    }

    private static Stream<String> parsePages(String link) {
        try {
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
            return IntStream.range(0, pages).mapToObj(page -> link + String.format("&page=%s&order=desc", page + 1));
        } catch (Exception e) {
            LOG.error("Error during parsing pages of " + link, e);
            return Stream.empty();
        }
    }

    private static Stream<String> parseThemes(String link) {
        try {
            Document page = Jsoup.parse(new URL(link), 10000);
            return page.select("td.alt1").stream()
                    .filter(el -> el.id() != null && el.id().startsWith("td_title"))
                    .map(el -> "http://forum.heroesworld.ru/" + el.child(0).child(1).attr("href"));
        } catch (Exception e) {
            LOG.error("Error during parsing themes of " + link, e);
            return Stream.empty();
        }
    }

    private static Stream<GameResult> parseResults1(String url) {
        try {
            Document doc = Jsoup.parse(new URL(url).openStream(), "windows-1251", url);
            Element statsTable = doc.body().getElementById("collapseobj_tournament_reports");
            if (statsTable == null) {
                return Stream.empty();
            }

            Elements records = statsTable.child(1).child(0).child(0).child(0).child(1).child(0).children();
            return records.stream().flatMap(el -> {
                ReportStatus status = ReportStatus.parse(el.child(1).text());
                List<Node> nodes = el.child(0).childNodes();

                if (status == ReportStatus.CONFIRMED
                        && validateNicknamePresent(((TextNode) nodes.get(4)).text())) { // sometimes happens on forum
                    return Stream.of(parseConfirmed(nodes));
                }

                if (status == ReportStatus.UNCONFIRMED
                        && validateNicknamePresent(((TextNode) nodes.get(4)).text())) { // sometimes happens on forum
                    return Stream.of(parseUnconfirmed(nodes));
                }

                return Stream.empty();
            }).peek(res -> res.setTourneyLink(url));
        } catch (Exception e) {
            LOG.error("Error during parsing theme " + url, e);
            return Stream.empty();
        }
    }

    private static boolean validateNicknamePresent(String text) {
        return !text.startsWith(", , , ) ")
                && !text.startsWith(", Random, , , ")
                && !text.startsWith(", , Random, , ) ");
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
            info.setStartHero(Hero.parse(parts[3]));
            info.setStartH(parts[3]);
            info.setFinalHero(Hero.parse(parts[4]));
            info.setFinalH(parts[4]);
            info.setRandomInfo(RandomInfo.CASTLE);
            return;
        }

        if ("Random".equals(parts[2])) {
            info.setCastle(Castle.parse(parts[1]));
            info.setStartHero(Hero.parse(parts[3]));
            info.setStartH(parts[3]);
            info.setFinalHero(Hero.parse(parts[4]));
            info.setFinalH(parts[4]);
            info.setRandomInfo(RandomInfo.HERO);
            return;
        }

        info.setCastle(Castle.parse(parts[1]));
        info.setStartHero(Hero.parse(parts[2]));
        info.setStartH(parts[2]);
        info.setFinalHero(Hero.parse(parts[3]));
        info.setFinalH(parts[3]);
        info.setRandomInfo(RandomInfo.NO);
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
}