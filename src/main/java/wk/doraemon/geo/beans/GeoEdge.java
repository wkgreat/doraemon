package wk.doraemon.geo.beans;

import java.io.Serializable;

public class GeoEdge implements Serializable {

    private GeoPoint start;
    private GeoPoint end;

    public GeoEdge(double lon1, double lat1, double lon2, double lat2) {
        this.start = new GeoPoint(lon1,lat1);
        this.end = new GeoPoint(lon2, lat2);
    }

    public GeoEdge(GeoPoint start, GeoPoint end) {
        this.start = start;
        this.end = end;
    }

    public GeoPoint getStart() {
        return start;
    }

    public void setStart(GeoPoint start) {
        this.start = start;
    }

    public GeoPoint getEnd() {
        return end;
    }

    public void setEnd(GeoPoint end) {
        this.end = end;
    }
}
