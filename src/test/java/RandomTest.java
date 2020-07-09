import java.util.Random;

public class RandomTest {

    static Random random = new Random();

    private static double gaussianValue(double mean, double std) {
        return random.nextGaussian() * Math.sqrt(Math.pow(std,2)) + mean;
    }

    public static void main(String[] args) {

        for(int i=0;i < 10000; i++) {
            double r = gaussianValue(5.2047, 1.1820);
            System.out.println(r);

        }

    }


}
