import junit.framework.TestCase;
import wk.doraemon.geo.Geodesic;

import java.text.ParseException;

/**
 * @author Ke Wang
 * @since 2020/9/7
 */
public class Test extends TestCase {

    public void test1() throws ParseException {

        double d = Geodesic.distanceSpheroid(32,117,32,117.01);
        System.out.println(d);

        long a = 0;
        long a1 = 1L<<62;
        System.out.println(Long.toBinaryString(Long.MAX_VALUE));
        System.out.println(Long.toBinaryString(a1));

    }

}
