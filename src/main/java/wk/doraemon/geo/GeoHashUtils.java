/**
 * Created by ke.wang7 on 2020/1/19.
 * 防止创建许多小对象，防止Spark调用时占用过多内存，并提升效率
 * 右领域: 纬度bits不变，经度bits值加1，注意边界条件
 * 左领域: 纬度bits不变，经度bits值减1，注意边界条件
 * 上领域: 经度bits不变，纬度bits值加1，注意边界条件
 * 下领域: 经度bits不变，纬度bits值减1，注意边界条件
 */
package wk.doraemon.geo;

import ch.hsr.geohash.GeoHash;

public class GeoHashUtils {

    private final static String BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz";

    /**
     * 根据经纬度，获取geohash的二进制格式
     * @param precision geohash 二进制格式的长度
     * */
    public static String geohashbits(double lon, double lat, int precision) {

        int latlen = precision / 2;
        int lonlen = precision%2==0 ? latlen : latlen+1;
        StringBuilder lonBitsBuffer = toBinary(-180.0, 180.0, lon, lonlen);
        StringBuilder latBitsBuffer = toBinary(-90.0, 90.0, lat, latlen);
        StringBuffer bits = new StringBuffer();
        int i=0;
        for(; i<latlen; ++i) {
            bits.append(lonBitsBuffer.charAt(i));
            bits.append(latBitsBuffer.charAt(i));
        }
        bits.append(lonBitsBuffer.substring(i));
        return bits.toString();
    }

    /**
     * 根据经纬度，获取Geohash的BASE32格式
     * @param precision geohash BASE32格式的长度
     * */
    public static String geohash(double lon, double lat, int precision) {
        String bits = geohashbits(lon, lat, precision*5);
        StringBuilder theGeohash = new StringBuilder();
        for(int i=0; i<bits.length(); i+=5) {
            int k = Integer.valueOf(bits.substring(i,i+5), 2);
            theGeohash.append(BASE32.charAt(k));
        }
        return theGeohash.toString();
    }

    private static StringBuilder toBinary(double lower, double upper, double value, int length) {
        StringBuilder bitsStr = new StringBuilder();
        double low = lower, upp = upper, mid;
        int bits = 1; //高位设置为1, 防止高位为0时, 0被去掉
        for(int clen=0; clen < length; clen++) {
            mid = (low + upp) / 2;
            if(value < mid) {
                bits = appendZero(bits);
                upp = mid;
            } else {
                bits = appendOne(bits);
                low = mid;
            }
        }
        bitsStr.append(getbits(bits)); //getbits函数中将高位1给去掉
        return bitsStr;
    }

    /**
     * geohash的base32格式转为二进制串
     * */
    private static void geohashToBits(String geohash) {
        for(int i=0; i<geohash.length(); ++i) {
            int b = BASE32.indexOf(geohash.charAt(i));
            //TODO
        }
    }

    /**
     * 获取经纬度点所位于的指定BASE32长度的Geohash格子的经纬度范围
     * @param geohashLens Geoahsh BASE32格式的长度
     * */
    public static double[] getGeoHashBarrier(double lon, double lat, int geohashLens) {
        int bitsLens = geohashLens * 5;
        double[] westEast = getLonBarrier(lon, bitsLens/2+bitsLens%2);
        double[] southNorth = getLatBarrier(lat, bitsLens/2);
        double [] wesn = new double[]{westEast[0],westEast[1],southNorth[0],southNorth[1]};
        return wesn;
    }

    /**
     * 获取指定经度的指定二进制长度的geohash的西东经度范围
     * */
    public static double[] getLonBarrier(double lon, int lonbitLens) {
        return getBarrier(-180.0, 180.0, lon, lonbitLens);
    }

    /**
     * 获取指定纬度的指定二进制长度的geohash的南北纬度范围
     * */
    public static double[] getLatBarrier(double lat, int latbitLens) {
        return getBarrier(-90.0, 90.0, lat, latbitLens);
    }

    private static double[] getBarrier(double lower, double upper, double value, int length) {
        double low = lower, upp = upper, mid;
        for(int i=0; i<length; ++i) {
            mid = (low + upp) / 2;
            if(value < mid) upp = mid;
            else low = mid;
        }
        return new double[] {low, upp};
    }

    /**
     * 二进制串低位加1
     * eg:
     * 010100 ->
     * 0101001
     * */
    private static int appendOne(int bits) {
        bits <<= 1;
        bits |= 1;
        return bits;
    }

    /**
     * 二进制串低位加0
     * eg
     * 010100 ->
     * 0101000
     * */
    private static int appendZero(int bits) {
        return bits << 1;
    }

    /**
     * 获取二进制字符串，并去掉最高位
     * eg
     * 100100 ->
     * "00100"
     * */
    private static String getbits(int bits) {
        return Integer.toBinaryString(bits).substring(1);
    }

    public static void main(String[] args) {

        double lon = 117;
        double lat = 32;
        String geohash = geohash(lon,lat,7);
        System.out.println(geohash);

        GeoHash gg = GeoHash.withCharacterPrecision(lat,lon,7);
        System.out.println(gg.toString());

        double[] wesn = getGeoHashBarrier(lon,lat, 7);
        double west = wesn[0], east = wesn[1], south = wesn[2], north = wesn[3];
        System.out.println("" + west +"," + east +","+ south +"," + north);
        System.out.println(GeoUtils.WGS84.getDistance(wesn[2],wesn[0],wesn[2],wesn[1], true));
        System.out.println(GeoUtils.WGS84.getDistance(wesn[2],wesn[0],wesn[3],wesn[0], true));


    }

}
