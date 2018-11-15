package ua.ihromant.config;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import ua.ihromant.parser.StatisticsCollector;

import java.nio.charset.StandardCharsets;

public class Config {
    public static final ITemplateEngine THYMELEAF = thymeleaf();

    private static ITemplateEngine thymeleaf() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(true);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver);
        return engine;
    }
}
