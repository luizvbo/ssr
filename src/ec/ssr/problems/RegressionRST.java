/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.ssr.problems;

import ec.ssr.core.Dataset;
import ec.*;
import ec.app.regression.RegressionData;
import ec.gp.*;
import ec.gp.koza.*;


/**
 * The same as Regression, but with random sampling technique (RST)
 * @author Luiz Otavio
 * @version 1.0 
 */
public class RegressionRST extends Regression{
    private Dataset originalDataset;
    private double[] originalOutput;
    
    
    /**
     * Sets the dataset used during training
     * @param dataset Training data
     */
    public void setDataset(Dataset dataset){
        this.dataset = dataset;
        originalDataset = new Dataset(dataset);
    }

    @Override
    public void setOutput(double[] output) {
        this.output = output;
        originalOutput = output;
    }
    
    /**
     * Select only one instance for population evaluation (with 50% probability).
     * @param mainState Evolution state
     * @param canResample If false, utilizes the whole dataset
     */
    public void resample(EvolutionState mainState, boolean canResample){
        double dice = mainState.random[0].nextDouble();
        if(canResample && dice <= .5){
            dataset = new Dataset();
            int index = mainState.random[0].nextInt(originalDataset.size());
            dataset.add(originalDataset.get(index));
            output = new double[1];
            output[0] = originalOutput[index];
        }
        else{
            dataset = originalDataset.softClone();
            output = originalOutput;
        }
    }
    

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
            currentValue = dataset.get(i).input;
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

            error = output[i] - regressionData.x;
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
}
