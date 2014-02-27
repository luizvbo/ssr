/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.gesec.functions;

import ec.*;
import ec.app.regression.*;
import ec.gp.*;

/* 
 * Exp.java
 * 
 * Created: Wed Nov  3 18:26:37 1999
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class Exp extends GPNode implements Function{
    public String toString() { 
        return "exp"; 
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
        rd.x = /*Strict*/Math.exp(rd.x);    
    }
    
    @Override
    public double eval(double[] val) {
        double evaluated = ((Function)children[0]).eval(val);
        return Math.exp(evaluated);
    }
    
    @Override
    public String print() {
        return "exp(" + ((Function)children[0]).print() + ")";
    }
}



