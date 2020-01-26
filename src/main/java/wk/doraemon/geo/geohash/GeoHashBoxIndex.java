package wk.doraemon.geo.geohash;

import java.io.Serializable;
import java.util.*;

public class GeoHashBoxIndex implements Serializable {

    private final static int INF = Integer.MAX_VALUE;

    public int bitPrecision;
    public Set<Long> allGeobits;

    public GeoHashBoxIndex(double west, double east, double south, double north) {
        bitPrecision = fittingPrecision(lonDiff(west,east), latDiff(south,north));
        allGeobits = expandGeohashes(west,east,south,north,bitPrecision);
    }

    public boolean contains(long geobits) {
        for(long g : allGeobits) {
            if (compare(g, geobits)) return true;
        }
        return false;
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

    public static Set<Long> expandGeohashes(double west, double east, double south, double north, int bitPrecision) {
        Set<Long> geobits = new HashSet<>();
        Queue<Long> cache = new ArrayDeque<>();
        long wsBits = GeoBits.geohashbits(west, south, bitPrecision);
        cache.add(wsBits);
        while (!cache.isEmpty()) {
            long theBits = cache.poll();
            if(!geobits.contains(theBits) && intersect(west,east,south,north,theBits)) {
                geobits.add(theBits);
                for(long n : GeoBits.neighbor8(theBits)) {
                    cache.add(n);
                }
            }
        }
        return geobits;
    }

    public static boolean intersect(double west, double east, double south, double north, long geobits) {
        double[] barriers = GeoBits.getGeoBitsBarrier(geobits); //west east south north
        return !(barriers[0] > east || barriers[1] < west || barriers[2] > north || barriers[3] < south);
    }

    private static double lonDiff(double lon1, double lon2) {
        if(lon1<=lon2) return lon2 - lon1;
        else return 360 + lon2 - lon1;
    }

    private static double latDiff(double lat1, double lat2) {
        return lat2 - lat1;
    }

    public static void main(String[] args) {
//        GeoHashBoxIndex index = new GeoHashBoxIndex(117.0,117.01,32.0,32.01);
//        for(long geobit : index.geobits) {
//            System.out.println(Long.toBinaryString(geobit));
//            double [] barrier = GeoBits.getGeoBitsBarrier(geobit);
//            System.out.println(barrier[0]+","+barrier[1]+","+barrier[2]+","+barrier[3]);
//        }

        long g1 = Long.valueOf("100000001",2);
        long g2 = Long.valueOf("10",2);
        System.out.println(GeoHashBoxIndex.compare(g1,g2));
    }

}
