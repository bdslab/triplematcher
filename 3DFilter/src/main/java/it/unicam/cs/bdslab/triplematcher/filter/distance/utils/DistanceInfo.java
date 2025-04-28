package it.unicam.cs.bdslab.triplematcher.filter.distance.utils;

public class DistanceInfo {
    private final int indexFirst;
    private final int indexSecond;
    private final double distance;

    public DistanceInfo(int indexFirst, int indexSecond, double distance)  {
        this.indexFirst = indexFirst;
        this.indexSecond = indexSecond;
        this.distance = distance;
    }

    public int getIndexFirst() {
        return indexFirst;
    }

    public int getIndexSecond() {
        return indexSecond;
    }

    public double getDistance() {
        return distance;
    }

    public String toString() {
        return "DistanceInfo{" +
                "indexFirst=" + indexFirst +
                ", indexSecond=" + indexSecond +
                ", distance=" + distance +
                '}';
    }

    public String formatForCSV() {
        return "(" + indexFirst + "; " + indexSecond + "; " + Utils.formatDoubleCsv(distance) + ")";
    }
}
