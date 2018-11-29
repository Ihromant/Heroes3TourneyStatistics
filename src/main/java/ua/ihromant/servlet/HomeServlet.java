package ua.ihromant.servlet;

import org.thymeleaf.context.Context;
import ua.ihromant.config.Config;
import ua.ihromant.data.GlobalStatistics;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Collectors;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("gamesActivity",
                GlobalStatistics.getInstance().getOverall().getActivities().entrySet().stream()
                        .map(e -> String.format("[new Date('%s'), %s]",
                                DateTimeFormatter.ISO_LOCAL_DATE.format(e.getKey()),
                                e.getValue()))
                        .collect(Collectors.joining(",")));
        context.setVariable("playerActivity",
                GlobalStatistics.getInstance().getOverall().getPlayerActivities().entrySet().stream()
                        .map(e -> String.format("[new Date('%s'), %s]",
                                DateTimeFormatter.ISO_LOCAL_DATE.format(e.getKey()),
                                e.getValue()))
                        .collect(Collectors.joining(",")));
        Config.THYMELEAF.process("/templates/home.html", context, response.getWriter());
    }
}
