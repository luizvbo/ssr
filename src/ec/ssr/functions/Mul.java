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
 * Mul.java
 * 
 * Created: Wed Nov  3 18:26:37 1999
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class Mul extends GPNode implements Function{
    private static final int EXPECTED_CHILDREN = 2;
    
    public String toString() { 
        return "*"; 
    }
    
    public static Mul newMultiplication(GPNode t1, GPNode t2) {
        Mul mul = new Mul();
        mul.children = new GPNode[EXPECTED_CHILDREN];
        mul.children[0] = t1;
        mul.children[0].argposition = (byte)0;
        mul.children[0].parent = mul;
        
        mul.children[1] = t2;
        mul.children[1].argposition = (byte)1;
        mul.children[1].parent = mul;
        
        return mul;
    }

/*
  public void checkConstraints(final EvolutionState state,
  final int tree,
  final GPIndividual typicalIndividual,
  final Parameter individualBase)
  {
  super.checkConstraints(state,tree,typicalIndividual,individualBase);
  if (children.length!=2)
  state.output.error("Incorrect number of children for node " + 
  toStringForError() + " at " +
  individualBase);
  }
*/
    public int expectedChildren() { return 2; }

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

        // can't shortcut because of NaN or +-Infinity

        children[1].eval(state,thread,input,stack,individual,problem);
        rd.x = result * rd.x;
    }
    
    @Override
    public double eval(double[] val) {
        return ((Function)children[0]).eval(val) * ((Function)children[1]).eval(val);
    }
    
    @Override
    public String print() {
        return "(" + ((Function)children[0]).print() + "*" + ((Function)children[1]).print() + ")";
    }
}



