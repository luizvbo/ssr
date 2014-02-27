/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.gesec.functions;

import ec.EvolutionState;
import ec.Problem;
import ec.app.regression.RegressionData;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;


/* 
 * Add.java
 * 
 * Created: Wed Nov  3 18:26:37 1999
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class Add extends GPNode implements Function{
    
    public String toString() {
        return "+"; 
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
        
        double result;
        RegressionData rd = ((RegressionData)(input));

        children[0].eval(state,thread,input,stack,individual,problem);
        result = rd.x;

        children[1].eval(state,thread,input,stack,individual,problem);
        rd.x = result + rd.x;
    }

    @Override
    public double eval(double[] val) {
        return ((Function)children[0]).eval(val) + ((Function)children[1]).eval(val);
    }

    @Override
    public String print() {
        return "(" + ((Function)children[0]).print() + "+" + ((Function)children[1]).print() + ")";
    }
}



