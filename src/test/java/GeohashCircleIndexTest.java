import wk.doraemon.geo.GeoUtils;
import wk.doraemon.geo.geohash.GeoHashCircleIndex;

public class GeohashCircleIndexTest {

    public static void main(String[] args) {

        double lon1 = 0, lat1 = 0;
        double lon2 = 0.001, lat2 = 1.00899;

        GeoHashCircleIndex index = new GeoHashCircleIndex(lon1,lat1,1000);
        GeoHashCircleIndex index2 = new GeoHashCircleIndex(lon2,lat2,1000);
        double d = GeoUtils.WGS84.getDistance(lat1, lon1, lat2, lon2, true);
        System.out.println(d);
        System.out.println(index2.contains(index));

    }

}
