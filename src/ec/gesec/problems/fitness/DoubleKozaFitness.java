/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gesec.problems.fitness;

import ec.EvolutionState;
import ec.Fitness;
import static ec.Fitness.FITNESS_PREAMBLE;
import ec.gp.koza.GPKozaDefaults;
import ec.gp.koza.KozaFitness;
import static ec.gp.koza.KozaFitness.P_KOZAFITNESS;
import ec.util.Code;
import ec.util.DecodeReturn;
import ec.util.Parameter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.LineNumberReader;

/**
 *
 * @author luiz
 */
public class DoubleKozaFitness {
//    public static final String P_KOZAFITNESS = "fitness";
//
//    /** This ranges from 0 (best) to infinity (worst).    I
//        define it here as equivalent to the standardized fitness. */
//    protected float standardizedFitness;
//
//    /** This auxillary measure is used in some problems for additional
//        information.  It's a traditional feature of Koza-style GP, and so
//        although I think it's not very useful, I'll leave it in anyway. */
//    public int hits;
//    
//    /** This ranges from 0 (best) to infinity (worst). This is similar
//     * to statardizedFitness, but using a double variable. */
//    protected double doubleFitness;
//
//    public Parameter defaultBase(){
//        return GPKozaDefaults.base().push(P_KOZAFITNESS);
//    }
//
//    /** Set the standardized fitness in the half-open interval [0.0,infinity)
//        which is defined (NOTE: DIFFERENT FROM fitness()!!!) as 0.0 
//        being the IDEAL and infinity being worse than the worst possible.
//        This is the GP tradition.  The fitness() function instead will output
//        the equivalent of Adjusted Fitness.
//    */
//    public final void setStandardizedFitness(final EvolutionState state, final float _f) {
//        if (_f < 0.0f || _f == Float.POSITIVE_INFINITY || Float.isNaN(_f)) {
//            state.output.warning("Bad fitness (may not be < 0, NaN, or infinity): " + _f  + ", setting to 0.");
//            standardizedFitness = 0;
//        }
//        else standardizedFitness = _f;
//    }
//
//    /** Returns the adjusted fitness metric, which recasts the
//        fitness to the half-open interval (0,1], where 1 is ideal and
//        0 is worst.  Same as adjustedFitness().  */
//    public final float fitness() {
////        return 1.0f/(1.0f+standardizedFitness);
//        return standardizedFitness;
//    }
//
//    /** Returns the raw fitness metric.  
//        @deprecated use standardizedFitness()
//    */
//    public final float rawFitness() {
//        return standardizedFitness();
//    }
//
//    /** Returns the standardized fitness metric. */
//
//    public final float standardizedFitness() {
//        return standardizedFitness;
//    }
//
//    /** Returns the adjusted fitness metric, which recasts the fitness
//        to the half-open interval (0,1], where 1 is ideal and 0 is worst.
//        This metric is used when printing the fitness out. */
//
//    public final float adjustedFitness() {
//        return fitness();
//    }
//
//    @Override
//    public void setup(final EvolutionState state, final Parameter base) { }
//    
//    @Override
//    public final boolean isIdealFitness() {
//        return standardizedFitness <= 0.0f;  // should always be == 0.0f, <0.0f is illegal, but just in case...
//
//    }
//    
//    @Override
//    public boolean equivalentTo(final Fitness _fitness) {
//        return _fitness.fitness() == fitness();
//    }
//
//    @Override
//    public boolean betterThan(final Fitness _fitness) {
//        return _fitness.fitness() < fitness();
//    }
// 
//    @Override
//    public String fitnessToString() {
//        return FITNESS_PREAMBLE + Code.encode(standardizedFitness) + Code.encode(hits);
//    }
//        
//    @Override
//    public String fitnessToStringForHumans() {
//        return FITNESS_PREAMBLE + "Standardized=" + standardizedFitness + " Adjusted=" + adjustedFitness() + " Hits=" + hits;
//    }
//            
//    @Override
//    public final void readFitness(final EvolutionState state, 
//                                  final LineNumberReader reader) throws IOException {
//        DecodeReturn d = Code.checkPreamble(FITNESS_PREAMBLE, state, reader);
//        
//        // extract fitness
//        Code.decode(d);
//        if (d.type!=DecodeReturn.T_FLOAT)
//            state.output.fatal("Reading Line " + d.lineNumber + ": " + "Bad Fitness.");
//        standardizedFitness = (float)d.d;
//        
//        // extract hits
//        Code.decode(d);
//        if (d.type!=DecodeReturn.T_INT)
//            state.output.fatal("Reading Line " + d.lineNumber + ": " + "Bad Fitness.");
//        hits = (int)d.l;
//    }
//
//    @Override
//    public void writeFitness(final EvolutionState state, 
//                             final DataOutput dataOutput) throws IOException {
//        dataOutput.writeFloat(standardizedFitness);
//        dataOutput.writeInt(hits);
//        writeTrials(state, dataOutput);
//    }
//
//    @Override
//    public void readFitness(final EvolutionState state,
//                            final DataInput dataInput) throws IOException {
//        standardizedFitness = dataInput.readFloat();
//        hits = dataInput.readInt();
//        readTrials(state, dataInput);
//    }
//
//    @Override
//    public void setToMeanOf(EvolutionState state, Fitness[] fitnesses) {
//        // this is not numerically stable.  Perhaps we should have a numerically stable algorithm for sums
//        // we're presuming it's not a very large number of elements, so it's probably not a big deal,
//        // since this function is meant to be used mostly for gathering trials together.
//        double f = 0;
//        long h = 0;
//        for(int i = 0; i < fitnesses.length; i++) {
//            DoubleKozaFitness fit = (DoubleKozaFitness)(fitnesses[i]);
//            f += fit.standardizedFitness;
//            h += fit.hits;
//        }
//        f /= fitnesses.length;
//        h /= fitnesses.length;
//        standardizedFitness = (float)f;
//        hits = (int)h;
//    }
//    
//    
//
//    public void setDoubleFitness(double doubleFitness) {
//        this.doubleFitness = doubleFitness;
//    }
//
//    public double getDoubleFitness() {
//        return doubleFitness;
//    }
}
