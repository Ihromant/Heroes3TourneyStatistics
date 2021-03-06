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
import java.util.Locale;
import java.util.stream.Collectors;

@WebServlet("/unconfirmed")
public class UnconfirmedServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("unconfirmed", GlobalStatistics.getInstance().getUnconfirmed());
        context.setVariable("lastUpdate", GlobalStatistics.getInstance().getLastUpdate());
        Config.THYMELEAF.process("/templates/unconfirmed.html", context, response.getWriter());
    }

    @WebServlet("/unconfirmed/*")
    public static class UnconfirmedPlayerServlet extends HttpServlet {
        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            String player = request.getPathInfo().split("/")[1].toLowerCase();
            Context context = new Context(Locale.ENGLISH);
            context.setVariable("unconfirmed", GlobalStatistics.getInstance().getUnconfirmed().getGames()
                    .stream().filter(res -> res.getConfirmer().getName().equalsIgnoreCase(player)).collect(Collectors.toList()));
            context.setVariable("player", player);
            context.setVariable("lastUpdate", GlobalStatistics.getInstance().getLastUpdate());
            Config.THYMELEAF.process("/templates/unconfirmedPlayer.html", context, response.getWriter());
        }
    }
}
