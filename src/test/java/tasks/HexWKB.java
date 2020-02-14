package tasks;/*
 */

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPoint;
import wk.doraemon.geo.JTSUtils;
import wk.doraemon.io.Bits;
import wk.doraemon.io.TextReader;
import wk.doraemon.io.TextWriter;

import java.io.PrintWriter;

public class HexWKB {


    public static void main(String[] args) {

        TextReader reader = new TextReader("/Users/wkgreat/codes/amh/ymm-data-analysis/output_data/皖L92931.txt");
        reader.init();
        TextWriter textWriter = new TextWriter("/Users/wkgreat/codes/amh/ymm-data-analysis/output_data/皖L92931_wkt.txt",false).init();
        for(String line : reader.readlines()) {
            byte[] wkb = Bits.hexStringToByteArray(line);
            LineString geom = (LineString) JTSUtils.wkb2geom(wkb);
            MultiPoint multiPoint = JTSUtils.lineString2MultiPoint(geom);
            String wkt = JTSUtils.geom2wkt(multiPoint);
            System.out.println(wkt);
            textWriter.writeLine(wkt);
        }
        textWriter.close();

    }

}
