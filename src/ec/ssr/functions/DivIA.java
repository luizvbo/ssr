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

public class DivIA extends Div implements FunctionIA{
    
    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem){
        
        RegressionData rd = ((RegressionData)(input));
        double result;

        // evaluate children[1] first to determine if the demoniator is 0
        children[1].eval(state,thread,input,stack,individual,problem);
        result = rd.x;
            
        children[0].eval(state,thread,input,stack,individual,problem);
        rd.x = rd.x / result;
    }
    
    @Override
    public double eval(double val[]) {
        double denominator = ((Function)children[1]).eval(val);      
        if(denominator == 0) numZeroDiv++;
        return ((Function)children[0]).eval(val) / denominator;
    }

    @Override
    public Bounds getBounds(Bounds[] bounds) {
        Bounds newBounds = Bounds.createNaNBounds();
        Bounds denominator = ((FunctionIA)children[1]).getBounds(bounds);
        if(denominator.isInsideBounds()){
            if((denominator.lowerBound >= 0 || denominator.upperBound >= 0) && 
               (denominator.lowerBound < 0 || denominator.upperBound < 0)){
                return newBounds;
            }
            else{
                Bounds numerator = ((FunctionIA)children[0]).getBounds(bounds);
                if(numerator.isInsideBounds()){
                    double ll = numerator.lowerBound / denominator.lowerBound;
                    double lu = numerator.lowerBound / denominator.upperBound;
                    double ul = numerator.upperBound / denominator.lowerBound;
                    double uu = numerator.upperBound / denominator.upperBound;
                    return Utils.getBounds(ll, lu, ul, uu);
                }
            }
        }
        return newBounds;
    }
}



