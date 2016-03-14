package opt.test;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import dist.*;
import opt.*;
import opt.example.*;
import opt.ga.*;
import shared.*;
import func.nn.backprop.*;

import java.util.*;
import java.io.*;
import java.text.*;

/**
 * Implementation of randomized hill climbing, simulated annealing, and genetic algorithm to
 * find optimal weights to a neural network that is classifying abalone as having either fewer 
 * or more than 15 rings. 
 *
 * @author Hannah Lau
 * @version 1.0
 */
public class AbaloneTest {
    private static Instance[] instances = initializeInstances();

    private static int inputLayer = 27, hiddenLayer = 4, outputLayer = 1, trainingIterations = 1;
    private static double trainSize = .8;

    private static int countIters = 0;
    private static BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();
    
    private static ErrorMeasure measure = new SumOfSquaresError();

    private static DataSet set = new DataSet(instances);

    private static BackPropagationNetwork networks[] = new BackPropagationNetwork[3];
    private static NeuralNetworkOptimizationProblem[] nnop = new NeuralNetworkOptimizationProblem[3];

    private static OptimizationAlgorithm[] oa = new OptimizationAlgorithm[3];
    private static String[] oaNames = {"RHC", "SA", "GA"};
    private static String results = "";

    private static DecimalFormat df = new DecimalFormat("0.000");

    public static void main(String[] args) {
        for(int i = 0; i < oa.length; i++) {
            networks[i] = factory.createClassificationNetwork(
                new int[] {inputLayer, hiddenLayer, outputLayer});
            nnop[i] = new NeuralNetworkOptimizationProblem(set, networks[i], measure);
        }

        oa[0] = new RandomizedHillClimbing(nnop[0]);
        oa[1] = new SimulatedAnnealing(1E11, .95, nnop[1]);
        oa[2] = new StandardGeneticAlgorithm(200, 100, 10, nnop[2]);

        for(int i = 0; i < oa.length; i++) {
            double start = System.nanoTime(), end, trainingTime, testingTime, correct = 0, incorrect = 0;
            train(oa[i], networks[i], oaNames[i]); //trainer.train();
            end = System.nanoTime();
            trainingTime = end - start;
            trainingTime /= Math.pow(10,9);

            Instance optimalInstance = oa[i].getOptimal();
            networks[i].setWeights(optimalInstance.getData());

            double predicted, actual;
            start = System.nanoTime();
            int testStart = (int)Math.round(instances.length*trainSize);
            for(int j = testStart; j < instances.length; j++) {

                networks[i].setInputValues(instances[j].getData());
                networks[i].run();

                predicted = Double.parseDouble(instances[j].getLabel().toString());
                actual = Double.parseDouble(networks[i].getOutputValues().toString());


                double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;
            }
            end = System.nanoTime();
            testingTime = end - start;
            testingTime /= Math.pow(10,9);

            results +=  "\nResults for " + oaNames[i] + ": \nCorrectly classified " + correct + " instances." +
                        "\nIncorrectly classified " + incorrect + " instances.\nPercent correctly classified: "
                        + df.format(correct/(correct+incorrect)*100) + "%\nTraining time: " + df.format(trainingTime)
                        + " seconds\nTesting time: " + df.format(testingTime) + " seconds\n";
        }

        System.out.println(results);
    }

    private static void train(OptimizationAlgorithm oa, BackPropagationNetwork network, String oaName) {
        System.out.println("\nError results for " + oaName + "\n---------------------------");
        int testStart = (int)Math.round(instances.length*trainSize);
        int j=1;
        double predicted, actual,correct = 0, incorrect = 0;

        try {
            long time = System.currentTimeMillis();
            String sFileName = "/home/harry/gtech/ml/hw2/csvs/" + time + ".csv";
            FileWriter writer = new FileWriter(sFileName);

            if(oaName == "GA"){
                trainingIterations = 1000;
            }

            for (int i = 0; i < trainingIterations; i++) {
                System.out.println(i);
                oa.train();
                writer.append(Integer.toString(i+1));
                writer.append(',');

                double error = 0;
                double error2 = 0;


//                for (j = 0; j < instances.length * trainSize; j++) {
//                    network.setInputValues(instances[j].getData());
//                    network.run();
//
//                    Instance output = instances[j].getLabel(), example = new Instance(network.getOutputValues());
//                    example.setLabel(new Instance(Double.parseDouble(network.getOutputValues().toString())));
//                    error += measure.value(output, example);
//                }

                System.out.println(df.format(error / j));

                writer.append(Double.toString(error / j));
                writer.append(',');

                correct = 0;
                incorrect = 0;

                for (j = testStart; j < instances.length; j++) {

                    network.setInputValues(instances[j].getData());
                    network.run();

                    predicted = Double.parseDouble(instances[j].getLabel().toString());
                    actual = Double.parseDouble(network.getOutputValues().toString());

                    Instance output = instances[j].getLabel(), example = new Instance(network.getOutputValues());
                    example.setLabel(new Instance(Double.parseDouble(network.getOutputValues().toString())));
                    error2 += measure.value(output, example);

                    double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;
                }

                System.out.println(error2 / (j - testStart));
                writer.append(Double.toString(error2 / (j - testStart)));
                writer.append('\n');
            }

            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private static Instance[] initializeInstances() {

        double[][][] attributes = new double[10514][][];

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("src/opt/test/attributes.csv")));

            for(int i = 0; i < attributes.length; i++) {
                Scanner scan = new Scanner(br.readLine());
                scan.useDelimiter(",");

                attributes[i] = new double[2][];
                attributes[i][0] = new double[27]; // 7 attributes
                attributes[i][1] = new double[1];

                for(int j = 0; j < 27; j++)
                    attributes[i][0][j] = Double.parseDouble(scan.next());

                attributes[i][1][0] = Double.parseDouble(scan.next());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        Instance[] instances = new Instance[attributes.length];

        for(int i = 0; i < instances.length; i++) {
            instances[i] = new Instance(attributes[i][0]);
            // classifications range from 0 to 30; split into 0 - 14 and 15 - 30
            instances[i].setLabel(new Instance(attributes[i][1][0] ==0  ? 0 : 1));
        }

        return instances;
    }
}
