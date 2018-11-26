package ua.ihromant.servlet;

import org.thymeleaf.context.Context;
import ua.ihromant.config.Config;
import ua.ihromant.data.GlobalStatistics;
import ua.ihromant.data.Template;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@WebServlet("/templates/*")
public class TemplateRatingServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String[] path = request.getPathInfo().split("/");
        if (path.length == 2) {
            Template template = Template.valueOf(path[1].toUpperCase());
            Context context = new Context(Locale.ENGLISH);
            context.setVariable("template", template);
            context.setVariable("templateRating", GlobalStatistics.getInstance().getTemplates().get(template));
            context.setVariable("lastUpdate", GlobalStatistics.getInstance().getLastUpdate());
            Config.THYMELEAF.process("/templates/templateRating.html", context, response.getWriter());
        }
        if (path.length == 3) {
            Template template = Template.valueOf(path[1].toUpperCase());
            String player = path[2].toLowerCase();
            Context context = new Context(Locale.ENGLISH);
            context.setVariable("player", GlobalStatistics.getInstance().getTemplates()
                    .get(template).getItems().get(player));
            context.setVariable("bannerServingUrl", String.format("[IMG]http://storage.googleapis.com/heroes3stat.appspot.com/banners/%s.png[/IMG]", player));
            Config.THYMELEAF.process("/templates/playerGames.html", context, response.getWriter());
        }
    }
}
