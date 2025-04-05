package it.unicam.cs.bdslab.triplematcher.filter.distance.filter;

import org.biojava.nbio.structure.*;
import org.biojava.nbio.structure.Chain;
import org.biojava.nbio.structure.GroupType;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.secstruc.SecStrucCalc;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DistanceMatrixCalculator {

    private static final int MAX_EDIT_DISTANCE_LENGTH = 100;
    private final Structure structure;
    private double threshold; //Value between 4.5 and 12 ångström
    private SecStrucCalc secondaryStructure;
    private double[][] distanceMatrix;
    private String distanceMatrixCalculationMethod;
    private String sequence;

    private Chain specifiedChain;
    private String rnaType;
    private String sequenceToFind;
    /**
     * Creates a new TertiaryStructure from a PDB file's structure
     * @param structure the structure extracted from the PDB file
     * @param RNAType a string like 16S, 23S, 5S, tRNA...
     * @param sequence the sequence of the RNA to check the distance
     */
    public DistanceMatrixCalculator(Structure structure, String RNAType, String sequence) {
        this.structure = structure;
        this.sequence = null;
        this.threshold = 12;
        this.secondaryStructure = null;
        this.distanceMatrix = null;
        this.distanceMatrixCalculationMethod = "centerofmass";
        this.specifiedChain = null;
        this.rnaType = RNAType;
        this.sequenceToFind = sequence;
    }

    /**
     * Calculates the structure's distance matrix, considering aminos / nucleotide's center of mass or taking distance between P/CA atoms as comparison method,
     * depending on distance matrix calculation method
     * @return distance matrix
     */
    public double[][] getDistanceMatrix(){
        if(this.distanceMatrixCalculationMethod.equals("default")){
            this.calculateDistanceMatrixDefault();
        }
        else if (this.distanceMatrixCalculationMethod.equals("centerofmass")){
            this.calculateDistanceMatrixCenterOfMass();
        }
        return this.distanceMatrix;
    }

    private void calculateDistanceMatrixCenterOfMass(){
        List<Group> nonHetatmGroups = this.getChain().getAtomGroups().stream()
                .filter(f -> f.getType() != GroupType.HETATM)
                .collect(Collectors.toList());
        int groupsNumber = nonHetatmGroups.size();
        double[][] distanceMatrix = new double[groupsNumber][];
        for (int i = 0; i < nonHetatmGroups.size(); i++) {
            distanceMatrix[i] = new double[i + 1];
            distanceMatrix[i][i] = 0;
            for (int j = 0; j < i; j++) {
                distanceMatrix[i][j] = Calc.getDistance(Calc.centerOfMass(nonHetatmGroups.get(i).getAtoms().toArray(new Atom[0])), Calc.centerOfMass(nonHetatmGroups.get(j).getAtoms().toArray(new Atom[0])));
            }
        }
        this.distanceMatrix = distanceMatrix;
    }
    /**
     * Find the best chain in the structure
     * @return the specified chain or the first chain if no chain is found
     */
    protected Chain getChain() {
        if (this.specifiedChain != null) {
            return this.specifiedChain;
        }
        List<ResultFilter> resultFilters = new ArrayList<>();
        for (Chain chain : this.structure.getChains()) {
            if (chain.isNucleicAcid()) {
                resultFilters.add(getResultFilter(chain));
            }
        }
        // If no chain is found, return the first chain
        this.specifiedChain = resultFilters.stream()
                .min(ResultFilter::compareTo)
                .orElseThrow(() -> new IllegalStateException("No chain in the structure"))
                .chain;
        return this.specifiedChain;
    }

    private ResultFilter getResultFilter(Chain chain) {
        boolean containType = chain.toString().contains(rnaType);
        String sequence = chain.getAtomSequence().substring(0, Math.min(MAX_EDIT_DISTANCE_LENGTH, chain.getAtomSequence().length()));
        String target = sequenceToFind.substring(0, Math.min(sequence.length(), sequenceToFind.length() - 1));
        // for efficency, we use only first 100 characters
        int[][] alignmentMatrix = new int[target.length() + 1][sequence.length() + 1];
        for (int i = 0; i <= target.length(); i++) {
            alignmentMatrix[i][0] = i;
        }
        for (int j = 0; j <= sequence.length(); j++) {
            alignmentMatrix[0][j] = j;
        }
        for (int i = 1; i <= target.length(); i++) {
            for (int j = 1; j <= sequence.length(); j++) {
                int cost = (target.charAt(i - 1) == sequence.charAt(j - 1)) ? 0 : 1;
                alignmentMatrix[i][j] = Math.min(Math.min(alignmentMatrix[i - 1][j] + 1, alignmentMatrix[i][j - 1] + 1), alignmentMatrix[i - 1][j - 1] + cost);
            }
        }
        int editDistance = alignmentMatrix[target.length()][sequence.length()];
        return new ResultFilter(chain, containType, editDistance);
    }

    private void calculateDistanceMatrixDefault(){
        Atom[] representativeAtomsArray = this.specifiedChain == null ? StructureTools.getRepresentativeAtomArray(this.structure) : this.getRepresentativeAtomArrayFromSpecifiedChains(this.specifiedChain);
        double[][] distanceMatrix = new double[representativeAtomsArray.length][representativeAtomsArray.length];
        for(int i=0; i < representativeAtomsArray.length; i++)
            for(int j=0; j < representativeAtomsArray.length; j++)
                distanceMatrix[i][j] = Calc.getDistance(representativeAtomsArray[i], representativeAtomsArray[j]);
        this.distanceMatrix = distanceMatrix;
    }

    private Atom[] getRepresentativeAtomArrayFromSpecifiedChains(Chain chain) {
        ArrayList<Atom> tempRepresentativeAtomsArray = new ArrayList<>(
                new ArrayList<>(Arrays.asList(StructureTools.getRepresentativeAtomArray(chain)))
        );
        Atom[] representativeAtomsArray = new Atom[tempRepresentativeAtomsArray.size()];
        tempRepresentativeAtomsArray.toArray(representativeAtomsArray);
        return  representativeAtomsArray;
    }


    private int getNonHetatmGroupsCounter(Chain chain){
        int nonHetatmGroupsCounter = 0;
        for(Group currentGroup : chain.getAtomGroups())
            if(currentGroup.getType() != GroupType.HETATM)
                nonHetatmGroupsCounter++;
        return nonHetatmGroupsCounter;
    }

    /**
     * Print the distance matrix
     */
    public void printDistanceMatrix(){
        for (int i=0; i<this.distanceMatrix.length; i++) {
            for (int j=0; j<this.distanceMatrix.length; j++) {
                System.out.print("pos " + i + " " + j + ": " + this.distanceMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public Structure getStructure() {
        return structure;
    }

    public String getSequence(){
        StringBuilder builder = new StringBuilder();
        for(Chain currentChain : this.structure.getChains())
            for(Group currentGroup : currentChain.getAtomGroups())
                if(currentGroup.isAminoAcid() || currentGroup.isNucleotide())
                    builder.append(StructureTools.get1LetterCode(currentGroup.getPDBName()));
        return builder.toString();
    }

    /**
     * Sets the distance matrix calculation method.
     * @param calculationMethod chosen calculation method, can be either "default" or "centerofmass"
     */
    public void setDistanceMatrixCalculationMethod(String calculationMethod){
        if(calculationMethod.toLowerCase(Locale.ROOT).equals("default") || calculationMethod.toLowerCase(Locale.ROOT).equals("centerofmass"))
            this.distanceMatrixCalculationMethod = calculationMethod.toLowerCase(Locale.ROOT);
    }

    /**
     * Prints the distance matrix to a csv file
     */
    public void printDistanceMatrixToCSV(){
        try {
            double[][] distanceMatrix = this.getDistanceMatrix();
            FileWriter writer;
            if(this.distanceMatrixCalculationMethod.equals("default"))
                writer = new FileWriter("src/main/resources/DefaultDistanceMatrix.csv");
            else
                writer = new FileWriter("src/main/resources/DistanceMatrixCenterOfMass.csv");
            for (int i = 0; i < distanceMatrix.length; i++) {
                for (int j = 0; j < distanceMatrix.length; j++) {
                    writer.append(String.valueOf(i)).append(" ").append(String.valueOf(j)).append(":").append(" ").append(String.valueOf(distanceMatrix[i][j])).append("   ");
                }
                writer.append("\n");
                writer.append("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Replace current sequence with a new one
     * @param sequence new sequence
     */
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    /**
     * Replace the current rnaType
     * @param rnaType the new rnaType
     */
    public void setRnaType(String rnaType) {
        this.rnaType = rnaType;
    }
    private static class ResultFilter implements Comparable<ResultFilter> {
        private final Chain chain;
        private final boolean containType;
        private final int editDistance;

        private ResultFilter(Chain chain, boolean containType, int editDistance) {
            this.chain = chain;
            this.containType = containType;
            this.editDistance = editDistance;
        }

        @Override
        public int compareTo(ResultFilter o) {
            if (this.containType == o.containType) {
                return Integer.compare(this.editDistance, o.editDistance);
            } else {
                return Boolean.compare(!this.containType, !o.containType);
            }
        }

    }

}
