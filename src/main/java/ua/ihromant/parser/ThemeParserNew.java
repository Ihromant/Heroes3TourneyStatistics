package ua.ihromant.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import ua.ihromant.data.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThemeParserNew {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final HttpRequestFactory REQUEST_FACTORY
            = new NetHttpTransport().createRequestFactory();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public List<GameResult> parseResults(String url) throws IOException {
        Document doc = Jsoup.parse(new URL(url).openStream(), "windows-1251", url);
        Element tourneyMetadata = doc.body().getElementById("hw-tournament-data");
        if (tourneyMetadata == null) {
            return Collections.emptyList();
        }

        Integer tourneyId = Integer.parseInt(tourneyMetadata.attr("data-tournament"));

        String resp = REQUEST_FACTORY.buildGetRequest(
                new GenericUrl("http://forum.heroesworld.ru/ajax.php?do=get_tournament_reports&tournament=" + tourneyId))
                .execute().parseAsString();
        List<HWGameData> hwResults = MAPPER.readValue(resp, new TypeReference<List<HWGameData>>() {});
        List<GameResult> results = new ArrayList<>();
        for (HWGameData hw : hwResults) {
            List<Node> nodes = Jsoup.parseBodyFragment("<div>" + hw.getReport() + "</div>").body().child(0).childNodes();

            if (!hw.isIsApproved() && !hw.isIsDiscarded()) {
                results.add(parseUnconfirmed(nodes));
            }

            if (hw.isIsApproved() && !hw.isIsDiscarded()) {
                results.add(parseConfirmed(nodes));
            }
        }
        results.forEach(res -> res.setTourneyLink(url));
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
