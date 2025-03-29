package it.unicam.cs.bdslab.triplematcher.filter.distance.filter;

import org.biojava.nbio.structure.*;
import org.biojava.nbio.structure.Chain;
import org.biojava.nbio.structure.GroupType;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.contact.Pair;
import org.biojava.nbio.structure.secstruc.SecStrucCalc;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TertiaryStructure {

    private final Structure structure;
    private double threshold; //Value between 4.5 and 12 ångström
    private SecStrucCalc secondaryStructure;
    private double[][] distanceMatrix;
    private String distanceMatrixCalculationMethod;
    private String sequence;

    private Chain specifiedChain;
    /**
     * Creates a new TertiaryStructure from a PDB file's structure
     * @param structure the structure extracted from the PDB file
     */
    public TertiaryStructure(Structure structure) {
        this.structure = structure;
        this.sequence = null;
        this.threshold = 4;
        this.secondaryStructure = null;
        this.distanceMatrix = null;
        this.distanceMatrixCalculationMethod = "default";
        this.specifiedChain = null;
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
        int groupsNumber = getNonHetatmGroupsCounter(this.getChain("16S"));
        // TODO: Make the distance matrix a triangular matrix
        double[][] distanceMatrix = new double[groupsNumber][groupsNumber];
        int moleculeCount = 0;
        Chain chain  = this.getChain("16S");
        for (Group currentMolecule : chain.getAtomGroups()) {
            if (currentMolecule.getType() != GroupType.HETATM) {
                int comparedMoleculeCount = 0;
                for (Group comparisonMolecule : chain.getAtomGroups()) {
                    if (comparisonMolecule.getType() != GroupType.HETATM) {
                        distanceMatrix[moleculeCount][comparedMoleculeCount] = Calc.getDistance(Calc.centerOfMass(currentMolecule.getAtoms().toArray(new Atom[0])), Calc.centerOfMass(comparisonMolecule.getAtoms().toArray(new Atom[0])));
                        comparedMoleculeCount++;
                    }
                }
                moleculeCount++;
            }
        }
        this.distanceMatrix = distanceMatrix;
    }
    /**
     * Calculates the secondary structure of the structure
     * @param type a string like 16S, 23S, 5S, tRNA...
     * @return the specified chain or the first chain if no chain is found
     */
    private Chain getChain(String type) {
        for (Chain chain : this.structure.getChains()) {
            // TODO: se ce ne sono  più di una guarda la sequenza e prendi quella che fa match
            if (chain.getName().contains(type)) {
                return chain;
            }
        }
        return this.specifiedChain == null ? this.structure.getChains().get(0) : this.specifiedChain;
    }

    private void calculateDistanceMatrixDefault(){
        Atom[] representativeAtomsArray = this.specifiedChain == null ? StructureTools.getRepresentativeAtomArray(this.structure) : this.getRepresentativeAtomArrayFromSpecifiedChains(this.specifiedChain);
        double[][] distanceMatrix = new double[representativeAtomsArray.length][representativeAtomsArray.length];
        for(int i=0; i<representativeAtomsArray.length; i++)
            for(int j=0; j<representativeAtomsArray.length; j++)
                distanceMatrix[i][j] = Calc.getDistance(representativeAtomsArray[i], representativeAtomsArray[j]);
        this.distanceMatrix = distanceMatrix;
    }

    private Atom[] getRepresentativeAtomArrayFromSpecifiedChains(Chain chain) {
        ArrayList<Atom> tempRepresentativeAtomsArray = new ArrayList<>();
        tempRepresentativeAtomsArray.addAll(new ArrayList<>(Arrays.asList(StructureTools.getRepresentativeAtomArray(chain))));
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

}
