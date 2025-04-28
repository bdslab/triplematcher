package it.unicam.cs.bdslab.triplematcher.filter.distance.utils;

import java.util.Locale;

public class Utils {
    private Utils() {
        // Prevent instantiation
    }
    public static String formatDoubleCsv(double d)  {
        return String.format(Locale.US, "%.2f", d);
    }
}
