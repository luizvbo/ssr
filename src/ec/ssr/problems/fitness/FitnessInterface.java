/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.problems.fitness;

import ec.EvolutionState;

/**
 *
 * @author luiz
 */
public interface FitnessInterface {
    public float getError();
    
    public void setError(EvolutionState state, float error);
    
    public boolean isPerfectFitness(int numHits);
}
