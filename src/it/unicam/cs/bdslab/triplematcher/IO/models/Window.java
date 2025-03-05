package it.unicam.cs.bdslab.triplematcher.IO.models;

public class Window {
    private final int start;
    private final int end;


    public Window(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

}
