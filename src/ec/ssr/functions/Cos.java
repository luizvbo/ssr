/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.ssr.functions;

import ec.*;
import ec.app.regression.RegressionData;
import ec.gp.*;
import ec.ssr.core.Bounds;

/* 
 * Cos.java
 * 
 * Created: Wed Nov  3 18:26:37 1999
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class Cos extends GPNode implements FunctionIA{
    public String toString() {
        return "cos"; 
    }
    public int expectedChildren() { 
        return 1; 
    }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem){
        RegressionData rd = ((RegressionData)(input));

        children[0].eval(state,thread,input,stack,individual,problem);
        rd.x = /*Strict*/Math.cos(rd.x);
    }
    
    @Override
    public double eval(double[] val) {
        double evaluated = ((Function)children[0]).eval(val);
        return Math.cos(evaluated);
    }
    
    @Override
    public String print() {
        return "cos(" + ((Function)children[0]).print() + ")";
    }
    
    @Override
    public int getNumNodes() {
        return numNodes(GPNode.NODESEARCH_ALL);
    }

    @Override
    public Bounds getBounds(Bounds[] bounds) {
        Bounds newBounds = new Bounds(-1, 1);
        Bounds inputBounds = ((FunctionIA)children[0]).getBounds(bounds);
        if(inputBounds.isInsideBounds()){
            if(Math.abs(inputBounds.lowerBound - inputBounds.upperBound) < 2*Math.PI ){
                double lb = Math.cos(inputBounds.lowerBound);
                double ub = Math.cos(inputBounds.upperBound);
                if(lb > ub)
                    newBounds.setBounds(ub, lb);
                else
                    newBounds.setBounds(lb, ub);
            }
        }
        return newBounds;
    }
}



