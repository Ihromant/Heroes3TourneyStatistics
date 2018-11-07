package ua.ihromant.heroes3stat;

import org.thymeleaf.context.Context;
import ua.ihromant.config.Config;
import ua.ihromant.data.GlobalStatistics;
import ua.ihromant.data.StatisticsItem;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.function.BiFunction;

public abstract class PlayerGamesServlet extends HttpServlet {
    private final BiFunction<GlobalStatistics, String, StatisticsItem> gamesExtractor;
    public PlayerGamesServlet(BiFunction<GlobalStatistics, String, StatisticsItem> gamesExtractor) {
        this.gamesExtractor = gamesExtractor;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String player = request.getPathInfo().split("/")[1].toLowerCase();

        Context context = new Context(Locale.ENGLISH);
        context.setVariable("player", gamesExtractor.apply(GlobalStatistics.getInstance(), player));
        context.setVariable("lastUpdate", GlobalStatistics.getInstance().getLastUpdate());
        Config.THYMELEAF.process("/templates/playerGames.html", context, response.getWriter());
    }

    @WebServlet(name = "overallPlayerServlet", value = "/rating/overall/*")
    public static class OverallGamesServlet extends PlayerGamesServlet {

        public OverallGamesServlet() {
            super((stat, player) -> stat.getOverall().getItems().get(player));
        }
    }

    @WebServlet(name = "currentGamesServlet", value = "/rating/current/*")
    public static class CurrentGamesServlet extends PlayerGamesServlet {

        public CurrentGamesServlet() {
            super((stat, player) -> stat.getCurrentSeason().getItems().get(player));
        }
    }

    @WebServlet(name = "previousGamesServlet", value = "/rating/previous/*")
    public static class PreviousGamesServlet extends PlayerGamesServlet {

        public PreviousGamesServlet() {
            super((stat, player) -> stat.getPreviousSeason().getItems().get(player));
        }
    }
}
