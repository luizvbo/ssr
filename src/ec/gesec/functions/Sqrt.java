/*
  Copyright 2012 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.gesec.functions;

import ec.*;
import ec.app.regression.*;
import ec.gp.*;

/* 
 * Sqrt.java
 * 
 * Created: Mon Apr 23 17:15:35 EDT 2012
 * By: Sean Luke

 <p>This function appears in the Korns and Keijzer function sets, and is sqrt(x)
 <p>M. F. Korns. Accuracy in Symbolic Regression. In <i>Proc. GPTP.</i> 2011.
 <p>M. Keijzer. Improving Symbolic Regression with Interval Arithmetic and Linear Scaling. In <i>Proc. EuroGP.</i> 2003.

*/

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class Sqrt extends GPNode implements Function{
    public String toString() { 
        return "sqrt"; 
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
        if (rd.x < 0){ 
            rd.x = 1;
        }
        else rd.x = Math.sqrt(rd.x);    
    }

    @Override
    public double eval(double[] val) {
        double evaluated = ((Function)children[0]).eval(val);
        if(evaluated < 0){
            return 1;
        }
        return Math.sqrt(evaluated);   
    }

    @Override
    public String print() {
        return "sqrt(" + ((Function)children[0]).print() + ")";
    }
}



