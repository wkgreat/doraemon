package wk.doraemon.geo.geohash;

public class GeoHash {

    public static String getGeoHash(double lon, double lat, int charPrecision) {
        return GeoBits.geohash(lon, lat, charPrecision);
    }

    public static String north(String geohash) {
        long geobits = GeoBits.geohashToBits(geohash);
        long northBits = GeoBits.toNorth(geobits);
        return GeoBits.geobitsToBase32(northBits);
    }
    public static String south(String geohash) {
        long geobits = GeoBits.geohashToBits(geohash);
        long southBits = GeoBits.toSouth(geobits);
        return GeoBits.geobitsToBase32(southBits);
    }
    public static String west(String geohash) {
        long geobits = GeoBits.geohashToBits(geohash);
        long westBits = GeoBits.toWest(geobits);
        return GeoBits.geobitsToBase32(westBits);
    }
    public static String east(String geohash) {
        long geobits = GeoBits.geohashToBits(geohash);
        long eastBits = GeoBits.toNorth(geobits);
        return GeoBits.geobitsToBase32(eastBits);
    }

}
