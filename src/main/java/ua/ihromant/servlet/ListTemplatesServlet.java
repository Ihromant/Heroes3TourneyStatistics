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
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/templates")
public class ListTemplatesServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("templates", GlobalStatistics.getInstance().getTemplates());
        context.setVariable("datamodels", GlobalStatistics.getInstance().getTemplates().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().getTimingMap().entrySet().stream()
                                .map(en -> String.format("['%s', %s]",
                                        en.getKey(),
                                        en.getValue()))
                                .collect(Collectors.joining(",")))));
        Config.THYMELEAF.process("/templates/listTemplates.html", context, response.getWriter());
    }
}
