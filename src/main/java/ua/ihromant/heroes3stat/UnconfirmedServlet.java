package ua.ihromant.heroes3stat;

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

@WebServlet(name = "unconfirmedServlet", value = "/unconfirmed")
public class UnconfirmedServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("unconfirmed", GlobalStatistics.getInstance().getUnconfirmed());
        context.setVariable("lastUpdate", GlobalStatistics.getInstance().getLastUpdate());
        Config.THYMELEAF.process("/templates/unconfirmed.html", context, response.getWriter());
    }
}
