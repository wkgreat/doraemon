import junit.framework.TestCase;
import org.junit.Test;
import wk.doraemon.geo.geohash.GeoBits;

import java.util.Set;

public class GeobitsTest extends TestCase {

    @Test
    public void test() {

        long geobits = GeoBits.geohashbits(117,32,30);
        System.out.println(geobits);
        System.out.println(Long.toBinaryString(geobits));
        System.out.println(GeoBits.getGeoBitsLength(geobits));

        Set<Long> geobitss = GeoBits.expandByRadius(geobits,1000);
        System.out.println(geobitss);

        for (Long b : geobitss) {
            System.out.println(GeoBits.showWKT(b));
        }

    }

}
