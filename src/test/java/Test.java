import junit.framework.TestCase;
import org.geotools.referencing.GeodeticCalculator;
import org.locationtech.jts.geom.Point;
import wk.doraemon.geo.JTSUtils;

import java.text.ParseException;

/**
 * @author Ke Wang
 * @since 2020/9/7
 */
public class Test extends TestCase {

    public void test1() throws ParseException {

        GeodeticCalculator gc = new GeodeticCalculator();
        gc.getOrthodromicDistance();

        Point p = JTSUtils.getPoint(0,0,4326);


    }

}
