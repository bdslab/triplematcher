package it.unicam.cs.bdslab.triplematcher.filter.distance.parser;

public enum Direction {
    LEFT_TO_RIGHT_FIRST_BOND,
    LEFT_TO_RIGHT_SECOND_BOND,
    RIGHT_TO_LEFT_FIRST_BOND,
    RIGHT_TO_LEFT_SECOND_BOND;

    String write() {
        switch (this) {
            case LEFT_TO_RIGHT_FIRST_BOND:
                return "A";
            case LEFT_TO_RIGHT_SECOND_BOND:
                return "B";
            case RIGHT_TO_LEFT_FIRST_BOND:
                return "C";
            case RIGHT_TO_LEFT_SECOND_BOND:
                return "D";
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    }
}
