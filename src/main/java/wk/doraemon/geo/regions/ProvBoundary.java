package wk.doraemon.geo.regions;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wk.doraemon.geo.JTSUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProvBoundary {

    private final static Logger LOG = LoggerFactory.getLogger(ProvBoundary.class);

    /**
     * 城市边界polygon的shapefile数据
     * */
    String provBoundarySHPPath;
    /**
     * 各市的polygon几何对象
     * */
    private Map<String, Geometry> provGeometries = null;

    public ProvBoundary(String path) {
        provBoundarySHPPath = path;
    }

    /**
     * 从shapefile中读取各市的polygon几何对象
     * */
    private Map<String, Geometry> getProvGeometries(){

        if(provGeometries!=null){
            return provGeometries;
        }
        else {
            try {
                provGeometries = new ConcurrentHashMap<>();

                File file = new File(provBoundarySHPPath);
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
                        Object cc = feature.getProperty("code").getValue();
                        if(cc!=null) {
                            String citycode = cc.toString();
                            if(!citycode.isEmpty()) {
                                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                                geometry.setSRID(4326);
                                provGeometries.put(citycode,geometry);
                                System.out.println(citycode);
                                //System.out.println(JTSUtils.geom2wkt(geometry));
                            }

                        }
                    }
                }
            } catch (IOException e) {
                LOG.error("Read ShapeFile Error: {}", e.getMessage());
                e.printStackTrace();
            }
        }
        return provGeometries;
    }

    /**
     * 计算一条路线在每个市经过的距离
     * */
    public Map<String, Double> provDistance(Geometry route) {

        Map<String, Double> cityDist = new HashMap<>();

        for(Map.Entry<String, Geometry> entry : getProvGeometries().entrySet()) {

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

}
