/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.ssr.functions;

import ec.*;
import ec.app.regression.*;
import ec.gp.*;

/* 
 * Div.java
 * 
 * Created: Wed Nov  3 18:26:37 1999
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class AQ extends GPNode implements Function{
    public String toString() { 
        return "%"; 
    }
    
    public int expectedChildren() {
        return 2; 
    }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem){
        RegressionData rd = ((RegressionData)(input));

        // evaluate children[1] first to determine if the demoniator is 0
        children[0].eval(state,thread,input,stack,individual,problem);
        double result;
        result = rd.x;

        children[1].eval(state,thread,input,stack,individual,problem);
        rd.x = result / Math.sqrt(1+(rd.x*rd.x));
    }

    @Override
    public double eval(double val[]) {
        // evaluate children[1] first to determine if the demoniator is 0
        double den = ((Function)children[1]).eval(val);        
        return ((Function)children[0]).eval(val) / Math.sqrt(1+(den*den));
    }

    @Override
    public String print() {
        return "AQ(" + ((Function)children[0]).print() + "," + ((Function)children[1]).print() + ")";
    }
    
    @Override
    public int getNumNodes() {
        return numNodes(GPNode.NODESEARCH_ALL);
    }
}



