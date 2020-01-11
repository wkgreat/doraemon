package wk.doraemon.geo;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;

import java.io.Serializable;

public class GeoUtils implements Serializable {

    private final static GeodeticCalculator GEODETIC = new GeodeticCalculator();

    public static class WGS84 implements Serializable {

        public static GeodeticCurve geodesicInverse(double lat1, double lon1, double lat2, double lon2){
            return GEODETIC.calculateGeodeticCurve(
                    Ellipsoid.WGS84,
                    new GlobalCoordinates(lat1, lon1),
                    new GlobalCoordinates(lat2, lon2));
        }

        public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
            return distanceHaversine(lat1, lon1, lat2, lon2);
        }

        public static double getDistance(double lat1, double lon1, double lat2, double lon2, boolean useSpheroid) {
            if(useSpheroid) return distanceSpheroid(lat1, lon1, lat2, lon2);
            else return distanceHaversine(lat1, lon1, lat2, lon2);
        }

        public static double getAzimuth(double lat1, double lon1, double lat2, double lon2) {
            return geodesicInverse(lat1, lon1, lat2, lon2).getAzimuth();
        }

        /**
         * 使用Haversine公式计算距离
         * */
        public static double distanceHaversine(double lat1, double lon1, double lat2, double lon2) {
            int earthR = 6371000;
            double x = Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.cos((lon1 - lon2) * Math.PI / 180);
            double y = Math.sin(lat1 * Math.PI / 180) * Math.sin(lat2 * Math.PI / 180);
            double s = x + y;
            if (s > 1) s = 1;
            if (s < -1) s = -1;
            double alpha = Math.acos(s);
            return alpha * earthR;
        }

        /**
         * @FIXEME 引用了org.gavaghan.geodesy库，但是里面封装了好多对象，可能会提高内存使用量
         * */
        public static double distanceSpheroid(double lat1, double lon1, double lat2, double lon2) {
            return geodesicInverse(lat1, lon1, lat2, lon2).getEllipsoidalDistance();
        }

    }

    public static void main(String[] args) {
        double lat1 = 32.955582;
        double lon1 = 117.339232;
        double lat2 = 32.927565;
        double lon2 = 117.347378;
    }

}


