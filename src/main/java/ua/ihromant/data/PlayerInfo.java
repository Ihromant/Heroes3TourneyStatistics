package ua.ihromant.data;

public class PlayerInfo {
    private String name;
    private String reportLink;
    private Color color;
    private Castle castle;
    private Hero startingHero;
    private String startH;
    private Hero finalHero;
    private String finalH;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReportLink() {
        return reportLink;
    }

    public void setReportLink(String reportLink) {
        this.reportLink = reportLink;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Castle getCastle() {
        return castle;
    }

    public void setCastle(Castle castle) {
        this.castle = castle;
    }

    public Hero getStartingHero() {
        return startingHero;
    }

    public void setStartingHero(Hero startingHero) {
        this.startingHero = startingHero;
    }

    public String getStartH() {
        return startH;
    }

    public void setStartH(String startH) {
        this.startH = startH;
    }

    public Hero getFinalHero() {
        return finalHero;
    }

    public void setFinalHero(Hero finalHero) {
        this.finalHero = finalHero;
    }

    public String getFinalH() {
        return finalH;
    }

    public void setFinalH(String finalH) {
        this.finalH = finalH;
    }

    @Override
    public String toString() {
        return "PlayerInfo{" +
                "name='" + name + '\'' +
                ", reportLink='" + reportLink + '\'' +
                ", color=" + color +
                ", castle=" + castle +
                ", startingHero=" + startingHero +
                ", startH='" + startH + '\'' +
                ", finalHero=" + finalHero +
                ", finalH='" + finalH + '\'' +
                '}';
    }
}
