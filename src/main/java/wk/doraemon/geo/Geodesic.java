package wk.doraemon.geo;

import ch.hsr.geohash.WGS84Point;
import wk.doraemon.geo.beans.GeoEdge;
import wk.doraemon.geo.beans.GeoPoint;
import wk.doraemon.geo.beans.Point;

import static java.lang.Math.*;
import static wk.doraemon.geo.GeoUtils.WGS84.geodesicInverse;

/**
 * 借鉴自PostGIS源码 lwgeodetic.c
 * WGS84地理坐标系下，各种几何计算
 * @author wangke
 * */
public class Geodesic {

    public static final double degToRad = 0.0174532925199433;

    /**
     * 角度->弧度
     * */
    public static GeoPoint toRadian(GeoPoint p) {
        return new GeoPoint(Math.toRadians(p.lon), Math.toRadians(p.lat));
    }
    /**
     * 弧度->角度
     * */
    public static GeoPoint toDegree(GeoPoint p) {
        return new GeoPoint(Math.toDegrees(p.lon), Math.toDegrees(p.lat));
    }
    /**
     * 角度->弧度
     * */
    public static GeoEdge toRadian(GeoEdge e) {
        return new GeoEdge(toRadian(e.getStart()),toRadian(e.getEnd()));
    }
    /**
     * 弧度->角度
     * */
    public static GeoEdge toDegree(GeoEdge e) {
        return new GeoEdge(toDegree(e.getStart()),toDegree(e.getEnd()));
    }

    /**
     * 计算点到线段的垂足(最近点)
     * @param onedge
     *      是否可以在线段延长线上
     *      true，直接返回垂足
     *      false返回（垂足，线段起点，线段终点）中的最近点
     * */
    public static GeoPoint closestPointToEdge(GeoPoint gp, GeoEdge e, boolean onedge) {
        double d1 = 1000000000.0, d2, d3, d_nearest;
        Point n, p, k;
        GeoPoint gk, g_nearest;

        if(e.getStart().equals(e.getEnd())) {
            gk = e.getStart();
            return gk;
        }
        n = robust_cross_product(e.getStart(), e.getEnd());
        normalize(n);
        p = geog2cart(gp);
        vector_scale(n, dot_product(p,n));
        k = vector_difference(p, n);
        normalize(k);
        gk = cart2geog(k);
        if (!onedge) return gk;

        if(edge_contains_point(e, gk)) {
            d1 = sphere_distance(gp, gk);
        }
        d2 = sphere_distance(gp, e.getStart());
        d3 = sphere_distance(gp, e.getEnd());
        d_nearest = d1;
        g_nearest = gk;
        if ( d2 < d_nearest )
        {
            d_nearest = d2;
            g_nearest = e.getStart();
        }
        if ( d3 < d_nearest )
        {
            d_nearest = d3;
            g_nearest = e.getEnd();
        }
        return g_nearest;
    }

    /**
     * Returns true if the point p is on the minor edge defined by the
     * end points of e.
     */
    private static boolean edge_contains_point(GeoEdge e, GeoPoint p)
    {
        return edge_point_in_cone(e, p) && edge_point_on_plane(e, p);
    }
    /**
     * Returns true if the point p is inside the cone defined by the
     * two ends of the edge e.
     */
    private static boolean edge_point_in_cone(GeoEdge e, GeoPoint p)
    {
        Point vcp, vs, ve, vp;
        double vs_dot_vcp, vp_dot_vcp;
        vs = geog2cart(e.getStart());
        ve = geog2cart(e.getEnd());
        /* Antipodal case, everything is inside. */
        if ( vs.x == -1.0 * ve.x && vs.y == -1.0 * ve.y && vs.z == -1.0 * ve.z )
            return true;
        vp = geog2cart(p);
        /* The normalized sum bisects the angle between start and end. */
        vcp = vector_sum(vs, ve);
        normalize(vcp);
        /* The projection of start onto the center defines the minimum similarity */
        vs_dot_vcp = dot_product(vs, vcp);
        /* The projection of candidate p onto the center */
        vp_dot_vcp = dot_product(vp, vcp);
        /* If p is more similar than start then p is inside the cone */

        return vp_dot_vcp > vs_dot_vcp || abs(vp_dot_vcp - vs_dot_vcp) < 2e-16;
    }
    /**
     * Returns true if the point p is on the great circle plane.
     * Forms the scalar triple product of A,B,p and if the volume of the
     * resulting parallelepiped is near zero the point p is on the
     * great circle plane.
     */
    private static boolean edge_point_on_plane(GeoEdge e, GeoPoint p)
    {
        int side = edge_point_side(e, p);
        return side == 0;
    }
    /**
     * Returns -1 if the point is to the left of the plane formed
     * by the edge, 1 if the point is to the right, and 0 if the
     * point is on the plane.
     */
    private static int edge_point_side(GeoEdge e, GeoPoint p)
    {
        Point normal, pt;
        double w;
        /* Normal to the plane defined by e */
        normal = robust_cross_product(e.getStart(), e.getEnd());
        normalize(normal);
        pt = geog2cart(p);
        /* We expect the dot product of with normal with any vector in the plane to be zero */
        w = dot_product(normal, pt);
        if ( isZero(w) )
        {
            return 0;
        }
        if ( w < 0 )
            return -1;
        else
            return 1;
    }

