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

public class Div extends GPNode implements Function{
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
        children[1].eval(state,thread,input,stack,individual,problem);
        if (rd.x == 0.0){ 
            // the answer is 1.0 since the denominator was 0.0
            rd.x = 1.0;
        }
        else{
            double result;
            result = rd.x;
            
            children[0].eval(state,thread,input,stack,individual,problem);
            rd.x = rd.x / result;
        }
    }

    @Override
    public double eval(double val[]) {
        // evaluate children[1] first to determine if the demoniator is 0
        double denominator = ((Function)children[1]).eval(val);        
        if(denominator == 0.0){
            // the answer is 1.0 since the denominator was 0.0
            return 1.0;
        }
        else{
            return ((Function)children[0]).eval(val) / denominator;
        }
    }

    @Override
    public String print() {
        return "div(" + ((Function)children[0]).print() + "," + ((Function)children[1]).print() + ")";
    }
    
    @Override
    public int getNumNodes() {
        return numNodes(GPNode.NODESEARCH_ALL);
    }
}



