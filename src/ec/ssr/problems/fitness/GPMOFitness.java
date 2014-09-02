/*
  Copyright 2010 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.ssr.problems.fitness;

import ec.EvolutionState;
import ec.multiobjective.nsga2.NSGA2MultiObjectiveFitness;
import ec.util.Parameter;

/* 
 * NSGA2MultiObjectiveFitness.java
 * 
 * Created: Thu Feb 04 2010
 * By: Faisal Abidi and Sean Luke
 */

/**
 * NSGA2MultiObjectiveFitness is a subclass of MultiObjeciveFitness which
 * adds auxiliary fitness measures (sparsity, rank) largely used by MultiObjectiveStatistics.
 * It also redefines the comparison measures to compare based on rank, and break ties
 * based on sparsity. 
 *
 */

public class GPMOFitness extends NSGA2MultiObjectiveFitness implements FitnessInterface{
    public void setup(EvolutionState state, Parameter base){
        int numFitnesses;

        numFitnesses = 2;

        objectives = new float[numFitnesses];
        maxObjective = new float[numFitnesses];
        minObjective = new float[numFitnesses];
        maximize = new boolean[numFitnesses];
                
        minObjective[0] = minObjective[1] = 0;
        maxObjective[0] = maxObjective[1] = 1;
        maximize[0] = maximize[1] = false;
        
        state.output.exitIfErrors();
    }

    @Override
    public float getError() {
        return objectives[0];
    }

    @Override
    public void setError(EvolutionState state, float error) {
        // Normalize the error to [0,1]
        error = 1/(1+error);
        error = 1 - error;
        objectives[0] = (float)error;
    }
    
    public void setSize(int size){
        // Normalize the size
        float sizeNorm = (float)(1.0 / size);
        objectives[1] = 1-sizeNorm;
    }
    
    public int getSize(){
        return (int)objectives[1];
    }

    /**
     * Override isPerfectFitness from the interface
     * @param numHits It has no use. It was added only for compatibility.
     * @return True if the ideal fitness is reached
     */
    @Override
    public boolean isPerfectFitness(int numHits) {
        return super.isIdealFitness();
    }
}
