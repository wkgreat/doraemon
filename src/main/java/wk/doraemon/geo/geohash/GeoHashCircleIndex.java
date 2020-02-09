package wk.doraemon.geo.geohash;

import wk.doraemon.geo.GeoUtils;

import java.io.Serializable;

public class GeoHashCircleIndex implements Serializable {

    long geobits;
    double lon, lat, radius;
    GeoHashBoxIndex boxIndex;

    public static GeoHashCircleIndex build(double lon, double lat, double radius) {
        GeoHashCircleIndex theIndex = new GeoHashCircleIndex();
        double left = GeoUtils.WGS84.moveInDirection(lon,lat,270,radius)[0];
        double right = GeoUtils.WGS84.moveInDirection(lon,lat,90,radius)[0];
        double down = GeoUtils.WGS84.moveInDirection(lon,lat,180,radius)[1];
        double upper = GeoUtils.WGS84.moveInDirection(lon,lat,0,radius)[1];
        theIndex.lon = lon;
        theIndex.lat = lat;
        theIndex.radius = radius;
        theIndex.boxIndex = new GeoHashBoxIndex(left,right,down,upper);
        theIndex.geobits = GeoBits.geohashbits(lon, lat, theIndex.boxIndex.bitPrecision);
        return theIndex;
    }
    public static GeoHashCircleIndex build(double lon, double lat, double radius, int bitPrecision) {
        GeoHashCircleIndex theIndex = new GeoHashCircleIndex();
        double left = GeoUtils.WGS84.moveInDirection(lon,lat,270,radius)[0];
        double right = GeoUtils.WGS84.moveInDirection(lon,lat,90,radius)[0];
        double down = GeoUtils.WGS84.moveInDirection(lon,lat,180,radius)[1];
        double upper = GeoUtils.WGS84.moveInDirection(lon,lat,0,radius)[1];
        theIndex.lon = lon;
        theIndex.lat = lat;
        theIndex.radius = radius;
        theIndex.boxIndex = new GeoHashBoxIndex(left,right,down,upper,bitPrecision);
        theIndex.geobits = GeoBits.geohashbits(lon, lat, bitPrecision);
        return theIndex;
    }

    private GeoHashCircleIndex(){}

    public long getGeobits() {
        return geobits;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public double getRadius() {
        return radius;
    }

    public GeoHashBoxIndex getBoxIndex() {
        return boxIndex;
    }

    public boolean contains(GeoHashCircleIndex otherIndex) {
        if(!boxIndex.contains(otherIndex.geobits)) return false;
        double dist = GeoUtils.WGS84.getDistance(lon,lat,otherIndex.lon,otherIndex.lat,true);
        return dist <= radius;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoHashCircleIndex index = (GeoHashCircleIndex) o;
        return this.contains(index);
    }

    @Override
    public int hashCode() {
        return 0;
    }


    /**
     * 根据半径和位置，确定最佳geobits精度
     * */
    public static int fitBitsPrecision(double lon, double lat, double radius) {
        double left = GeoUtils.WGS84.moveInDirection(lon,lat,270,radius)[0];
        double right = GeoUtils.WGS84.moveInDirection(lon,lat,90,radius)[0];
        double down = GeoUtils.WGS84.moveInDirection(lon,lat,180,radius)[1];
        double upper = GeoUtils.WGS84.moveInDirection(lon,lat,0,radius)[1];
        return GeoHashBoxIndex.fittingPrecision(left,right,down,upper);
    }

    public static void main(String[] args) {

        GeoHashCircleIndex index = GeoHashCircleIndex.build(117,32,1000,30);
        System.out.println(index.getBoxIndex().getAllGeobits());


    }

}
