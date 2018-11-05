package ua.ihromant.heroes3stat;

import org.thymeleaf.context.Context;
import ua.ihromant.config.Config;
import ua.ihromant.data.GlobalStatistics;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@WebServlet(name = "overallPlayerServlet", value = "/rating/overall/*")
public class OverallPlayerServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String player = request.getPathInfo().split("/")[1].toLowerCase();

        Context context = new Context(Locale.ENGLISH);
        context.setVariable("player", GlobalStatistics.getInstance().getOverall().getItems().get(player));
        context.setVariable("lastUpdate", GlobalStatistics.getInstance().getLastUpdate());
        Config.THYMELEAF.process("/templates/playerGames.html", context, response.getWriter());
    }
}
