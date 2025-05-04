package it.unicam.cs.bdslab.triplematcher.filter.distance.parser;

public enum Direction {
    LEFT_TO_RIGHT_FIRST_BOND,
    LEFT_TO_RIGHT_SECOND_BOND,
    RIGHT_TO_LEFT_FIRST_BOND,
    RIGHT_TO_LEFT_SECOND_BOND,
    CROSS_LEFT_TO_RIGHT_FIRST_BOND,
    CROSS_LEFT_TO_RIGHT_SECOND_BOND,
    CROSS_RIGHT_TO_LEFT_FIRST_BOND,
    CROSS_RIGHT_TO_LEFT_SECOND_BOND;

    public String write() {
        switch (this) {
            case LEFT_TO_RIGHT_FIRST_BOND:
                return "A";
            case LEFT_TO_RIGHT_SECOND_BOND:
                return "B";
            case RIGHT_TO_LEFT_FIRST_BOND:
                return "C";
            case RIGHT_TO_LEFT_SECOND_BOND:
                return "D";
            case CROSS_LEFT_TO_RIGHT_FIRST_BOND:
                return "CA";
            case CROSS_LEFT_TO_RIGHT_SECOND_BOND:
                return "CB";
            case CROSS_RIGHT_TO_LEFT_FIRST_BOND:
                return "CC";
            case CROSS_RIGHT_TO_LEFT_SECOND_BOND:
                return "CD";
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    }

    public boolean isFirstBond() {
        return this == LEFT_TO_RIGHT_FIRST_BOND || this == RIGHT_TO_LEFT_FIRST_BOND ||
                this == CROSS_LEFT_TO_RIGHT_FIRST_BOND || this == CROSS_RIGHT_TO_LEFT_FIRST_BOND;
    }
}