    private static Point robust_cross_product(GeoPoint p, GeoPoint q)
    {
        double lon_qpp = (q.lon + p.lon) / -2.0;
        double lon_qmp = (q.lon - p.lon) / 2.0;
        double sin_p_lat_minus_q_lat = sin(p.lat-q.lat);
        double sin_p_lat_plus_q_lat =  sin(p.lat+q.lat);
        double sin_lon_qpp = sin(lon_qpp);
        double sin_lon_qmp = sin(lon_qmp);
        double cos_lon_qpp = cos(lon_qpp);
        double cos_lon_qmp = cos(lon_qmp);

        double x = sin_p_lat_minus_q_lat * sin_lon_qpp * cos_lon_qmp -
                sin_p_lat_plus_q_lat * cos_lon_qpp * sin_lon_qmp;
        double y = sin_p_lat_minus_q_lat * cos_lon_qpp * cos_lon_qmp +
                sin_p_lat_plus_q_lat * sin_lon_qpp * sin_lon_qmp;
        double z = cos(p.lat) * cos(q.lat) * sin(q.lon-p.lon);
        return new Point(x,y,z);
    }
    private static void normalize(Point p)
    {
        double d = sqrt(p.x*p.x + p.y*p.y + p.z*p.z);
        if (isZero(d))
        {
            p.x = p.y = p.z = 0.0;
            return;
        }
        p.x = p.x / d;
        p.y = p.y / d;
        p.z = p.z / d;
    }
    private static Point geog2cart(GeoPoint g)
    {
        double x = cos(g.lat) * cos(g.lon);
        double y = cos(g.lat) * sin(g.lon);
        double z = sin(g.lat);
        return new Point(x,y,z);
    }
    private static GeoPoint cart2geog(Point p)
    {
        double lon = atan2(p.y, p.x);
        double lat = asin(p.z);
        return new GeoPoint(lon,lat);
    }
    private static double dot_product(Point p1, Point p2)
    {
        return (p1.x*p2.x) + (p1.y*p2.y) + (p1.z*p2.z);
    }
    private static void vector_scale(Point n, double scale)
    {
        n.x *= scale;
        n.y *= scale;
        n.z *= scale;
    }
    /**
     * Calculate the sum of two vectors
     */
    private static Point vector_sum(Point a, Point b)
    {
        double x = a.x + b.x;
        double y = a.y + b.y;
        double z = a.z + b.z;
        return new Point(x,y,z);
    }
    private static Point vector_difference(Point a, Point b)
    {
        double x = a.x - b.x;
        double y = a.y - b.y;
        double z = a.z - b.z;
        return new Point(x,y,z);
    }
    private static boolean isZero(double d) {
        return abs(d) < 1E-14;
    }

    /**
     * Given two points on a unit sphere, calculate their distance apart in radians.
     * Unit Sphere
     */
    public static double sphere_distance(GeoPoint s, GeoPoint e)
    {
        double d_lon = e.lon - s.lon;
        double cos_d_lon = cos(d_lon);
        double cos_lat_e = cos(e.lat);
        double sin_lat_e = sin(e.lat);
        double cos_lat_s = cos(s.lat);
        double sin_lat_s = sin(s.lat);

        double a1 = pow(cos_lat_e * sin(d_lon), 2);
        double a2 = pow(cos_lat_s * sin_lat_e - sin_lat_s * cos_lat_e * cos_d_lon, 2);
        double a = sqrt(a1 + a2);
        double b = sin_lat_s * sin_lat_e + cos_lat_s * cos_lat_e * cos_d_lon;
        return atan2(a, b);
    }

