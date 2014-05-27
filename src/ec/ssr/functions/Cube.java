/*
  Copyright 2012 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.ssr.functions;

import ec.*;
import ec.app.regression.*;
import ec.gp.*;

/* 
 * Cube.java
 * 
 * Created: Mon Apr 23 17:15:35 EDT 2012
 * By: Sean Luke
 
 <p>This function appears in the Korns function set, and is just x * x * x
 <p>M. F. Korns. Accuracy in Symbolic Regression. In <i>Proc. GPTP.</i> 2011.
 
*/

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class Cube extends GPNode implements Function{
    public String toString() { 
        return "cube"; 
    }

    public int expectedChildren() { 
        return 1; 
    }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem) {
        RegressionData rd = ((RegressionData)(input));

        children[0].eval(state,thread,input,stack,individual,problem);
        rd.x = rd.x * rd.x * rd.x;
    }

    @Override
    public double eval(double[] val) {
        Double evaluated = ((Function)children[0]).eval(val);
        return evaluated*evaluated*evaluated;
    }

    @Override
    public String print() {
        return "(" + ((Function)children[0]).print() + ")^3";
    }
    
    @Override
    public int getNumNodes() {
        return numNodes(GPNode.NODESEARCH_ALL);
    }
}



