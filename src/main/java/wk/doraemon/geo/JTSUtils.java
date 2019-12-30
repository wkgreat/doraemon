package wk.doraemon.geo;

import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wk.doraemon.collection.ListUtil;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by WangKe on 2018/7/18.
 * JTS (JAVA Topology Suite) 封装
 */
public class JTSUtils implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(JTSUtils.class);

    public final static String POINT = "Point";
    public final static String LINESTRING = "LineString";
    public final static String POLYGON = "Polygon";
    public final static String MULTIPOINT = "MultiPoint";
    public final static String MULTILINESTRING = "MultiLineString";
    public final static String MULTIPOLYGON = "MultiPolygon";
    public final static String GEOMETRYCOLLECTION = "GeometryCollection";
    public final static String LINEARRING = "LinearRing";

    /**
     * EPSG:4326 WGS-84坐标系几何构造工厂类
     * */
    public final static GeometryFactory FACTORY_WGS84 = new GeometryFactory(new PrecisionModel(),4326);

    /**
     * EPSG:3857 谷歌平面坐标系几何构造工厂类
     * */
    public final static GeometryFactory FACTORY_3857 = new GeometryFactory(new PrecisionModel(),3857);

    /**
     * 按照srid 获取对应坐标系的几何构造工厂类
     * */
    private static GeometryFactory getFactory(int srid) {
        return new GeometryFactory(new PrecisionModel(),srid);
    }

    /**
     * 获取点要素
     * @param x x坐标
     * @param y y坐标
     * @param srid 坐标系
     * */
    public static Point getPoint(double x, double y, int srid) {
        return getPoint(new Coordinate(x,y),srid);
    }

    /**
     * 获取WGS-84点要素
     * @param coord 坐标
     * */
    public static Point getPoint(Coordinate coord) {
        return FACTORY_WGS84.createPoint(coord);
    }

    /**
     * 获取点要素
     * @param coord 坐标
     * @param srid 坐标系
     * */
    public static Point getPoint(Coordinate coord, int srid) {
        return getFactory(srid).createPoint(coord);
    }

    /**
     * 获取多点要素,默认WGS-84坐标系
     * @param coords 多个点坐标的列表
     * */
    public static MultiPoint getMultiPoint(List<Coordinate> coords) {

        int theLength = coords.size();
        Coordinate[] coordinates = new Coordinate[theLength];
        coordinates = coords.toArray(coordinates);

        return FACTORY_WGS84.createMultiPoint(coordinates);
    }

    /**
     * 获取多点要素
     * @param coords 多个点坐标的列表
     * @param srid 坐标系
     * */
    public static MultiPoint getMultiPoint(List<Coordinate> coords, int srid) {

        int theLength = coords.size();
        Coordinate[] coordinates = new Coordinate[theLength];
        coordinates = coords.toArray(coordinates);

        return getFactory(srid).createMultiPoint(coordinates);
    }

    /**
     * 获取线要素，默认WGS-84坐标
     * @param coords 线要素节点坐标列表
     * */
    public static LineString getLineString(List<Coordinate> coords) {
        int theLength = coords.size();
        Coordinate[] coordinates = new Coordinate[theLength];
        coordinates = coords.toArray(coordinates);
        return FACTORY_WGS84.createLineString(coordinates);
    }

    /**
     * 获取线要素
     * @param coords 线要素节点坐标列表
     * @param srid 坐标系
     * */
    public static LineString getLineString(List<Coordinate> coords, int srid) {
        int theLength = coords.size();
        Coordinate[] coordinates = new Coordinate[theLength];
        coordinates = coords.toArray(coordinates);
        return getFactory(srid).createLineString(coordinates);
    }

    /**
     * 计算凸壳
     * @param coords 多个点的坐标列表
     * */
    public static Geometry getConvexHull(List<Coordinate> coords) {

        MultiPoint points = getMultiPoint(coords);
        ConvexHull convexHull = new ConvexHull(points);
        Geometry hull = convexHull.getConvexHull();

        return hull;
    }

    /**
     * 计算凸壳
     * @param points 多个点要素的列表
     * */
    public static Geometry getConvexHullFromPoints(List<Point> points) {
        List<Coordinate> coords = ListUtil.map(points, Point::getCoordinate);
        return getConvexHull(coords);
    }

    /**
     * 计算凸壳
     * @param geometry 几何要素
     * */
    public static Geometry getConvexHull(Geometry geometry) {

        ConvexHull convexHull = new ConvexHull(geometry);
        Geometry hull = convexHull.getConvexHull();

        return hull;
    }

    /**
     * 几何要素是否为空
     * */
    public static boolean isEmpty(Geometry geometry) {
        return geometry==null || geometry.isEmpty();
    }

    /**
     * 几何要素转线要素
     * */
    public static LineString geom2LineString(Geometry geometry) {
        if(geometry==null) {
            return null;
        }
        return getLineString(Arrays.asList(geometry.getCoordinates()),geometry.getSRID());
    }

    /**
     * 获取几何要素的WKT
     * */
    public static String geom2wkt(Geometry geometry) {
        if(geometry==null || geometry.isEmpty()) {
            return "NULL";
        } else {
            return new WKTWriter().write(geometry);
        }
    }

    /**
     * 根据WKT生成几何要素
     * */
    public static Geometry wkt2geom(String wkt) {
        Geometry geometry = null;
        if(StringUtils.isBlank(wkt)) {
            return null;
        } else {
            try {
                geometry = new WKTReader().read(wkt);
            } catch (ParseException e) {
                geometry = null;
                e.printStackTrace();
            } finally {
                return geometry;
            }
        }
    }

    /**
     * 获取几何要素的WKB
     * */
    public static byte[] geom2wkb(Geometry geometry) {
        return new WKBWriter().write(geometry);
    }

    /**
     * 根据WKB生成几何要素
     * */
    public static Geometry wkb2geom(byte[] wkb) {
        Geometry geometry = null;
        try {
            geometry = new WKBReader().read(wkb);
        } catch (ParseException e) {
            geometry = null;
            e.printStackTrace();
        } finally {
            return geometry;
        }
    }

    /**
     * 将 WGS-84 坐标系下的几何要素 转换为 GCJ02 坐标系
     * */
    public static Geometry toGcj02(Geometry geometry) {
        Geometry $geometry = (Geometry) geometry.clone();
        $geometry.apply(new CoordinateFilter() {
            @Override
            public void filter(Coordinate coordinate) {
                ProjUtils.apply_wgs84_gcj02(coordinate);
            }
        });
        return $geometry;
    }

    /**
     * 显示几何要素的所有点坐标
     * */
    public static void showCoords(Geometry geometry) {
        for(Coordinate coord : geometry.getCoordinates()) {
            System.out.println(coord.y+","+coord.x+","+geometry.getGeometryType());
        }
    }

    public static double getLengthInMeters(Geometry geometry) {
        double d = 0;
        String gtype = geometry.getGeometryType();
        switch (gtype) {
            case POINT:
                LOG.warn("Calulate POINT Length as 0!");
                d += 0;
                break;
            case LINESTRING:
                d += getLengthInMeters((LineString) geometry);
                break;
            case POLYGON:
                LOG.warn("Calulate POLYGON Length as 0!");
                d += 0;
                break;
            case MULTIPOINT:
                LOG.warn("Calulate MULTIPOINT Length as 0!");
                d += 0;
                break;
            case MULTILINESTRING:
                d += getLengthInMeters((MultiLineString) geometry);
                break;
            case MULTIPOLYGON:
                LOG.warn("Calulate MULTIPOLYGON Length as 0!");
                d += 0;
                break;
            case GEOMETRYCOLLECTION:
                d += getLengthInMeters((GeometryCollection) geometry);
                break;
        }
        return d;
    }

    public static double getLengthInMeters(GeometryCollection geometryCollection) {
        int d = 0;
        for(int i=0; i<geometryCollection.getNumGeometries(); ++i) {
            Geometry g = geometryCollection.getGeometryN(i);
            d+=getLengthInMeters(g);
        }
        return d;
    }


    public static double getLengthInMeters(LineString lineString) {
        double distance = 0;
        Coordinate[] coords = lineString.getCoordinates();
        if(coords.length<2) {
            return 0;
        }
        for(int i=1; i<coords.length; ++i) {
            distance += getDistance(coords[i],coords[i-1],lineString.getSRID());
        }
        return distance;
    }

    public static double getLengthInMeters(MultiLineString lineStrings) {
        double length = 0;
        for(int i=0; i<lineStrings.getNumGeometries(); i++) {
            length += getLengthInMeters((LineString) lineStrings.getGeometryN(i));
        }
        return length;
    }

    /**
     * 获取距离，单位米m
     * */
    public static double getDistance(Coordinate coord1, Coordinate coord2, int srid) {
        double d = 0;
        if(srid==4326 || srid==0) {
            d = GeoUtils.WGS84.getDistance(coord1.y,coord1.x,coord2.y,coord2.x);
        } else {
            d = coord1.distance(coord2);
        }
        return d;
    }
}
