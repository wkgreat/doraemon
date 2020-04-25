/**
 * Created by ke.wang7 on 2020/1/19.
 * @author ke Wang
 *
 * 防止创建许多小对象，防止Spark调用时占用过多内存，并提升效率
 * geohash有两种表现形式：
 *  1) BASE32 (String类型)
 *  2) geobits (Long类型，其中最高位为标识位)
 * 内部主要以geobits进行计算
 * BASE32最高只支持12位，因为当BASE32达到13位时，加上标识位，geobits需要66个二进制位，已经超出了Long型的位数
 * eg
 *  对于点 Point(lon=0, lat=0)
 *  5位的Base32的geohash为
 *      "s0000"
 *  对应的二进制格式为
 *      11100000000000000000000000  (最高位的1为标识位，表明二进制串的长度)
 *  转换为Long型为 58720256 ，及geobits为 58720256
 *
 *  里面的bits在高位都有一个1标识位，叫做lead1，用来防止二进制串以Long型存储时高位0被自动去除
 *  如 001 会自动 变成 1
 *  而加了lead1 为 1001 这样前面的两个零，再以long型存储是就不会去除掉
 *
 */
package wk.doraemon.geo.geohash;

import wk.doraemon.geo.GeoUtils;
import wk.doraemon.geo.JTSUtils;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import static java.lang.Math.abs;
import static java.lang.Math.min;

public class GeoBits {

    private final static String BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz";

    /**
     * 根据经纬度，获取geohash的二进制格式(geobits)
     * @param lon 经度
     * @param lat 维度
     * @param precision geohash 二进制格式的长度
     * */
    public static long geohashbits(double lon, double lat, int precision) {
        int latlen = precision / 2;
        int lonlen = (precision+1) / 2;
        long lonBits = toBinary(-180.0, 180.0, lon, lonlen);
        long latBits = toBinary(-90.0, 90.0, lat, latlen);
        return composeBits(lonBits,latBits);
    }

    /**
     * 根据经纬度，获取Geohash的BASE32格式
     * @param lon 经度
     * @param lat 维度
     * @param charPrecision geohash BASE32格式的长度
     * */
    public static String geohash(double lon, double lat, int charPrecision) {
        long bits = geohashbits(lon, lat, charPrecision*5);
        return geobitsToBase32(bits);
    }

    private static long toBinary(double lower, double upper, double value, int length) {
        double low = lower, upp = upper, mid;
        long bits = 1L; //高位设置为1, 防止高位为0时, 0被去掉
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
        return bits;
    }

    /**
     * geobits -> base32
     * 二进制格式的geohash(geobits) 转换为 base32的 geohash
     * @param geobits geohash in binary format(geobits)
     * @return geohash in base32 format
     * */
    public static String geobitsToBase32(long geobits) {
        StringBuilder sb = new StringBuilder();
        while (geobits>1) {
            sb.append(BASE32.charAt((int)(geobits & 0x1F)));
            geobits >>= 5;
        }
        return sb.reverse().toString();
    }

    /**
     * base32 -> geobits
     * geohash的base32格式转为二进制格式(geobits)
     * @param geohash geohash in base32 format
     * @return geohash in binary format (geobits)
     * */
    public static long geohashToBits(String geohash) {
        long bits = 1;
        for(int i=0; i<geohash.length(); ++i) {
            int cb = BASE32.indexOf(geohash.charAt(i));
            bits = (bits << 5) | cb;
        }
        return bits;
    }

    /**
     * 获取经纬度点所位于的指定BASE32长度的Geohash格子的经纬度范围
     * @param charPrecision Geoahsh BASE32格式的长度
     * @return [west, east, south, north]
     * */
    public static double[] getGeoHashBarrier(double lon, double lat, int charPrecision) {
        return getGeoBitsBarrier(geohashbits(lon, lat, charPrecision*5));
    }

