package ua.ihromant;

import java.util.Objects;

public class PlayerInfo {
    private final String name;
    private final Color color;
    private final Castle castle;
    private final Hero startingHero;
    private final Hero finalHero;

    public PlayerInfo(String name, Color color, Castle castle, Hero startingHero, Hero finalHero) {
        this.name = name;
        this.color = color;
        this.castle = castle;
        this.startingHero = startingHero;
        this.finalHero = finalHero;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public Castle getCastle() {
        return castle;
    }

    public Hero getStartingHero() {
        return startingHero;
    }

    public Hero getFinalHero() {
        return finalHero;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerInfo that = (PlayerInfo) o;
        return Objects.equals(name, that.name) &&
                color == that.color &&
                castle == that.castle &&
                startingHero == that.startingHero &&
                finalHero == that.finalHero;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, castle, startingHero, finalHero);
    }

    @Override
    public String toString() {
        return "PlayerInfo{" +
                "name='" + name + '\'' +
                ", color=" + color +
                ", castle=" + castle +
                ", startingHero=" + startingHero +
                ", finalHero=" + finalHero +
                '}';
    }
}
