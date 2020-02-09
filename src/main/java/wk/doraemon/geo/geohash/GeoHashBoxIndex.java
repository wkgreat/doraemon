package wk.doraemon.geo.geohash;

import wk.doraemon.geo.GeoUtils;

import java.io.Serializable;
import java.util.*;

public class GeoHashBoxIndex implements Serializable {

    private final static int INF = Integer.MAX_VALUE;

    int bitPrecision;
    Set<Long> allGeobits;

    public GeoHashBoxIndex(double west, double east, double south, double north, int bitPrecision) {
        allGeobits = GeoBits.expandGeohashes(west,east,south,north,bitPrecision);
    }
    public GeoHashBoxIndex(double west, double east, double south, double north) {
        bitPrecision = fittingPrecision(west, east, south, north);
        allGeobits = GeoBits.expandGeohashes(west,east,south,north,bitPrecision);
    }

    public boolean contains(long geobits) {
        for(long g : allGeobits) {
            if (compare(g, geobits)) return true;
        }
        return false;
    }

    public int getBitPrecision() {
        return bitPrecision;
    }

    public Set<Long> getAllGeobits() {
        return allGeobits;
    }

    public static boolean compare(long geobits1, long geobits2) {
        int len1 = GeoBits.getGeoBitsLength(geobits1);
        int len2 = GeoBits.getGeoBitsLength(geobits2);
        if(len2>len1) geobits2 >>>= len2-len1;
        else geobits1>>>=len1-len2;
        return geobits1==geobits2;
    }

    /**
     * 自适应GeoHash精度
     * */
    public static int fittingPrecision(double west, double east, double south, double north) {
        return fittingPrecision(lonDiff(west,east), latDiff(south,north));
    }
    public static int fittingPrecision(double lonRange, double latRange) {
        int lonp=INF, latp=INF;
        for(int cp=63; cp>=2; cp--) {
            double[] csize = GeoBits.cellsize(cp);
            if(csize[0]>=lonRange && lonp==INF) lonp = cp;
            if(csize[1]>=latRange && latp==INF) latp = cp;
        }
        if(lonp==INF) lonp = 2;
        if(latp==INF) latp = 2;
        return (lonp+latp) / 2;
    }

    private static double lonDiff(double lon1, double lon2) {
        if(lon1<=lon2) return lon2 - lon1;
        else return 360 + lon2 - lon1;
    }

    private static double latDiff(double lat1, double lat2) {
        return lat2 - lat1;
    }

}
