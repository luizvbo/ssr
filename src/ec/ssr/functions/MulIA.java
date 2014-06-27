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
 * Mul.java
 * 
 * Created: Wed Nov  3 18:26:37 1999
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class MulIA extends Mul implements FunctionIA{
    
    public void eval(final EvolutionState state,
                     final int thread, 
                     final GPData input,
                     final ADFStack stack, 
                     final GPIndividual individual, 
                     final Problem problem){
        
        RegressionData rd = ((RegressionData)(input));
        
        double result;
        children[0].eval(state,thread,input,stack,individual,problem);
        result = rd.x;
        
        // can't shortcut because of NaN or +-Infinity
        children[1].eval(state,thread,input,stack,individual,problem);
        rd.x = result * rd.x;
    }
    
    @Override
    public Bounds getBounds(Bounds[] bounds) {
        Bounds newBounds = Bounds.createNaNBounds();
        Bounds firstTerm = ((FunctionIA)children[0]).getBounds(bounds);
        if(firstTerm.isInsideBounds()){
            Bounds secondTerm = ((FunctionIA)children[1]).getBounds(bounds);
            if(secondTerm.isInsideBounds()){
                double ll = firstTerm.lowerBound * secondTerm.lowerBound;
                double lu = firstTerm.lowerBound * secondTerm.upperBound;
                double ul = firstTerm.upperBound * secondTerm.lowerBound;
                double uu = firstTerm.upperBound * secondTerm.upperBound;
                return Utils.getBounds(ll, lu, ul, uu);
            }
        }
        return newBounds;
    }
}



