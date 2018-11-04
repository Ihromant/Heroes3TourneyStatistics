package ua.ihromant.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;

public class GameResult implements Cloneable {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
    private String tourneyLink;
    private PlayerInfo reporter;
    private PlayerInfo confirmer;
    private Template template;
    private Result result;
    private String timing;
    @JsonIgnore
    private int previousReporter;
    @JsonIgnore
    private int reporterChange;
    @JsonIgnore
    private int previousConfirmer;
    @JsonIgnore
    private int confirmerChange;

    public GameResult cloned() {
        try {
            return (GameResult) this.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e); // never happens
        }
    }

    public GameResult reversed() {
        GameResult reversed = this.cloned();
        reversed.previousConfirmer = this.previousReporter;
        reversed.previousReporter = this.previousConfirmer;
        reversed.reporterChange = this.confirmerChange;
        reversed.confirmerChange = this.reporterChange;
        reversed.result = this.result.reversed();
        reversed.confirmer = this.reporter;
        reversed.reporter = this.confirmer;
        return reversed;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTourneyLink() {
        return tourneyLink;
    }

    public void setTourneyLink(String tourneyLink) {
        this.tourneyLink = tourneyLink;
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

    public int getPreviousReporter() {
        return previousReporter;
    }

    public void setPreviousReporter(int previousReporter) {
        this.previousReporter = previousReporter;
    }

    public int getReporterChange() {
        return reporterChange;
    }

    public void setReporterChange(int reporterChange) {
        this.reporterChange = reporterChange;
    }

    public int getPreviousConfirmer() {
        return previousConfirmer;
    }

    public void setPreviousConfirmer(int previousConfirmer) {
        this.previousConfirmer = previousConfirmer;
    }

    public int getConfirmerChange() {
        return confirmerChange;
    }

    public void setConfirmerChange(int confirmerChange) {
        this.confirmerChange = confirmerChange;
    }

    @Override
    public String toString() {
        return "GameResult{" +
                "date=" + date +
                ", tourneyLink='" + tourneyLink + '\'' +
                ", reporter=" + reporter +
                ", confirmer=" + confirmer +
                ", template=" + template +
                ", result=" + result +
                ", timing='" + timing + '\'' +
                ", previousReporter=" + previousReporter +
                ", reporterChange=" + reporterChange +
                ", previousConfirmer=" + previousConfirmer +
                ", confirmerChange=" + confirmerChange +
                '}';
    }
}
