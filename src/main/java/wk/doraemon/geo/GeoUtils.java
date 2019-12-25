package wk.doraemon.geo;

import java.io.Serializable;

public class GeoUtils implements Serializable {
    public static class WGS84 implements Serializable {

        public static final double a = 6378137.0; /*equatorial radius*/
        public static final double c = 6356752.3; /*polar radius*/
        public static final double f = 1 / 298.257223; /*flattening*/

        /**
         * latitude is radian
         * get radius of WGS spheroid
         * Todo: 只考虑纬度半径的影响。还要考虑海拔的影响。
         */
        public static double getR(double latitude) {
            double base1 = Math.pow(Math.cos(latitude), 2) / Math.pow(a, 2);
            double base2 = Math.pow(Math.sin(latitude), 2) / Math.pow(c, 2);
            double r = Math.pow((base1 + base2), -0.5);
            return r;
        }

        public static double getDistance(double lat1, double lng1, double lat2, double lng2) {

            lat1 = Math.toRadians(lat1);
            lng1 = Math.toRadians(lng1);
            lat2 = Math.toRadians(lat2);
            lng2 = Math.toRadians(lng2);
            double r = getR(Math.toRadians((lat1 + lat2) / 2));
            double d1 = Math.abs(lat1 - lat2);
            double d2 = Math.abs(lng1 - lng2);
            double p = Math.pow(Math.sin(d1 / 2), 2)
                    + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(d2 / 2), 2);
            double dis = r * 2 * Math.asin(Math.sqrt(p));
            return dis;

        }

    }

    //B点在A点的什么方位
    //计算方向角，正北方向为0度，顺时针0-360度
    public static double getDirectionDegree(double Alat, double Alng, double Blat, double Blng) {
        double x1 = Alng;
        double y1 = Alat;
        double x2 = Blng;
        double y2 = Blat;
        double pi = Math.PI;
        double w1 = y1 / 180 * pi;
        double j1 = x1 / 180 * pi;
        double w2 = y2 / 180 * pi;
        double j2 = x2 / 180 * pi;
        double ret;
        if (j1 == j2) {
            if (w1 > w2) return 270;
            else if (w1 < w2) return 90;
            else return -1;
        }
        ret = 4 * Math.pow(Math.sin((w1 - w2) / 2), 2) - Math.pow(Math.sin((j1 - j2) / 2) * (Math.cos(w1) - Math.cos(w2)), 2);
        ret = Math.sqrt(ret);
        double temp = Math.sin(Math.abs(j1 - j2) / 2) * (Math.cos(w1) + Math.cos(w2));
        ret = ret / temp;
        ret = Math.atan(ret) / pi * 180;
        if (j1 > j2) {
            if (w1 > w2) ret += 180;
            else ret = 180 - ret;
        } else if (w1 > w2) ret = 360 - ret;
        return ret;
    }

    //B点在A点的什么方位
    //计算方向角，正北方向为0度，顺时针0-360度
    public static double getDirectionDegree(String Alat, String Alng, String Blat, String Blng) {
        double x1 = Double.parseDouble(Alng);
        double y1 = Double.parseDouble(Alat);
        double x2 = Double.parseDouble(Blng);
        double y2 = Double.parseDouble(Blat);
        double pi = Math.PI;
        double w1 = y1 / 180 * pi;
        double j1 = x1 / 180 * pi;
        double w2 = y2 / 180 * pi;
        double j2 = x2 / 180 * pi;
        double ret;
        if (j1 == j2) {
            if (w1 > w2) return 270;
            else if (w1 < w2) return 90;
            else return -1;
        }
        ret = 4 * Math.pow(Math.sin((w1 - w2) / 2), 2) - Math.pow(Math.sin((j1 - j2) / 2) * (Math.cos(w1) - Math.cos(w2)), 2);
        ret = Math.sqrt(ret);
        double temp = Math.sin(Math.abs(j1 - j2) / 2) * (Math.cos(w1) + Math.cos(w2));
        ret = ret / temp;
        ret = Math.atan(ret) / pi * 180;
        if (j1 > j2) {
            if (w1 > w2) ret += 180;
            else ret = 180 - ret;
        } else if (w1 > w2) ret = 360 - ret;
        return ret;
    }

    public static void main(String[] args) {
        double lat1 = 32.955582;
        double lon1 = 117.339232;
        double lat2 = 32.927565;
        double lon2 = 117.347378;
        double dist = GeoUtils.WGS84.getDistance(lat1, lon1, lat2, lon2);
        System.out.println(dist);
    }

}


