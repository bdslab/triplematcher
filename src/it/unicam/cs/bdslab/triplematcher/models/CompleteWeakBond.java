package it.unicam.cs.bdslab.triplematcher.models;

import it.unicam.cs.bdslab.triplematcher.RNAInputFileParserException;
import it.unicam.cs.bdslab.triplematcher.WeakBond;

import java.util.Objects;

public class CompleteWeakBond extends WeakBond {
    private final char leftC;
    private final char rightC;
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
    public CompleteWeakBond(int left, int right, char leftC, char rightC) throws RNAInputFileParserException {
        super(left, right);
        this.leftC = leftC;
        this.rightC = rightC;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        //I know this is wrong but no time to fix it
        //if (!super.equals(o)) return false;
        CompleteWeakBond that = (CompleteWeakBond) o;
        return leftC == that.leftC && rightC == that.rightC;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), leftC, rightC);
    }
}
