package ua.ihromant.cron;

import com.google.appengine.api.images.Composite;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.tools.cloudstorage.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.ihromant.data.GlobalStatistics;
import ua.ihromant.data.StatisticsItem;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "bannerUpdateServlet", value = "/cron/banners")
public class RefreshBannerServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(RefreshBannerServlet.class);
    private List<String> monsters;
    private static final String OVERALL = "Overall rating";
    private static final String SEASON = "Season rating";
    private final GcsService gcsService = GcsServiceFactory.createGcsService();

    @Override
    public void init() {
        try (InputStream is = getClass().getResourceAsStream("/monsterList.txt");
             InputStreamReader ir = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(ir)) {
            this.monsters = br.lines()
                    .peek(System.out::println)
                    .map(line -> line.split("\\s"))
                    .filter(it -> it.length == 2)
                    .sorted(Comparator.<String[], Integer>comparing(it1 -> Integer.parseInt(it1[1])).reversed())
                    .map(it -> it[0])
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong", e);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        for (Map.Entry<String, StatisticsItem> e : GlobalStatistics.getInstance().getOverall().getItems().entrySet()) {
            try {
                LOG.info("Generating banner for " + e.getKey());
                String player = e.getKey();
                Image img = generateImage(player,
                        e.getValue(),
                        GlobalStatistics.getInstance().getCurrentSeason().getItems().get(player));
                writeImageToBucket(player, img);
            } catch (Exception ex) {
                LOG.error("Caught exception while generating banner for " + e.getKey(), ex);
            }
        }
    }

    private Image generateImage(String player, StatisticsItem global, StatisticsItem current) throws IOException {
        int monsterIndex = (global.getRank() - 1) * this.monsters.size() / GlobalStatistics.getInstance().getOverall().getItems().size();
        int nickSize = 22;
        int textSize = 12;

        int labelxOff = - 6 * textSize;
        int overallyOff = -textSize / 2 + 2;
        int seasonyOff = 2 * textSize;
        int ratingxOff = 25;
        int winsxOff = 60;
        int losesxOff = 85;

        Image border = ImagesServiceFactory.makeImage(IOUtils.toByteArray(
                getClass().getResourceAsStream("/imageParts/Border.png")));
        Image back = ImagesServiceFactory.makeImage(IOUtils.toByteArray(
                getClass().getResourceAsStream("/imageParts/Back.png")));
        Image bird = ImagesServiceFactory.makeImage(IOUtils.toByteArray(
                getClass().getResourceAsStream("/imageParts/" + this.monsters.get(monsterIndex) + ".png")));

        return ImagesServiceFactory.getImagesService()
                .composite(Arrays.asList(
                        ImagesServiceFactory.makeComposite(border, 0, 0, 1, Composite.Anchor.TOP_LEFT),
                        ImagesServiceFactory.makeComposite(back, 0, 0, 1, Composite.Anchor.TOP_LEFT),
                        ImagesServiceFactory.makeComposite(getImageWithText(global.getName(), nickSize, "FCE883"),
                                - player.length() * nickSize / 4, 0, 1, Composite.Anchor.TOP_CENTER),
                        ImagesServiceFactory.makeComposite(getImageWithText(OVERALL, textSize, "FFFFFF"),
                                labelxOff, overallyOff, 1, Composite.Anchor.CENTER_CENTER),
                        ImagesServiceFactory.makeComposite(getImageWithText(SEASON, textSize, "FFFFFF"),
                                labelxOff, seasonyOff, 1, Composite.Anchor.CENTER_CENTER),
                        ImagesServiceFactory.makeComposite(getImageWithText(global.getRating() + "(" + global.getRank() + ")",
                                textSize, "FFFFFF"), ratingxOff, overallyOff, 1, Composite.Anchor.CENTER_CENTER),
                        ImagesServiceFactory.makeComposite(getImageWithText((current != null ? current.getRating() : "--") + "(" + (current != null ? current.getRank() : "--") + ")",
                                textSize, "FFFFFF"), ratingxOff, seasonyOff, 1, Composite.Anchor.CENTER_CENTER),
                        ImagesServiceFactory.makeComposite(getImageWithText(String.valueOf(global.getWins()),
                                textSize, "32CD32"), winsxOff, overallyOff, 1, Composite.Anchor.CENTER_CENTER),
                        ImagesServiceFactory.makeComposite(getImageWithText(String.valueOf(current != null ? current.getWins() : "--"),
                                textSize, "32CD32"), winsxOff, seasonyOff, 1, Composite.Anchor.CENTER_CENTER),
                        ImagesServiceFactory.makeComposite(getImageWithText(String.valueOf(global.getLoses()),
                                textSize, "FF0000"), losesxOff, overallyOff, 1, Composite.Anchor.CENTER_CENTER),
                        ImagesServiceFactory.makeComposite(getImageWithText(String.valueOf((current != null ? current.getLoses() : "--")),
                                textSize, "FF0000"), losesxOff, seasonyOff, 1, Composite.Anchor.CENTER_CENTER),
                        ImagesServiceFactory.makeComposite(bird, 0, 0, 1, Composite.Anchor.TOP_LEFT)),
                        back.getWidth(), back.getHeight(), 0x00FFFFFFL, ImagesService.OutputEncoding.PNG);
    }

    private void writeImageToBucket(String player, Image img) throws IOException {
        GcsFilename fileName = new GcsFilename("heroes3stat.appspot.com", "banners/" + player + ".png");
        gcsService.createOrReplace(fileName,
                new GcsFileOptions.Builder().mimeType("image/png").build(),
                ByteBuffer.wrap(img.getImageData()));
    }

    private Image getImageWithText(String text, int size, String color) throws IOException {
        return ImagesServiceFactory.makeImage(IOUtils.toByteArray(new URL(String.format(
                "http://chart.apis.google.com/chart?chs=%sx%s&cht=p3&chtt=%s&chts=%s,%s&chf=bg,s,00000000",
                size * text.length(),
                size * 6 / 4,
                URLEncoder.encode(text, StandardCharsets.UTF_8.name()),
                color,
                size))));
    }
}
