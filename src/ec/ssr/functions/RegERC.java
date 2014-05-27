/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.ssr.functions;

import ec.EvolutionState;
import ec.Problem;
import ec.app.regression.RegressionData;
import ec.gp.*;
import ec.util.*;
import java.io.*;


/* 
 * RegERC.java
 * 
 * Created: Wed Nov  3 18:26:37 1999
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class RegERC extends ERC implements Function {
    public double value;

    // Koza claimed to be generating from [-1.0, 1.0] but he wasn't,
    // given the published simple-lisp code.  It was [-1.0, 1.0).  This is
    // pretty minor, but we're going to go with the code rather than the
    // published specs in the books.  If you want to go with [-1.0, 1.0],
    // just change nextDouble() to nextDouble(true, true)

    @Override
    public void resetNode(final EvolutionState state, final int thread) {
        value = state.random[thread].nextDouble() * 2 - 1.0; 
    }

    @Override
    public int nodeHashCode() {
        // a reasonable hash code
        return this.getClass().hashCode() + Float.floatToIntBits((float)value);
    }

    @Override
    public boolean nodeEquals(final GPNode node) {
        // check first to see if we're the same kind of ERC -- 
        // won't work for subclasses; in that case you'll need
        // to change this to isAssignableTo(...)
        if (this.getClass() != node.getClass()) return false;
        // now check to see if the ERCs hold the same value
        return (((RegERC)node).value == value);
    }

    @Override
    public void readNode(final EvolutionState state, final DataInput dataInput) throws IOException{
        value = dataInput.readDouble();
    }

    @Override
    public void writeNode(final EvolutionState state, final DataOutput dataOutput) throws IOException {
        dataOutput.writeDouble(value);
    }

    @Override
    public String encode(){
        return Code.encode(value); 
    }

    @Override
    public boolean decode(DecodeReturn dret) {
        // store the position and the string in case they
        // get modified by Code.java
        int pos = dret.pos;
        String data = dret.data;

        // decode
        Code.decode(dret);

        if (dret.type != DecodeReturn.T_DOUBLE) { // uh oh!

            // restore the position and the string; it was an error
            dret.data = data;
            dret.pos = pos;
            return false;
        }

        // store the data
        value = dret.d;
        return true;
    }

    @Override
    public String toStringForHumans(){ 
        return "" + (double)value; 
    }

    @Override
    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem) {
        RegressionData rd = ((RegressionData)(input));
        rd.x = value;
    }
    
    @Override
    public double eval(double[] val) {
        return value;
    }

    @Override
    public String print() {
        return value+"";
    }
    
    @Override
    public int getNumNodes() {
        return numNodes(GPNode.NODESEARCH_ALL);
    }
}