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
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(name = "bannerUpdateServlet", value = "/cron/banners")
public class RefreshBannerServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(RefreshBannerServlet.class);
    private NavigableMap<Integer, String> monsters;
    private static final String OVERALL = "Overall rating";
    private static final String SEASON = "Season rating";
    private final GcsService gcsService = GcsServiceFactory.createGcsService();

    @Override
    public void init() {
        try (InputStream is = getClass().getResourceAsStream("/monsters.csv");
             InputStreamReader ir = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(ir)) {
            this.monsters = br.lines()
                    .map(line -> line.split(","))
                    .collect(Collectors.toMap(arr -> Integer.parseInt(arr[3]), arr -> arr[0], (a, b) -> a, TreeMap::new));
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong", e);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
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

    private String getMonster(StatisticsItem global) {
        if (global.getTotalGames() <= 20) {
            return "021";
        }

        Map.Entry<Integer, String> floor = this.monsters.floorEntry(global.getRating());
        return floor != null ? floor.getValue() : "011";
    }

    private Image generateImage(String player, StatisticsItem global, StatisticsItem current) throws IOException {
        int nickSize = 22;
        int textSize = 12;

        int labelxOff = 189 - 115;
        int overallyOff = 44 - textSize;
        int seasonyOff = 44 + 3 * textSize / 2 - 2;
        int ratingxOff = 189 - 5;
        int winsxOff = 189 + 57;
        int losesxOff = 189 + 82;

        Image border = ImagesServiceFactory.makeImage(IOUtils.toByteArray(
                getClass().getResourceAsStream("/imageParts/Border.png")));
        Image back = ImagesServiceFactory.makeImage(IOUtils.toByteArray(
                getClass().getResourceAsStream("/imageParts/Back.png")));
        Image bird = ImagesServiceFactory.makeImage(IOUtils.toByteArray(
                getClass().getResourceAsStream("/imageParts/" + getMonster(global) + ".png")));

        return ImagesServiceFactory.getImagesService()
                .composite(Arrays.asList(
                        ImagesServiceFactory.makeComposite(back, 0, 0, 1, Composite.Anchor.TOP_LEFT),
                        ImagesServiceFactory.makeComposite(getImageWithText(global.getName(), nickSize, "FCE883"),
                                189 - player.length() * nickSize / 3, 0, 1, Composite.Anchor.TOP_LEFT),
                        ImagesServiceFactory.makeComposite(getImageWithText(OVERALL, textSize, "FFFFFF"),
                                labelxOff, overallyOff, 1, Composite.Anchor.TOP_LEFT),
                        ImagesServiceFactory.makeComposite(getImageWithText(SEASON, textSize, "FFFFFF"),
                                labelxOff, seasonyOff, 1, Composite.Anchor.TOP_LEFT),
                        ImagesServiceFactory.makeComposite(getImageWithText(global.getRating() + "(" + global.getRank() + ")",
                                textSize, "FFFFFF"), ratingxOff, overallyOff, 1, Composite.Anchor.TOP_LEFT),
                        ImagesServiceFactory.makeComposite(getImageWithText((current != null ? current.getRating() : "-/-") + "(" + (current != null ? current.getRank() : "-/-") + ")",
                                textSize, "FFFFFF"), ratingxOff, seasonyOff, 1, Composite.Anchor.TOP_LEFT),
                        ImagesServiceFactory.makeComposite(getImageWithText(String.valueOf(global.getWins()),
                                textSize, "32CD32"), winsxOff, overallyOff, 1, Composite.Anchor.TOP_LEFT),
                        ImagesServiceFactory.makeComposite(getImageWithText(String.valueOf(current != null ? current.getWins() : "-/-"),
                                textSize, "32CD32"), winsxOff, seasonyOff, 1, Composite.Anchor.TOP_LEFT),
                        ImagesServiceFactory.makeComposite(getImageWithText(String.valueOf(global.getLoses()),
                                textSize, "FF0000"), losesxOff, overallyOff, 1, Composite.Anchor.TOP_LEFT),
                        ImagesServiceFactory.makeComposite(getImageWithText(String.valueOf((current != null ? current.getLoses() : "-/-")),
                                textSize, "FF0000"), losesxOff, seasonyOff, 1, Composite.Anchor.TOP_LEFT),
                        ImagesServiceFactory.makeComposite(bird, 0, 0, 1, Composite.Anchor.TOP_LEFT),
                        ImagesServiceFactory.makeComposite(border, 0, 0, 1, Composite.Anchor.TOP_LEFT)),
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
                "http://chart.apis.google.com/chart?chs=%sx%s&cht=p3&chtt=%s&chts=%s,%s,l&chf=bg,s,00000000",
                size * text.length(),
                size * 6 / 4,
                URLEncoder.encode(text, StandardCharsets.UTF_8.name()),
                color,
                size))));
    }
}
