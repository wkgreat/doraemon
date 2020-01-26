import org.locationtech.jts.geom.Polygon;
import wk.doraemon.geo.JTSUtils;

public class JTSUtilsTest {

    public static void main(String[] args) {

        Polygon polygon = JTSUtils.createBox(0,10,0,10,4326);
        String wkt = JTSUtils.geom2wkt(polygon);
        System.out.println(wkt);

    }
}
