package wk.doraemon.geo.geohash;

import wk.doraemon.geo.GeoUtils;

public class GeoHashCircleIndex {

    long left, right, down, upper;
    long geobits, lonBits, latBits;
    double lon, lat, radius;

    public static GeoHashCircleIndex build(double lon, double lat, double radius, int precision) {

        GeoHashCircleIndex index = new GeoHashCircleIndex();
        index.lon = lon;
        index.lat = lat;
        index.radius = radius;
        index.geobits = GeoBits.geohashbits(lon,lat,precision);
        long[] lonlatBits = GeoBits.splitGeobits(index.geobits);
        index.lonBits = lonlatBits[0];
        index.latBits = lonlatBits[1];
        index.left = index.right = index.lonBits;
        index.down = index.upper = index.latBits;
        double[] cellsize = GeoBits.cellsize(index.lonBits, index.latBits);
        double[] wesn = GeoBits.getGeoBitsBarrier(index.geobits);

        double leftCircleLon = GeoUtils.WGS84.moveInDirection(lon,lat,270,radius)[0];
        double rightCircleLon = lon + (lon-leftCircleLon);
        double downCircleLat = GeoUtils.WGS84.moveInDirection(lon,lat,180,radius)[1];
        double upperCircleLat = lat + (lat-downCircleLat);

        for(double  west=wesn[0]; west>leftCircleLon-1E-10;  west-=cellsize[0]) index.left--;
        for(double  east=wesn[1]; east<=rightCircleLon+1E-10; east+=cellsize[0]) index.right++;
        for(double south=wesn[2]; south>downCircleLat-1E-5;  south-=cellsize[1]) index.down--;
        for(double north=wesn[3]; north<=upperCircleLat+1E-5; north+=cellsize[1]) index.upper++;

        return index;
    }

    public boolean contains(GeoHashCircleIndex otherIndex) {
        if(otherIndex.lonBits<this.left || otherIndex.lonBits >this.right ||
                otherIndex.latBits<this.down || otherIndex.latBits>this.upper){
            return false;
        } else {
            double d = GeoUtils.WGS84.getDistance(lat,lon,otherIndex.lat,otherIndex.lon,true);
            return d<=radius;
        }
    }
}
