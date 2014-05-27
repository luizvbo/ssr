/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.ssr.functions;

import ec.*;
import ec.app.regression.RegressionData;
import ec.ssr.problems.Regression;
import ec.gp.*;

/* 
 * X.java
 * 
 * Created: Wed Nov  3 18:26:37 1999
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

public abstract class X extends GPNode implements Function{ 
    public abstract String getVariableName();
    
    public abstract int getInputIndex();
    
    @Override
    public String toString() {
        return getVariableName(); 
    }

    @Override
    public int expectedChildren() {
        return 0; 
    }
    
    @Override
    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem){
            RegressionData rd = ((RegressionData)(input));
            rd.x = ((Regression)problem).currentValue[getInputIndex()];    
    }
    
    @Override
    public double eval(double[] x){
        return x[getInputIndex()];
    }

    @Override
    public String print() {
        return getVariableName();
    }
    
    @Override
    public int getNumNodes() {
        return numNodes(GPNode.NODESEARCH_ALL);
    }
}



