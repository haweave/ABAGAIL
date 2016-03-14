package shared;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
/**
 * A fixed iteration trainer
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class FixedIterationTrainer implements Trainer {
    
    /**
     * The inner trainer
     */
    private Trainer trainer;
    
    /**
     * The number of iterations to train
     */
    private int iterations;
    
    /**
     * Make a new fixed iterations trainer
     * @param t the trainer
     * @param iter the number of iterations
     */
    public FixedIterationTrainer(Trainer t, int iter) {
        trainer = t;
        iterations = iter;
    }

    /**
     * @see shared.Trainer#train()
     */
    public double train() {

        double sum = 0;
        double value = 0;
        long time = System.currentTimeMillis();
        long startTime = time;
        Date date=new Date(time);

        try {
            String sFileName = "/home/harry/gtech/ml/hw2/csvs/" + startTime + ".csv";
            FileWriter writer = new FileWriter(sFileName);
            for (int i = 0; i < iterations; i++) {
                value = trainer.train();
                time = System.currentTimeMillis();
                sum += value;

                writer.append(Integer.toString(i));
                writer.append(',');
                writer.append(Double.toString(value));
                writer.append(',');
                writer.append(Long.toString(time-startTime));
                writer.append('\n');

            }

            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return sum / iterations;
    }
    

}
