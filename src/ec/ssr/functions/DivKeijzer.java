/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.ssr.functions;

import ec.*;
import ec.app.regression.*;
import ec.gp.*;
import ec.ssr.core.Bounds;
import ec.ssr.core.Utils;

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

public class DivKeijzer extends GPNode implements FunctionIA{
    public int expectedChildren() {
        return 1; 
    }
    
    @Override
    public String print() {
        return "div(1," + ((Function)children[0]).print() + ")";
    }
    
    @Override
    public int getNumNodes() {
        return numNodes(GPNode.NODESEARCH_ALL);
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
        rd.x = 1 / rd.x;
    }
    
    @Override
    public double eval(double val[]) {
        return 1 / ((Function)children[0]).eval(val);
    }

    @Override
    public Bounds getBounds(Bounds[] bounds) {
        Bounds newBounds = Bounds.createNaNBounds();
        Bounds denominator = ((FunctionIA)children[0]).getBounds(bounds);
        if(denominator.isInsideBounds()){
            if((denominator.lowerBound >= 0 || denominator.upperBound >= 0) && 
               (denominator.lowerBound < 0 || denominator.upperBound < 0)){
                return newBounds;
            }
            else{
                double l = 1 / denominator.lowerBound;
                double u = 1 / denominator.upperBound;
                if(u > l)
                    newBounds.setBounds(l, u);
                else
                    newBounds.setBounds(u, l);
            }
        }
        return newBounds;
    }

    @Override
    public String toString() {
        return "%"; 
    }
}



