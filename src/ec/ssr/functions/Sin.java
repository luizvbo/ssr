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
 * Sin.java
 * 
 * Created: Wed Nov  3 18:26:37 1999
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class Sin extends GPNode implements Function{
    public String toString() { 
        return "sin"; 
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
        rd.x = /*Strict*/Math.sin(rd.x);   
    }    
    
    @Override
    public double eval(double[] val) {
        double evaluated = ((Function)children[0]).eval(val);
        return Math.sin(evaluated);
    }
    
    @Override
    public String print() {
        return "sin(" + ((Function)children[0]).print() + ")";
    }
}