    /**
     * 计算对应geohash(geobits)的格子西东南被格子边界经纬度
     * @param geobits 二级制格式的geohash(geobits)
     * @return [west, east, south, north]
     * */
    public static double[] getGeoBitsBarrier(long geobits) {
        long[] lonlatBits = splitGeobits(geobits);
        long lonBits = lonlatBits[0];
        long lonMask = leftmostOne(lonBits);
        long latBits = lonlatBits[1];
        long latMask = leftmostOne(latBits);
        double lonInv = 360.0/Math.pow(2,getGeoBitsLength(lonBits));
        double latInv = 180.0/Math.pow(2,getGeoBitsLength(latBits));

        double lonleft = -180.0;
        for(int k=0; k<(lonBits^lonMask); ++k) lonleft += lonInv;
        double latdown = -90.0;
        for(int k=0; k<(latBits^latMask); ++k) latdown += latInv;

        return new double[]{lonleft, lonleft+lonInv, latdown, latdown+latInv};
    }

    /**
     * geobits格子大小
     * [东西度数，南北度数]
     * @param bitPrecision the bit length of geobits.
     * */
    public static double[] cellsize(int bitPrecision) {
        double lonInv = 360.0/Math.pow(2, bitPrecision/2.0);
        double latInv = 180.0/Math.pow(2, (bitPrecision+1)/2.0);
        return new double[]{lonInv, latInv};
    }

    /**
     * 向东移动
     * */
    public static long toEast(long geobits) {
        long[] lonlatBits = splitGeobits(geobits);
        long lonMask = leftmostOne(lonlatBits[0]);
        long latMask = leftmostOne(lonlatBits[1]);
        long[] eastLonLatBits = toEast(lonlatBits[0], lonMask, lonlatBits[1], latMask);
        return composeBits(eastLonLatBits[0], eastLonLatBits[1]);
    }
    public static long[] toEast(long lonBits, long lonMask, long latBits, long latMask) {
        lonBits = ++lonBits>(lonMask|(lonMask-1)) ? lonMask : lonBits;
        return new long[]{lonBits, latBits};
    }
    /**
     * 向西移动
     * */
    public static long toWest(long geobits) {
        long[] lonlatBits = splitGeobits(geobits);
        long lonMask = leftmostOne(lonlatBits[0]);
        long latMask = leftmostOne(lonlatBits[1]);
        long[] westLonLatBits = toWest(lonlatBits[0], lonMask, lonlatBits[1], latMask);
        return composeBits(westLonLatBits[0], westLonLatBits[1]);
    }
    public static long[] toWest(long lonBits, long lonMask, long latBits, long latMask) {
        lonBits =  --lonBits<lonMask ? (lonMask|(lonMask-1)) : lonBits;
        return new long[]{lonBits, latBits};
    }
    /**
     * 向北移动
     * */
    public static long toNorth(long geobits) {
        long[] lonlatBits = splitGeobits(geobits);
        long lonMask = leftmostOne(lonlatBits[0]);
        long latMask = leftmostOne(lonlatBits[1]);
        long[] northLonLatBits = toNorth(lonlatBits[0], lonMask, lonlatBits[1], latMask);
        return composeBits(northLonLatBits[0], northLonLatBits[1]);
    }
    public static long[] toNorth(long lonBits, long lonMask, long latBits, long latMask){
        long nlatBits = latBits+1;
        if(nlatBits>(latMask|(latMask-1))) {
            return new long[] {antipodLonBits(lonBits, lonMask), latBits};
        } else {
            return new long[] {lonBits, nlatBits};
        }
    }
    /**
     * 向南移动
     * */
    public static long toSouth(long geobits) {
        long[] lonlatBits = splitGeobits(geobits);
        long lonMask = leftmostOne(lonlatBits[0]);
        long latMask = leftmostOne(lonlatBits[1]);
        long[] southLonLatBits = toSouth(lonlatBits[0], lonMask, lonlatBits[1], latMask);
        return composeBits(southLonLatBits[0], southLonLatBits[1]);
    }
    public static long[] toSouth(long lonBits, long lonMask, long latBits, long latMask){
        long nlatBits = latBits - 1;
        if(nlatBits<latMask) {
            return new long[] {antipodLonBits(lonBits, lonMask), latBits};
        } else {
            return new long[] {lonBits, nlatBits};
        }
    }

    /**
     * Antipode的经度bits
     * */
    public static long antipodLonBits(long lonBits, long lonMask) {
        long halfBits = lonMask / 2;
        return lonBits>=halfBits ? lonBits - halfBits : lonBits + halfBits;
    }

