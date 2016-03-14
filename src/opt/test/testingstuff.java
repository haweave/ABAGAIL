package opt.test;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by harry on 3/1/16.
 */
public class testingstuff {
    public static void main(String[] args) {
        int N = 60;
        int[] ranges = new int[N];
        Arrays.fill(ranges, 2);
        ranges = randomList(20);
        for(int x = 0;x<ranges.length;x++){
            System.out.print(ranges[x]);
        }
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
