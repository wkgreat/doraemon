import org.locationtech.jts.geom.Polygon;
import wk.doraemon.geo.JTSUtils;
import wk.doraemon.geo.geohash.GeoBits;
import wk.doraemon.geo.geohash.GeoHashBoxIndex;

import java.util.Set;

public class GeohashBoxIndexTest {

    public static void main(String[] args) {

        long geobits = GeoBits.geohashbits(0,0,10);
        double[] gs = GeoBits.getGeoBitsBarrier(geobits);
        System.out.println(gs[0]);
        System.out.println(gs[1]);
        System.out.println(gs[2]);
        System.out.println(gs[3]);


        double left=0, right=10, down=0, upper=10;
        GeoHashBoxIndex index = new GeoHashBoxIndex(left,right,down,upper);
        Polygon box = JTSUtils.createBox(left,right,down,upper,4326);
        System.out.println(JTSUtils.geom2wkt(box));
        Set<Long> allGeobits = index.allGeobits;
        allGeobits.forEach(g-> System.out.println(GeoBits.showWKT(g)));

    }

}
