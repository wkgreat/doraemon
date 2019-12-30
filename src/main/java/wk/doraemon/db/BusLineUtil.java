package wk.doraemon.db;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import wk.doraemon.geo.CoordinateConverter;
import wk.doraemon.geo.JTSUtils;
import wk.doraemon.io.TextReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1001225 on 2019/8/16.
 */
public class BusLineUtil {
    public static void insertBusLines(List<List<Object>> records) {

        String sql =
                "INSERT INTO bus_line (" +
                        "bus_id," +
                        "bus_type," +
                        "bus_name," +
                        "start_stop," +
                        "end_stop," +
                        "stime," +
                        "etime," +
                        "distance," +
                        "basic_price," +
                        "total_price," +
                        "company," +
                        "the_geom) VALUES (?,?,?,?,?,?,?,?,?,?,?,ST_GeomFromText(?,4326));";

        Connection ct = null;
        PreparedStatement ps = null;

        try {
            ct = PostGISUtil.getConnection();
            ct.setAutoCommit(false);

            ps = ct.prepareStatement(sql);

            for(List<Object> record : records) {
                ps.setString( 1, (String) record.get(0));
                ps.setString( 2, (String) record.get(1));
                ps.setString( 3, (String) record.get(2));
                ps.setString( 4, (String) record.get(3));
                ps.setString( 5, (String) record.get(4));
                ps.setString( 6, (String) record.get(5));
                ps.setString( 7, (String) record.get(6));
                if(record.get(7)!=null) {
                    ps.setDouble(8, (double) record.get(7));
                }
                if(record.get(8)!=null) {
                    ps.setDouble(9, (double) record.get(8));
                }
                if(record.get(9)!=null) {
                    ps.setDouble(10, (double) record.get(9));
                }
                ps.setString(11, (String) record.get(10));
                ps.setString(12, (String) record.get(11));
                ps.addBatch();

            }

            ps.executeBatch();
            ct.commit();
            ct.setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     *   bus_id VARCHAR(12),
     stop_id VARCHAR(12),
     stop_name VARCHAR(20),
     stop_sequence INT
     * */
    public static void insertBusStop(List<List<Object>> records) {

        String sql =
                "INSERT INTO bus_line_stop (" +
                        "bus_id," +
                        "stop_id," +
                        "stop_name," +
                        "stop_sequence," +
                        "the_geom) VALUES (?,?,?,?,ST_GeomFromText(?,4326));";

        Connection ct = null;
        PreparedStatement ps = null;

        try {
            ct = PostGISUtil.getConnection();
            ct.setAutoCommit(false);

            ps = ct.prepareStatement(sql);

            for(List<Object> record : records) {
                ps.setString( 1, (String) record.get(0));
                ps.setString( 2, (String) record.get(1));
                ps.setString( 3, (String) record.get(2));
                ps.setInt( 4, (Integer) record.get(3));
                ps.setString( 5, (String) record.get(4));
                ps.addBatch();

            }

            ps.executeBatch();
            ct.commit();
            ct.setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public static void insertBusData(String filepath) {

        List<List<Object>> busLineRecords = new ArrayList<>();
        List<List<Object>> busStopRecords = new ArrayList<>();

        TextReader reader = new TextReader(filepath,"UTF-8").init();

        List<String> lines = reader.readlines();
        for (String line : lines) {

            JSONArray array = JSON.parseArray(line);
            int size0 = array.size();
            for (int a = 0; a < size0; a++) {

                JSONArray lineInfo = array.getJSONObject(a).getJSONArray("lineInfo");

                for (int j = 0; j < lineInfo.size(); j++) {

                    JSONObject object1 = lineInfo.getJSONObject(j);

                    String bus_id = object1.getString("id");
                    System.out.println(bus_id);
                    String type = object1.getString("type");
                    String name = object1.getString("name");
                    String start_stop = object1.getString("start_stop");
                    String end_stop = object1.getString("end_stop");
                    String time1 = object1.getString("stime");
                    String stime;
                    if (!StringUtils.isNumeric(time1)) {
                        stime = "0000";
                    } else {
                        stime = time1;
                    }
                    String time2 = object1.getString("etime");
                    String etime;
                    if (!StringUtils.isNumeric(time2)) {
                        etime = "0000";
                    } else {
                        etime = time2;
                    }
                    double distance = object1.getDouble("distance");
                    double basic_price = 0;
                    String price1 = object1.getString("basic_price");
                    if (!price1.isEmpty() && StringUtils.isNumeric(price1)) {
                        basic_price = Float.parseFloat(price1);
                    }
                    double total_price = 0;
                    String price2 = object1.getString("total_price");
                    if (!price2.isEmpty() && StringUtils.isNumeric(price2)) {
                        total_price = Float.parseFloat(price2);
                    }
                    String company = object1.getString("company");

                    JSONArray path = object1.getJSONArray("path");
                    List<Coordinate> coords = new ArrayList<>();
                    for (int m = 0; m < path.size(); m++) {
                        JSONObject object2 = path.getJSONObject(m);
                        double lat = object2.getDouble("lat");
                        double lng = object2.getDouble("lng");
                        double latlon[] = CoordinateConverter.gcj2WGSExactly(lat,lng);

                        lat = latlon[0];
                        lng = latlon[1];
                        coords.add(new Coordinate(lng,lat));
                    }

                    LineString pathLine = JTSUtils.getLineString(coords,4326);
                    String pathWKT = JTSUtils.geom2wkt(pathLine);

                    List<Object> record = new ArrayList<>();
                    record.add(bus_id);
                    record.add(type);
                    record.add(name);
                    record.add(start_stop);
                    record.add(end_stop);
                    record.add(stime);
                    record.add(etime);
                    record.add(distance);
                    record.add(basic_price);
                    record.add(total_price);
                    record.add(company);
                    record.add(pathWKT);
                    busLineRecords.add(record);

                    JSONArray via_stops = object1.getJSONArray("via_stops");
                    for (int n = 0; n < via_stops.size(); n++) {

                        JSONObject object3 = via_stops.getJSONObject(n);
                        JSONObject object4 = object3.getJSONObject("location");
//						System.out.println(object3.getString("id")+","+object3.getString("name")+","+object3.getString("sequence")+","+object4.getString("lat")+","+object4.getString("lng"));

                        String stop_id = object3.getString("id");
                        String stop_name = object3.getString("name");
                        int sequence = object3.getInteger("sequence");
                        double lat = object4.getDouble("lat");
                        double lng = object4.getDouble("lng");

                        double latlon[] = CoordinateConverter.gcj2WGSExactly(lat,lng);
                        lat = latlon[0];
                        lng = latlon[1];
                        Coordinate coord = new Coordinate(lng,lat);
                        Point point = JTSUtils.getPoint(coord,4326);
                        String stopWKT = JTSUtils.geom2wkt(point);

                        List<Object> stopRecord = new ArrayList<>();
                        stopRecord.add(bus_id);
                        stopRecord.add(stop_id);
                        stopRecord.add(stop_name);
                        stopRecord.add(sequence);
                        stopRecord.add(stopWKT);

                        busStopRecords.add(stopRecord);


                    }
                }
            }

        }

        insertBusLines(busLineRecords);
        insertBusStop(busStopRecords);
    }
}
