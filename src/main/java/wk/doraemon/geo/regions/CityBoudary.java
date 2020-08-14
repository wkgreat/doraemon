/**
 * Created by ke.wang7 on 2019/12/30.
 */
package wk.doraemon.geo.regions;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wk.doraemon.geo.JTSUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @deprecated
 * */
@Deprecated
public class CityBoudary implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(CityBoudary.class);

    /**
     * 城市边界polygon的shapefile数据
     * */
    private String cityBoundarySHPPath;

    /**
     * 各市的polygon几何对象
     * */
    public Map<String, Geometry> cityGeometries = null;

    public CityBoudary(String shpPath) {
        cityBoundarySHPPath = shpPath;
        getCityGeometries();
    }

    /**
     * 从shapefile中读取各市的polygon几何对象
     * */
    private Map<String, Geometry> getCityGeometries(){

        if(cityGeometries!=null){
            return cityGeometries;
        }
        else {
            try {
                cityGeometries = new ConcurrentHashMap<>();

                File file = new File(cityBoundarySHPPath);
                Map<String,Object> map = new HashMap<>();
                map.put("url",file.toURI().toURL());
                DataStore dataStore = DataStoreFinder.getDataStore(map);
                String typeName = dataStore.getTypeNames()[0];

                FeatureSource<SimpleFeatureType, SimpleFeature> source =
                        dataStore.getFeatureSource(typeName);
                Filter filter = Filter.INCLUDE; // ECQL.toFilter("BBOX(THE_GEOM, 10,20,30,40)")

                FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(filter);
                try (FeatureIterator<SimpleFeature> features = collection.features()) {
                    while (features.hasNext()) {
                        SimpleFeature feature = features.next();
                        String citycode = feature.getProperty("CITYCODE").getValue().toString();
                        Geometry geometry = (Geometry) feature.getDefaultGeometry();
                        geometry.setSRID(4326);
                        cityGeometries.put(citycode,geometry);
                    }
                }
            } catch (IOException e) {
                LOG.error("Read ShapeFile Error: {}", e.getMessage());
                e.printStackTrace();
            }
        }
        return cityGeometries;
    }

    public String locateCity(Point point) {
        for(Map.Entry<String, Geometry> entry : getCityGeometries().entrySet()) {

            String citycode = entry.getKey();
            Geometry cityGeom = entry.getValue();
            if(point.intersects(cityGeom)) {
                return citycode;
            }
        }
        return "";
    }

    /**
     * 计算一条路线在每个市经过的距离
     * */
    public Map<String, Double> cityDistance(Geometry route) {

        Map<String, Double> cityDist = new HashMap<>();

        for(Map.Entry<String, Geometry> entry : getCityGeometries().entrySet()) {

            String citycode = entry.getKey();
            Geometry cityGeom = entry.getValue();
            if(route.intersects(cityGeom)) {
                Geometry geom = cityGeom.intersection(route);
                double dist = JTSUtils.getLengthInMeters(geom);
                cityDist.put(citycode, dist);
            }
        }
        return cityDist;
    }

    public double getTotalDistance(Map<String,Double> cityDists) {
        double d = 0;
        for(Map.Entry<String,Double> entry: cityDists.entrySet()) {
            d += entry.getValue();
        }
        return d;
    }

    public static void main(String[] args) {

        CityBoudary b = new CityBoudary("/Users/wkgreat/codes/projects/doraemon/src/main/resources/china_ad_boundary_WGS1984/city_boudary.shp");
        //System.out.println(b.cityGeometries);
        String code = b.locateCity(JTSUtils.getPoint(117,32,4326));
        System.out.println(code);

    }

}
