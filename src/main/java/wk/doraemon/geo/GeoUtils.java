package wk.doraemon.geo;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import wk.doraemon.geo.beans.GeoEdge;
import wk.doraemon.geo.beans.GeoPoint;

import java.io.Serializable;

import static wk.doraemon.geo.Geodesic.*;

public class GeoUtils implements Serializable {

    private final static GeodeticCalculator GEODETIC = new GeodeticCalculator();

    public static class WGS84 implements Serializable {

        /**
         * 两点距离及方位
         * */
        public static GeodeticCurve geodesicInverse(double lat1, double lon1, double lat2, double lon2){
            return GEODETIC.calculateGeodeticCurve(
                    Ellipsoid.WGS84,
                    new GlobalCoordinates(lat1, lon1),
                    new GlobalCoordinates(lat2, lon2));
        }

        /**
         * 两点距离 Haversine公式
         * */
        public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
            return distanceHaversine(lat1, lon1, lat2, lon2);
        }

        /**
         * 两点距离
         * @param useSpheroid 距离计算方式，是否使用WGS84椭球体
         *                    true 使用WGS84椭球体，更精确
         *                    false Haversine公式，更快速
         * */
        public static double getDistance(double lat1, double lon1, double lat2, double lon2, boolean useSpheroid) {
            if(useSpheroid) return distanceSpheroid(lat1, lon1, lat2, lon2);
            else return distanceHaversine(lat1, lon1, lat2, lon2);
        }

        /**
         * 方位角
         * */
        public static double getAzimuth(double lat1, double lon1, double lat2, double lon2) {
            return geodesicInverse(lat1, lon1, lat2, lon2).getAzimuth();
        }

        /**
         * 点到线段的最近点(类似垂足)
         * @param onEdge 是否考虑延长线的情况
         *               true 必须返回线段上的点，所以如果垂足在延长线上，则返回（垂足，线段起点，线段终点）中的最近点
         *               false 直接返回垂足，即使其在线段延长线上
         * */
        public static GeoPoint closestPointToEdge(GeoPoint p, GeoEdge e, boolean onEdge){
            return toDegree(Geodesic.closestPointToEdge(toRadian(p), toRadian(e), onEdge));
        }

        /**
         * 点到线段的距离
         * @param onEdge 是否考虑延长线的情况
         *               true 返回点到（垂足，线段起点，线段终点）距离中的最小值
         *               false 直接返回点到垂足的距离，即使其在线段延长线上
         * */
        public static double getDistancePointToEdge(GeoPoint p, GeoEdge e, boolean onEdge) {
            GeoPoint cp = closestPointToEdge(p, e, onEdge);
            return getDistance(p.lat,p.lon,cp.lat,cp.lon, false);
        }

        /**
         * 点到线段的距离
         * @param onEdge 是否考虑延长线的情况
         *               true 返回点到（垂足，线段起点，线段终点）距离中的最小值
         *               false 直接返回点到垂足的距离，即使其在线段延长线上
         * @param useSpheroid 距离计算方式，是否使用WGS84椭球体
         *                    true 使用WGS84椭球体，更精确
         *                    false Haversine公式，更快速
         *
         * */
        public static double getDistancePointToEdge(GeoPoint p, GeoEdge e, boolean onEdge, boolean useSpheroid) {
            GeoPoint cp = closestPointToEdge(p, e, onEdge);
            return getDistance(p.lat,p.lon,cp.lat,cp.lon, useSpheroid);
        }

        /**
         * 角度平均值
         * */
        static double meanAngle(double... anglesDeg) {
            return Geodesic.meanAngle(anglesDeg);
        }

        /**
         * 角度标准差
         * */
        static double stddevAngle(double... anglesDeg) {
            return Geodesic.stddevAngle(anglesDeg);
        }


    }

    public static void main(String[] args) {
        double lat1 = 36.0913;
        double lon1 = 115.131545;
        double lat2 = 36.20613;
        double lon2 = 115.143785;
        double d1 = GeoUtils.WGS84.getDistance(lat1,lon1, lat2,lon2,true);
        double d2 = GeoUtils.WGS84.getDistance(lat1,lon1, lat2,lon2,false);
        System.out.println(d1-d2);

//        GeoPoint start = new GeoPoint(115.845360, 33.256987);
//        GeoPoint end = new GeoPoint(116.074433, 33.290955);
//        GeoPoint p = new GeoPoint(116.301559, 33.160992);
//        GeoPoint k = GeoUtils.WGS84.closestPointToEdge(p,new GeoEdge(start,end),true);
//        System.out.println(k.lon+","+k.lat);

    }

}


