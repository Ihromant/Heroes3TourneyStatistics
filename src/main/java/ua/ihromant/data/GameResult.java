package ua.ihromant.data;

import java.time.LocalDate;
import java.util.Objects;

public class GameResult {
    private LocalDate date;
    private PlayerInfo reporter;
    private PlayerInfo confirmer;
    private Template template;
    private Result result;
    private String timing;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public PlayerInfo getReporter() {
        return reporter;
    }

    public void setReporter(PlayerInfo reporter) {
        this.reporter = reporter;
    }

    public PlayerInfo getConfirmer() {
        return confirmer;
    }

    public void setConfirmer(PlayerInfo confirmer) {
        this.confirmer = confirmer;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }
}
