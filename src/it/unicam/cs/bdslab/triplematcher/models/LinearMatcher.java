package it.unicam.cs.bdslab.triplematcher.models;

import it.unicam.cs.bdslab.triplematcher.models.EditOperation;
import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.models.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class LinearMatcher<T> {

    private final int tolerance;
    private final int minLength;

    /**
     * Constructor
     * @param tolerance the tolerance, the maximum distance between the pattern and the text
     * @param minLength the minimum length of a match to be considered
     */
    public LinearMatcher(int tolerance, int minLength) {
        this.tolerance = tolerance;
        this.minLength = minLength;
    }

    /**
     * this method solve the problem of finding the best match between a text and a pattern
     * @param text the text to be matched
     * @param pattern the pattern to be matched
     * @return a list of matches
     */
    public List<Match<T>> solve(RNASecondaryStructure rna, List<T> text, T pattern) {
        //The algorithm consist in 2 steps:
        //1. Group all the consecutive elements in the pattern
        //2. Filter and merge the groups considering a certain tolerance
        List<Group<T>> groups = group(text);
        List<MergedGroup<T>> mergedGroups = filterAndMerge(groups, pattern);
        //For each merged group, if the length is greater than the minimum length, create a match
        List<Match<T>> matches = new ArrayList<>();
        for (MergedGroup<T> mergedGroup : mergedGroups) {
            if (mergedGroup.elements.size() >= minLength) {
                List<T> fullPattern = Utils.replicate(pattern, mergedGroup.elements.size() - mergedGroup.distance);
                int row = fullPattern.size();
                int col = mergedGroup.end + 1;
                matches.add(new Match<>(row, col, mergedGroup.distance, text, fullPattern, mergedGroup.getEditOperations()));
            }
        }
        return matches;
    }

    private List<Group<T>> group(List<T> text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Group<T>> groups = new ArrayList<Group<T>>();
        List<T> elements = new ArrayList<T>();
        int start = 0;
        elements.add(text.get(0));
        for (int i = 1; i < text.size(); i++) {
            if (text.get(i).equals(text.get(i - 1))) {
                elements.add(text.get(i));
            } else {
                groups.add(new Group<T>(start, i - 1, elements));
                elements = new ArrayList<T>();
                elements.add(text.get(i));
                start = i;
            }
        }
        groups.add(new Group<T>(start, text.size() - 1, elements));
        return groups;
    }

    private List<MergedGroup<T>> filterAndMerge(List<Group<T>> groups, T pattern) {
        List<MergedGroup<T>> mergedGroups = new ArrayList<MergedGroup<T>>();
        for (int i = 0; i < groups.size(); i++) {
            if (!groups.get(i).elements.get(0).equals(pattern)) {
                continue;
            }
            Group<T> group = groups.get(i);
            int start = group.start;
            int end = group.end;
            List<T> elements = group.elements;
            int realDistance = 0;
            int distance = 0;
            int j = i + 1;
            while (j < groups.size() && distance <= tolerance) {
                Group<T> nextGroup = groups.get(j);
                if (!nextGroup.elements.get(0).equals(pattern)) {
                    distance += nextGroup.elements.size();
                } else {
                    end = nextGroup.end;
                }
                if (distance <= tolerance) {
                    elements.addAll(nextGroup.elements);
                    realDistance = distance;
                }
                j++;
            }
            mergedGroups.add(new MergedGroup<T>(start, end, elements, realDistance, pattern));
        }
        return mergedGroups;
    }

    private static class Group<T> {
        private final int start;
        private final int end;
        private final List<T> elements;
        public Group(int start, int end, List<T> elements) {
            this.start = start;
            this.end = end;
            this.elements = elements;
        }
    }

    private static class MergedGroup<T> {
        private final int start;
        private final int end;
        private final List<T> elements;
        private final int distance;
        private final T leader;
        public MergedGroup(int start, int end, List<T> elements, int distance, T leader) {
            this.start = start;
            this.end = end;
            this.elements = elements;
            this.distance = distance;
            this.leader = leader;
        }

        public List<EditOperation<T>> getEditOperations() {
            List<EditOperation<T>> editOperations = new ArrayList<>();
            for (int i = 0; i < elements.size(); i++) {
                editOperations.add(new EditOperation<>(leader, elements.get(i)));
            }
            return editOperations;
        }
    }

}
