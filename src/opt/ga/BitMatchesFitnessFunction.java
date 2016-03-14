package opt.ga;

import opt.EvaluationFunction;
import util.linalg.Vector;
import shared.Instance;

import java.util.Random;

/**
 * Created by harry on 3/1/16.
 */
public class BitMatchesFitnessFunction  implements EvaluationFunction {

    private int[] targetArray = randomList(10000);

    public BitMatchesFitnessFunction(int listSize){
        targetArray=randomList(listSize);
    }


    public double value(Instance d) {
        Vector data = d.getData();
        int i = 0;
        int count = 0;
        while(i < data.size())
        {
            if(data.get(i)==targetArray[i])
            {
                count++;
            }
            i++;
        }
        //System.out.println(count);

        return Math.pow(count,4);
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
