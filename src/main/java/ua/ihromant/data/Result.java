package ua.ihromant.data;

public enum Result {
    DEF, LOSE, DRAW;

    public Result reversed() {
        switch (this) {
            case DEF:
                return LOSE;
            case LOSE:
                return DEF;
            default:
                return DRAW;
        }
    }
}
