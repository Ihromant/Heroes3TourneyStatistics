package ua.ihromant.heroes3stat;

import freemarker.template.*;
import ua.ihromant.data.GlobalStatistics;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ratingServlet", value = "/rating")
public class RatingServlet extends HttpServlet {
  private Configuration cfg;

  @Override
  public void init() {
    cfg = new Configuration(Configuration.VERSION_2_3_27);
    cfg.setClassForTemplateLoading(RatingServlet.class, "/templates");
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    cfg.setLogTemplateExceptions(false);
    cfg.setWrapUncheckedExceptions(true);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    Template temp = cfg.getTemplate("rating.html");

    try {
      temp.process(GlobalStatistics.instance(), response.getWriter());
    } catch (TemplateException e) {
      e.printStackTrace(response.getWriter());
    }
  }

  public static String getInfo() {
    return "Version: " + System.getProperty("java.version")
          + " OS: " + System.getProperty("os.name")
          + " User: " + System.getProperty("user.name");
  }

}
