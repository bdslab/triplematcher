package it.unicam.cs.bdslab.triplematcher.models;

import it.unicam.cs.bdslab.triplematcher.RNAInputFileParserException;
import it.unicam.cs.bdslab.triplematcher.WeakBond;

import java.util.Objects;

public class CompleteWeakBond extends WeakBond {
    private final char leftC;
    private final char rightC;
    private final boolean cross;
    /**
     * Construct a pair of indexes representing the weak bond.
     *
     * @param left  left index (starting with 1)
     * @param right right index (starting with 1)
     * @throws RNAInputFileParserException if the left position is less than 1
     *                                     or if the left position is greater
     *                                     than or equal to the right
     *                                     position.
     */
    public CompleteWeakBond(int left, int right, char leftC, char rightC, boolean cross) throws RNAInputFileParserException {
        super(left, right);
        this.leftC = leftC;
        this.rightC = rightC;
        this.cross = cross;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        //if (!super.equals(o)) return false;
        CompleteWeakBond that = (CompleteWeakBond) o;
        return leftC == that.leftC && rightC == that.rightC;
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftC, rightC);
    }

    @Override
    public String toString() {
        return "(" + leftC + "; " + rightC + ")";
    }
}
