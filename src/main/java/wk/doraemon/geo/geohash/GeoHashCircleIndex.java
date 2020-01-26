package wk.doraemon.geo.geohash;

import wk.doraemon.geo.GeoUtils;

public class GeoHashCircleIndex {

    long geobits;
    double lon, lat, radius;
    GeoHashBoxIndex boxIndex;

    public GeoHashCircleIndex(double lon, double lat, double radius) {

        double left = GeoUtils.WGS84.moveInDirection(lon,lat,270,radius)[0];
        double right = GeoUtils.WGS84.moveInDirection(lon,lat,90,radius)[0];
        double down = GeoUtils.WGS84.moveInDirection(lon,lat,180,radius)[1];
        double upper = GeoUtils.WGS84.moveInDirection(lon,lat,0,radius)[1];
        this.lon = lon;
        this.lat = lat;
        this.radius = radius;
        boxIndex = new GeoHashBoxIndex(left,right,down,upper);
        geobits = GeoBits.geohashbits(lon, lat, boxIndex.bitPrecision);

    }

    public boolean contains(GeoHashCircleIndex otherIndex) {
        if(!boxIndex.contains(otherIndex.geobits)) return false;
        double dist = GeoUtils.WGS84.getDistance(lon,lat,otherIndex.lon,otherIndex.lat,true);
        return dist <= radius;
    }

}
