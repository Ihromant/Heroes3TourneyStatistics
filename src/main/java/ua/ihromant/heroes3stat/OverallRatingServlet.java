package ua.ihromant.heroes3stat;

import org.thymeleaf.context.Context;
import ua.ihromant.config.Config;
import ua.ihromant.data.GlobalStatistics;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "overallRatingServlet", value = "/rating")
public class OverallRatingServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("overall", GlobalStatistics.instance().getOverall());
        Config.THYMELEAF.process("/templates/overallRating.html", context, response.getWriter());
    }

    public static String getInfo() {
        return "Version: " + System.getProperty("java.version")
                + " OS: " + System.getProperty("os.name")
                + " User: " + System.getProperty("user.name");
    }

}