    /**
     * 获取西东南北与该geohash(geobits)相邻的geohash(geobits)
     * [west, east, south, north]
     * */
    public static long[] neighbor4(long geobits) {

        long[] splitedBits = splitGeobits(geobits);
        long lonBits = splitedBits[0];
        long latBits = splitedBits[1];
        long lonMask = leftmostOne(lonBits);
        long latMask = leftmostOne(latBits);

        long[] westBits = toWest(lonBits, lonMask, latBits, latMask);
        long[] eastBits = toEast(lonBits, lonMask, latBits, latMask);
        long[] southBits = toSouth(lonBits, lonMask, latBits, latMask);
        long[] northBits = toNorth(lonBits, lonMask, latBits, latMask);

        return new long[] {
                composeBits(westBits[0],westBits[1]),
                composeBits(eastBits[0],eastBits[1]),
                composeBits(southBits[0],southBits[1]),
                composeBits(northBits[0],northBits[1])
        };
    }
    /**
     * [NW N NE W E SW S SE]
     * */
    public static long[] neighbor8(long geobits) {

        long[] splitedBits = splitGeobits(geobits);
        long lonBits = splitedBits[0];
        long latBits = splitedBits[1];
        long lonMask = leftmostOne(lonBits);
        long latMask = leftmostOne(latBits);

        long[] westBits = toWest(lonBits, lonMask, latBits, latMask);
        long[] eastBits = toEast(lonBits, lonMask, latBits, latMask);
        long[] southBits = toSouth(lonBits, lonMask, latBits, latMask);
        long[] northBits = toNorth(lonBits, lonMask, latBits, latMask);

        long[] northWestBits = toWest(northBits[0], lonMask, northBits[1], latMask);
        long[] northEastBits = toEast(northBits[0], lonMask, northBits[1], latMask);
        long[] southWestBits = toWest(southBits[0], lonMask, southBits[1], latMask);
        long[] southEastBits = toEast(southBits[0], lonMask, southBits[1], latMask);

        return new long[] {
                composeBits(northWestBits[0], northWestBits[1]),
                composeBits(northBits[0],northBits[1]),
                composeBits(northEastBits[0], northBits[1]),
                composeBits(westBits[0],westBits[1]),
                composeBits(eastBits[0],eastBits[1]),
                composeBits(southWestBits[0],southWestBits[1]),
                composeBits(southBits[0],southBits[1]),
                composeBits(southEastBits[0],southEastBits[1])
        };
    }

