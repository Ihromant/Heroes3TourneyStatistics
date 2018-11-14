package ua.ihromant.listener;

import ua.ihromant.data.GlobalStatistics;
import ua.ihromant.parser.GlobalStatisticsRetriever;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class StartupListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        GlobalStatistics.setInstance(GlobalStatisticsRetriever.retrieve());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
