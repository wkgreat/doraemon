import wk.doraemon.geo.CoordinateConverter;
import wk.doraemon.time.TimeUtils;

public class Test {
    public static void main(String[] args) {
        long t1 = TimeUtils.str2Second("2019-06-01 00:00:00","yyyy-MM-dd HH:mm:ss");
        long t2 = TimeUtils.str2Second("2019-07-01 00:00:00","yyyy-MM-dd HH:mm:ss");
        System.out.println(t1);
        System.out.println(t2);
    }
}
