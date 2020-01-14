package wk.doraemon.geo.beans;

import java.io.Serializable;
import java.util.Objects;

public class GeoPoint implements Serializable {

    public double lon;
    public double lat;
    public double ele;

    public GeoPoint(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
        this.ele = 0;
    }

    public GeoPoint(double lon, double lat, double ele) {
        this.lon = lon;
        this.lat = lat;
        this.ele = ele;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoPoint geoPoint = (GeoPoint) o;
        return Double.compare(geoPoint.lon, lon) == 0 &&
                Double.compare(geoPoint.lat, lat) == 0 &&
                Double.compare(geoPoint.ele, ele) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lon, lat, ele);
    }

}
