package wk.doraemon.geo;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.osgeo.proj4j.BasicCoordinateTransform;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.ProjCoordinate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangKe on 2018/7/18.
 */
public class ProjUtils implements Serializable {

    private static CRSFactory crsFactory = new CRSFactory();

    private final static CoordinateReferenceSystem EPSG4326 = crsFactory.createFromName("EPSG:4326");
    private final static CoordinateReferenceSystem EPSG3857 = crsFactory.createFromName("EPSG:3857");
    private final static BasicCoordinateTransform T4326T3857 = new BasicCoordinateTransform(EPSG4326,EPSG3857);
    private final static BasicCoordinateTransform T3857T4326 = new BasicCoordinateTransform(EPSG3857,EPSG4326);

    /**
     * WGS84坐标系(EPSG:4326) -> 谷歌平面坐标系(EPSG:3857)
     * */
    public static Coordinate wgs84_webMercator(Coordinate coord) {
        ProjCoordinate target = new ProjCoordinate();
        T4326T3857.transform(new ProjCoordinate(coord.x,coord.y),target);
        return new Coordinate(target.x,target.y);
    }
    /**
     * WGS84坐标系(EPSG:4326) -> 谷歌平面坐标系(EPSG:3857)
     * */
    public static Coordinate wgs84_webMercator(double lon, double lat) {
        ProjCoordinate target = new ProjCoordinate();
        T4326T3857.transform(new ProjCoordinate(lon, lat),target);
        return new Coordinate(target.x,target.y);
    }
    /**
     * WGS84坐标系(EPSG:4326) -> 谷歌平面坐标系(EPSG:3857)
     * */
    public static LineString wgs84_webMercator(LineString lineString) {
        Coordinate[] coords = lineString.getCoordinates();
        List<Coordinate> newCoords = new ArrayList<>();
        for(Coordinate coord : coords) {
            newCoords.add(wgs84_webMercator(coord));
        }
        return JTSUtils.getLineString(newCoords,3857);
    }

    /**
     * 谷歌平面坐标系(EPSG:3857) -> WGS84坐标系(EPSG:4326)
     * */
    public static Coordinate webMercator_wgs84(Coordinate coord) {
        ProjCoordinate target = new ProjCoordinate();
        T3857T4326.transform(new ProjCoordinate(coord.x,coord.y),target);
        return new Coordinate(target.x,target.y);
    }

    /**
     * 谷歌平面坐标系(EPSG:3857) -> WGS84坐标系(EPSG:4326)
     * */
    public static Coordinate webMercator_wgs84(double x, double y) {
        ProjCoordinate target = new ProjCoordinate();
        T3857T4326.transform(new ProjCoordinate(x, y),target);
        return new Coordinate(target.x,target.y);
    }
    /**
     * 谷歌平面坐标系(EPSG:3857) -> WGS84坐标系(EPSG:4326)
     * */
    public static LineString webMercator_wgs84(LineString lineString) {
        Coordinate[] coords = lineString.getCoordinates();
        List<Coordinate> newCoords = new ArrayList<>();
        for(Coordinate coord : coords) {
            newCoords.add(webMercator_wgs84(coord));
        }
        return JTSUtils.getLineString(newCoords,4326);
    }

    /**
     * WGS84坐标系(EPSG:4326) -> GCJ02坐标系(火星坐标系)
     * */
    public static Coordinate wgs84_gcj02(Coordinate coord) {
        double[] latlon = CoordinateConverter.wgs2GCJ(coord.y,coord.x);
        return new Coordinate(latlon[1],latlon[0]);
    }
    /**
     * GCJ02坐标系(火星坐标系) -> WGS84坐标系(EPSG:4326)
     * */
    public static Coordinate gcj02_wgs84(Coordinate coord) {
        double[] latlon = CoordinateConverter.gcj2WGS(coord.y,coord.x);
        return new Coordinate(latlon[1],latlon[0]);
    }


    public static void apply_wgs84_gcj02(Coordinate coord) {
        double[] latlon = CoordinateConverter.wgs2GCJ(coord.y,coord.x);
        coord.x = latlon[1];
        coord.y = latlon[0];
    }
    public static void apply_gcj02_wgs84(Coordinate coord) {
        double[] latlon = CoordinateConverter.gcj2WGS(coord.y,coord.x);
        coord.x = latlon[1];
        coord.y = latlon[0];
    }
}
