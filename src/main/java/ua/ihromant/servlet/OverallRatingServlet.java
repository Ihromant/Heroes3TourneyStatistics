package ua.ihromant.servlet;

import org.thymeleaf.context.Context;
import ua.ihromant.config.Config;
import ua.ihromant.data.GlobalStatistics;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/rating")
public class OverallRatingServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("overall", GlobalStatistics.getInstance().getOverall());
        context.setVariable("currentSeason", GlobalStatistics.getInstance().getCurrentSeason());
        context.setVariable("previousSeason", GlobalStatistics.getInstance().getPreviousSeason());
        context.setVariable("lastUpdate", GlobalStatistics.getInstance().getLastUpdate());
        Config.THYMELEAF.process("/templates/overallRating.html", context, response.getWriter());
    }
}
