/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.ssr.problems;

import ec.*;
import ec.app.regression.RegressionData;
import ec.gp.*;
import ec.gp.koza.*;
import ec.ssr.core.Dataset;
import ec.ssr.core.Instance;
import java.util.HashMap;


/**
 * The same as Regression, but with random sampling technique (RST)
 * @author Luiz Otavio
 * @version 1.0 
 */
public class RegressionRST2 extends Regression{ 

    protected HashMap<Integer, Double> outputMap;
    
    @Override
    public void evaluate(final EvolutionState state, final Individual ind, final int subpopulation, final int threadnum){
        RegressionData regressionData = (RegressionData)(this.input);
        int hits = 0;
        // Total squared error
        double TSE = 0.0;
        double error;
        double squaredError;
        boolean overflow = false;
        for (int i = 0; i < dataset.size() && !overflow; i++){
            Instance instance = dataset.get(i);
            currentValue = instance.input;
            ((GPIndividual)ind).trees[0].child.eval(
                state, threadnum, regressionData, stack, ((GPIndividual)ind),this);

            // It's possible to get NaN because cos(infinity) and
            // sin(infinity) are undefined (hence cos(exp(3000)) zings ya!)
            // So since NaN is NOT =,<,>,etc. any other number, including
            // NaN, we're CAREFULLY wording our cutoff to include NaN.
            // Interesting that this has never been reported before to
            // my knowledge.
            final double PROBABLY_ZERO = 1.4e-45;
            final double BIG_NUMBER = 3.4e+38;

            error = outputMap.get(instance.id) - regressionData.x;
            squaredError = error * error;

            if (squaredError < PROBABLY_ZERO){  // slightly off
                squaredError = 0.0;
            }
            if(Double.isInfinite(squaredError) || Double.isNaN(squaredError) || squaredError >= BIG_NUMBER){
                overflow = true;
                hits = 0;
            }
            else{
                if (error <= hitLevel) hits++;  // whatever!
                TSE += squaredError;
            }
        }

        // the fitness better be KozaFitness!
        KozaFitness f = ((KozaFitness)ind.fitness);            
        if(overflow)
            f.setStandardizedFitness(state, Float.MAX_VALUE);
        else
            f.setStandardizedFitness(state, (float)Math.sqrt(TSE/dataset.size()));
        f.hits = hits;
        ind.evaluated = true;            
    }

    public void setDataset(Dataset currentFold, HashMap<Integer, Double> outputMap) {
        this.dataset = currentFold;
        this.outputMap = outputMap;
    }
}
