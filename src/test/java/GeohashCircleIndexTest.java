import wk.doraemon.geo.GeoUtils;
import wk.doraemon.geo.geohash.GeoHashCircleIndex;

public class GeohashCircleIndexTest {

    public static void main(String[] args) {

        double lon1 = 0, lat1 = 0;
        double lon2 = 0.0006, lat2 = 0.0005;

        GeoHashCircleIndex index = GeoHashCircleIndex.build(lon1,lat1,1000, 30);
        GeoHashCircleIndex index2 = GeoHashCircleIndex.build(lon2,lat2,1000,30);
        double d = GeoUtils.WGS84.getDistance(lat1, lon1, lat2, lon2, true);
        System.out.println(d);
        System.out.println(index2==index);

    }

}
