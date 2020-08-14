
import junit.framework.TestCase;
import org.junit.Test;
import wk.doraemon.geo.geohash.GeoBits;
import wk.doraemon.geo.geohash.GeoHash;

public class GeohashTest extends TestCase {

    @Test
    public static void test1() {
        double lon = 0, lat = 89.9;
        String g = GeoHash.getGeoHash(lon, lat, 2);
        System.out.println(g);
        double[] b = GeoBits.getGeoBitsBarrier(GeoBits.geohashToBits(g));
        System.out.println(b[0]+","+b[1]+","+b[2]+","+b[3]);
        String north = GeoHash.north(g);
        System.out.println(north);
        double[] b2 = GeoBits.getGeoBitsBarrier(GeoBits.geohashToBits(north));
        System.out.println(b2[0]+","+b2[1]+","+b2[2]+","+b2[3]);
    }
}
