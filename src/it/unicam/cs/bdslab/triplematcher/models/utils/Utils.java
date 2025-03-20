package it.unicam.cs.bdslab.triplematcher.models.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
    public static int[] getMins(int[] array) {
        int[] mins = new int[array.length];
        int index = 0;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
                index = 0;
                mins[index++] = i;
            } else if (array[i] == min) {
                mins[index++] = i;
            }
        }
        return Arrays.copyOf(mins, index);
    }

    public static<T extends Comparable<T>> T getMin(T[] array) {
        T min = array[0];
        for (T t : array) {
            if (t.compareTo(min) < 0) {
                min = t;
            }
        }
        return min;
    }

    public static int getMin(int... array) {
        int min = array[0];
        for (int i : array) {
            if (i < min) {
                min = i;
            }
        }
        return min;
    }

    public static<T> List<T> replicate(T element, int times) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            list.add(element);
        }
        return list;
    }

    public static<T> List<T> reverse(List<T> list) {
        List<T> reversed = new ArrayList<>(list);
        for (int i = 0; i < reversed.size() / 2; i++) {
            T temp = reversed.get(i);
            reversed.set(i, reversed.get(reversed.size() - i - 1));
            reversed.set(reversed.size() - i - 1, temp);
        }
        return reversed;
    }

}