    public static Set<Long> expandByRadius(long geobits, double radius) {
        double[] barrier = getGeoBitsBarrier(geobits); //[west, east, south, north]
        double minLat = abs(barrier[2])<abs(barrier[3]) ? barrier[2] : barrier[3];
        double[] rWest = GeoUtils.WGS84.moveInDirection(
                barrier[0],minLat,270, radius);
        double[] rEast = GeoUtils.WGS84.moveInDirection(
                barrier[1],minLat,90, radius);
        double[] rSouth = GeoUtils.WGS84.moveInDirection(
                barrier[0],barrier[2],180,radius);
        double[] rNorth = GeoUtils.WGS84.moveInDirection(
                barrier[0],barrier[3],0,radius);
        return expandGeohashes(rWest[0],rEast[0],rSouth[1],rNorth[1], getGeoBitsLength(geobits));

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

    private static boolean intersect(double west, double east, double south, double north, long geobits) {
        double[] barriers = GeoBits.getGeoBitsBarrier(geobits); //west east south north
        return !(barriers[0] > east || barriers[1] < west || barriers[2] > north || barriers[3] < south);
    }

    /**
     * 将两个geobits交错合并
     * 应用于经度geobits和纬度geobits交错合并，生成完整的geobits
     * eg 100000, 11111
     * => 1010101010
     * */
    public static long composeBits(long lonBits, long latBits) {
        long newBits = 1;
        long lonMask = leftmostOne(lonBits);
        long latMask = leftmostOne(latBits);
        int shift = 1;
        while((lonMask>>shift)!=0) {
            if(((lonMask>>shift) & lonBits)==0) newBits = appendZero(newBits);
            else newBits = appendOne(newBits);
            if((latMask>>shift)==0) break;
            if(((latMask>>shift) & latBits)==0) newBits = appendZero(newBits);
            else newBits = appendOne(newBits);
            shift++;
        }
        return newBits;
    }

    /**
     * 将交错分布的二进制串分解
     * bits高位为lead1
     * eg 1010101 => [1111, 1000]
     *
     * 输入的bits为高位包含lead1,防止0截断
     * 获取偶数位和奇数位上的二级制串
     * 偶数位 第 0, 2, 4, 6 ... 2n 位上二进制位
     * 奇数位 第 1, 3, 5, 7 ... 2n+1 位上的二进制位
     * eg 注意高位1 为 lead1 只起到防截断的作用
     *                1000111011
     * 偶数位          1. . . . .
     * 奇数位          1 . . . .
     * 偶数位结果      10  0 1 0 1 => 100101
     * 奇数位结果      1  0 1 1 1  => 10111
     *
     * */
    public static long[] splitGeobits(long geobits) {
        long evenBits = 1, oddBits = 1;
        long lonBits, latBits;
        boolean isBreak = false;
        while(geobits>1) {
            long leadMask = leftmostOne(evenBits);
            long newLeadMask = ((geobits&1)==0) ? leadMask*2 : leadMask*3; //10...*2 = 100... ; 10...*3 = 110...
            evenBits = (evenBits^leadMask) + newLeadMask;
            geobits >>= 1;
            if(geobits<=1) {
                isBreak = true;
                break;
            }
            newLeadMask = ((geobits&1)==0) ? leadMask*2 : leadMask*3; //10...*2 = 100... ; 10...*3 = 110...
            oddBits = (oddBits^leadMask) + newLeadMask;
            geobits >>= 1;
        }
        if(isBreak) { //如果break，说明经纬度交错合并的二进制串位数为奇数
            lonBits = evenBits;
            latBits = oddBits;
        } else {
            lonBits = oddBits;
            latBits = evenBits;
        }
        return new long[]{lonBits, latBits};
    }

    /**
     * 包含标志1
     * */
    private static long mask0Place(long bits) {
        long leadMask = leftmostOne(bits);
        int i=0;
        long v = (long) Math.pow(4,i);
        long m=0;
        while(m+v < leadMask) {
            m+=v;
            v = (long) Math.pow(4,++i);
        }
        return m;
    }
    /**
     * 包含标志1
     * */
    private static long mask1Place(long bits) {
        long leadMask = leftmostOne(bits);
        int i=0;
        long v = (long) (2*Math.pow(4,i));
        long m=0;
        while(m+v < leadMask) {
            m+=v;
            v = (long) (2*Math.pow(4,++i));
        }
        return m;
    }

    /**
     * 二进制串低位加1
     * eg:
     * 010100 ->
     * 0101001
     * */
    private static long appendOne(long bits) {
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
    private static long appendZero(long bits) {
        return bits << 1;
    }

    /**
     * 将二进制串最左侧的1保留，右侧的1全置为0
     * 可以取到geobits的标识位
     * eg
     * 100101 ->
     * 100000
     * */
    protected static long leftmostOne(long bits) {
        for(long newb = bits & (bits-1); newb > 0; newb = bits & (bits-1)) bits = newb;
        return bits;
    }

    /**
     * geobits的长度
     * */
    public static int getGeoBitsLength(long geobits) {
        long mask = leftmostOne(geobits);
        return (int) (Math.log(mask) / Math.log(2));
    }

    /**
     * geobtis cell to wkt
     * */
    public static String toWKT(long geobits) {
        double[] barriers = GeoBits.getGeoBitsBarrier(geobits);
        return JTSUtils.geom2wkt(JTSUtils.createBox(barriers[0],barriers[1],barriers[2],barriers[3], 4326));
    }

    /**
     * geobtis cell to wkt
     * */
    public static byte[] toWKB(long geobits) {
        double[] barriers = GeoBits.getGeoBitsBarrier(geobits);
        return JTSUtils.geom2wkb(JTSUtils.createBox(barriers[0],barriers[1],barriers[2],barriers[3], 4326));
    }

    public static String showWKT(long geobits) {
        double[] barriers = GeoBits.getGeoBitsBarrier(geobits);
        String wkt = JTSUtils.geom2wkt(JTSUtils.createBox(barriers[0],barriers[1],barriers[2],barriers[3], 4326));
        return wkt;
    }

    public static void main(String[] args) {

        long lonBits = Long.valueOf("10010", 2);
        long mask = leftmostOne(lonBits);
        long anti = antipodLonBits(lonBits,mask);
        System.out.println(anti);
        System.out.println(Long.toBinaryString(anti));

    }
}