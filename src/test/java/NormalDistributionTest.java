import org.apache.commons.math3.distribution.NormalDistribution;
import wk.doraemon.io.TextReader;
import wk.doraemon.io.TextWriter;

import java.util.ArrayList;
import java.util.List;

public class NormalDistributionTest {

    private final static NormalDistribution normal = new NormalDistribution(0,1);

    public static double normp(double thres, double mean, double std) {
        return normal.cumulativeProbability((thres-mean)/std);
    }

    public static void main(String[] args) {


        TextReader reader = new TextReader("/Users/wkgreat/codes/amh/share/allinfo.csv").init();
        reader.readlines();
        List<List<String>> data = reader.getRecords("\t");
        List<Double[]> ns = new ArrayList<>();
        for(List<String> r : data) {
            //System.out.println(r.get(9)+","+r.get(10));
            if(r.get(9).equals("NULL") || r.get(10).equals("NULL")) continue;
            ns.add(new Double[]{Double.parseDouble(r.get(9)),Double.parseDouble(r.get(10)),0.0});
        }
        double sum = 0.0;
        int count = 0;
        for(Double[] n : ns) {
            sum+=n[0];
            count++;
        }
        double avg = sum / count;

        TextWriter writer = new TextWriter("/Users/wkgreat/codes/amh/share/allinfo_price.csv",false).init();
        writer.writeLine("amt,std,p");
        for(Double[] n : ns) {
            double p = normp(avg,n[0],n[1]);
            n[2] = p;
            //System.out.println(n[0]+","+n[1]+","+n[2]);
            if (p>1) {
                System.out.println(p);
                System.out.println(n[0]+","+n[1]+","+n[2]+","+avg);
            }
            writer.writeLine(n[0]+","+n[1]+","+n[2]);
        }

        writer.close();
        reader.close();


    }

}
