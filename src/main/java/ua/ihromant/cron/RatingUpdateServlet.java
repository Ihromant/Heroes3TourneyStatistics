package ua.ihromant.cron;

import ua.ihromant.data.GlobalStatistics;
import ua.ihromant.parser.GlobalStatisticsRetriever;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ratingUpdateServlet", value = "/cron/rating")
public class RatingUpdateServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        GlobalStatistics stat = GlobalStatisticsRetriever.retrieve();
        if (stat != null) {
            GlobalStatistics.setInstance(stat);
        }
    }
}
