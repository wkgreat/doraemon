import wk.doraemon.geo.CoordinateConverter;

public class Test {
    public static void main(String[] args) {
        double lon = 109.304401;
        double lat = 28.513859;
        double[] r = CoordinateConverter.gcj2WGS(lat,lon);
        System.out.println(r[0]+","+r[1]);
    }
}
