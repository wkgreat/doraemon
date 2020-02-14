import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import wk.doraemon.geo.JTSUtils;
import wk.doraemon.io.TextReader;

import java.math.BigInteger;

public class JTSUtilsTest {

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static void main(String[] args) {


    }
}
