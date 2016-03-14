package opt.test;

import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;
import dist.MixtureDistribution;
import opt.*;
import opt.example.FourPeaksEvaluationFunction;
import opt.ga.*;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

import java.util.Arrays;
import java.util.Random;
import opt.EvaluationFunction;
import opt.ga.BitMatchesFitnessFunction;
import java.lang.Math;

/**
 * Created by harry on 3/1/16.
 */
public class BitMatchesTest {
    /** The n value */
    private static final int N = 1000;


    public static void main(String[] args) {
        int [] ranges = new int[N];
        Arrays.fill(ranges, 2);

        EvaluationFunction ef = new BitMatchesFitnessFunction(N);
        Distribution odd = new DiscreteUniformDistribution(ranges);

        NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        MutationFunction mf = new DiscreteChangeOneMutation(ranges);
        CrossoverFunction cf = new SingleCrossOver();
        Distribution df = new DiscreteDependencyTree(.1, ranges);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);

        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);
        FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 25000);
        fit.train();
        System.out.println(Math.pow(ef.value(rhc.getOptimal()),.25));


        SimulatedAnnealing sa = new SimulatedAnnealing(1E11, .95, hcp);
        fit = new FixedIterationTrainer(sa, 25000);
        fit.train();
        System.out.println(Math.pow(ef.value(sa.getOptimal()),.25));

        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(10000, 6000, 50, gap);
        fit = new FixedIterationTrainer(ga, 25000);
        fit.train();
        System.out.println(Math.pow(ef.value(ga.getOptimal()),.25));
//
//        MIMIC mimic = new MIMIC(200, 5, pop);
//        fit = new FixedIterationTrainer(mimic, 25000);
//        fit.train();
//        System.out.println(Math.pow(ef.value(mimic.getOptimal()),.25));

    }

    public static int[] randomList(int bitLength) {
        Random rnd = new Random();
        rnd.setSeed(42);

        int[] randomBits = new int[bitLength];

        for(int x=0;x<randomBits.length;x++){
            randomBits[x]=rnd.nextInt(2);
        }
        return randomBits;
    }
}