    /**
     * 使用Haversine公式计算距离
     * 输入为角度
     * */
    public static double distanceHaversine(double lat1, double lon1, double lat2, double lon2) {
        int earthR = 6371000;
        double x = Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.cos((lon1 - lon2) * Math.PI / 180);
        double y = sin(lat1 * Math.PI / 180) * sin(lat2 * Math.PI / 180);
        double s = x + y;
        if (s > 1) s = 1;
        if (s < -1) s = -1;
        double alpha = Math.acos(s);
        return alpha * earthR;
    }

    /**
     * @FIXEME 引用了org.gavaghan.geodesy库，但是里面封装了好多对象，可能会提高内存使用量
     * 输入为角度
     * */
    public static double distanceSpheroid(double lat1, double lon1, double lat2, double lon2) {
        return geodesicInverse(lat1, lon1, lat2, lon2).getEllipsoidalDistance();
    }


    /**
     * from ch.hsr.geohash
     * returns the {@link WGS84Point} that is in the given direction at the
     * following distance of the given point.<br>
     * Uses Vincenty's formula and the WGS84 ellipsoid.
     *
     * @return [lon, lat]
     */
    public static double[] moveInDirection(double lon, double lat, double bearingInDegrees, double distanceInMeters) {

        if (bearingInDegrees < 0 || bearingInDegrees > 360) {
            throw new IllegalArgumentException("direction must be in (0,360)");
        }

        double a = 6378137, b = 6356752.3142, f = 1 / 298.257223563; // WGS-84
        // ellipsiod
        double alpha1 = bearingInDegrees * degToRad;
        double sinAlpha1 = Math.sin(alpha1), cosAlpha1 = Math.cos(alpha1);

        double tanU1 = (1 - f) * Math.tan(lat * degToRad);
        double cosU1 = 1 / Math.sqrt((1 + tanU1 * tanU1)), sinU1 = tanU1 * cosU1;
        double sigma1 = Math.atan2(tanU1, cosAlpha1);
        double sinAlpha = cosU1 * sinAlpha1;
        double cosSqAlpha = 1 - sinAlpha * sinAlpha;
        double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));

        double sinSigma = 0, cosSigma = 0, cos2SigmaM = 0;
        double sigma = distanceInMeters / (b * A), sigmaP = 2 * Math.PI;
        while (Math.abs(sigma - sigmaP) > 1e-12) {
            cos2SigmaM = Math.cos(2 * sigma1 + sigma);
            sinSigma = Math.sin(sigma);
            cosSigma = Math.cos(sigma);
            double deltaSigma = B
                    * sinSigma
                    * (cos2SigmaM + B
                    / 4
                    * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
                    * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
            sigmaP = sigma;
            sigma = distanceInMeters / (b * A) + deltaSigma;
        }

        double tmp = sinU1 * sinSigma - cosU1 * cosSigma * cosAlpha1;
        double lat2 = Math.atan2(sinU1 * cosSigma + cosU1 * sinSigma * cosAlpha1, (1 - f)
                * Math.sqrt(sinAlpha * sinAlpha + tmp * tmp));
        double lambda = Math.atan2(sinSigma * sinAlpha1, cosU1 * cosSigma - sinU1 * sinSigma * cosAlpha1);
        double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
        double L = lambda - (1 - C) * f * sinAlpha
                * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));

        double newLat = lat2 / degToRad;
        double newLon = lon + L / degToRad;

        newLon = (newLon > 180.0 ? 360.0 - newLon : newLon);
        newLon = (newLon < -180.0 ? 360.0 + newLon : newLon);

        return new double[]{newLon, newLat};
    }

    /**
     * 角度平均值
     * */
    public static double meanAngle(double... anglesDeg) {
        double x = 0.0;
        double y = 0.0;

        for (double angleD : anglesDeg) {
            double angleR = Math.toRadians(angleD);
            x += Math.cos(angleR);
            y += sin(angleR);
        }
        double avgR = Math.atan2(y / anglesDeg.length, x / anglesDeg.length);
        return Math.toDegrees(avgR);
    }

    /**
     * 角度标准差
     * */
    public static double stddevAngle(double... anglesDeg) {
        double x = 0.0;
        double y = 0.0;

        for (double angleD : anglesDeg) {
            double angleR = Math.toRadians(angleD);
            x += Math.cos(angleR);
            y += sin(angleR);
        }
        y /= anglesDeg.length;
        x /= anglesDeg.length;
        double stddevR = sqrt(-Math.log(y*y+x*x));
        return Math.toDegrees(stddevR);
    }

}
