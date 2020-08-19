package wk.doraemon.geo;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import wk.doraemon.geo.beans.GeoEdge;
import wk.doraemon.geo.beans.GeoPoint;

import java.io.Serializable;

import static wk.doraemon.geo.Geodesic.*;

/**
 * 地理空间几何计算（距离，角度等）
 * */
public class GeoUtils implements Serializable {

    private final static GeodeticCalculator GEODETIC = new GeodeticCalculator();

    public static class WGS84 implements Serializable {

        /**
         * 两点距离及方位
         * @param lon1 第一个点的经度
         * @param lat1 第一个点的纬度
         * @param lon2 第二个点的经度
         * @param lat2 第二个点的纬度
         *
         * */
        public static GeodeticCurve geodesicInverse(double lon1, double lat1, double lon2, double lat2){
            return GEODETIC.calculateGeodeticCurve(
                    Ellipsoid.WGS84,
                    new GlobalCoordinates(lat1, lon1),
                    new GlobalCoordinates(lat2, lon2));
        }

        /**
         * 两点距离 Haversine公式
         * @param lon1 第一个点的经度
         * @param lat1 第一个点的纬度
         * @param lon2 第二个点的经度
         * @param lat2 第二个点的纬度
         * @return  两个点的Haversine距离
         * */
        public static double getDistance(double lon1, double lat1, double lon2, double lat2) {
            return distanceHaversine(lat1, lon1, lat2, lon2);
        }

        /**
         * 两点距离
         * @param lon1 第一个点的经度
         * @param lat1 第一个点的纬度
         * @param lon2 第二个点的经度
         * @param lat2 第二个点的纬度
         * @param useSpheroid 距离计算方式，是否使用WGS84椭球体
         *                    true 使用WGS84椭球体，更精确
         *                    false Haversine公式，更快速
         * @return 两个点的距离
         * */
        public static double getDistance(double lon1, double lat1, double lon2, double lat2, boolean useSpheroid) {
            if(useSpheroid) return distanceSpheroid(lat1, lon1, lat2, lon2);
            else return distanceHaversine(lat1, lon1, lat2, lon2);
        }

        /**
         * 方位角
         * @param lon1 第一个点的经度
         * @param lat1 第一个点的纬度
         * @param lon2 第二个点的经度
         * @param lat2 第二个点的纬度
         * @return 从第一个点到第二个点的方位角
         * */
        public static double getAzimuth(double lon1, double lat1, double lon2, double lat2) {
            return geodesicInverse(lat1, lon1, lat2, lon2).getAzimuth();
        }

        /**
         * 某点沿某方向移动一定距离的位置
         * @param lat 纬度 degree
         * @param lon 经度 degree
         * @param bearing 方位角 degree
         * @param distance 距离 meter
         * @return [lon, lat]
         * */
        public static double[] moveInDirection(double lon, double lat, double bearing, double distance) {
            return Geodesic.moveInDirection(lon, lat, bearing, distance);
        }

        /**
         * 点到线段的最近点(类似垂足)
         * @param point 点
         * @param edge 线段
         * @param onEdge 是否考虑延长线的情况
         *               true 必须返回线段上的点，所以如果垂足在延长线上，则返回（垂足，线段起点，线段终点）中的最近点
         *               false 直接返回垂足，即使其在线段延长线上
         * */
        public static GeoPoint closestPointToEdge(GeoPoint point, GeoEdge edge, boolean onEdge){
            return toDegree(Geodesic.closestPointToEdge(toRadian(point), toRadian(edge), onEdge));
        }

        /**
         * 点到线段的距离
         * @param point 点
         * @param edge 线段
         * @param onEdge 是否考虑延长线的情况
         *               true 返回点到（垂足，线段起点，线段终点）距离中的最小值
         *               false 直接返回点到垂足的距离，即使其在线段延长线上
         * */
        public static double getDistancePointToEdge(GeoPoint point, GeoEdge edge, boolean onEdge) {
            GeoPoint cp = closestPointToEdge(point, edge, onEdge);
            return getDistance(point.lat,point.lon,cp.lat,cp.lon, false);
        }

        /**
         * 点到线段的距离
         * @param point 点
         * @param edge 线段
         * @param onEdge 是否考虑延长线的情况
         *               true 返回点到（垂足，线段起点，线段终点）距离中的最小值
         *               false 直接返回点到垂足的距离，即使其在线段延长线上
         * @param useSpheroid 距离计算方式，是否使用WGS84椭球体
         *                    true 使用WGS84椭球体，更精确
         *                    false Haversine公式，更快速
         *
         * */
        public static double getDistancePointToEdge(GeoPoint point, GeoEdge edge, boolean onEdge, boolean useSpheroid) {
            GeoPoint cp = closestPointToEdge(point, edge, onEdge);
            return getDistance(point.lat,point.lon,cp.lat,cp.lon, useSpheroid);
        }

        /**
         * 角度平均值
         * @param anglesDeg several angles in degree
         * @return average of angles
         * */
        public static double meanAngle(double... anglesDeg) {
            return Geodesic.meanAngle(anglesDeg);
        }

        /**
         * 角度标准差
         * @param anglesDeg several angles in degree
         * @return standard deviation of angles
         * */
        public static double stddevAngle(double... anglesDeg) {
            return Geodesic.stddevAngle(anglesDeg);
        }


    }
}


