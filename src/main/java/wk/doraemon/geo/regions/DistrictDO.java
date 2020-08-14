package wk.doraemon.geo.regions;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.locationtech.jts.geom.Geometry;

import java.util.Arrays;

/**
 * @deprecated
 * */
@Deprecated
@Getter
@Setter
public class DistrictDO {

    private String prov_id;
    private String prov_name;
    private String city_id;
    private String city_name;
    private String district_id;
    private String district_name;
    private String phone_code;

    private Geometry the_geom;

    @Override
    public String toString() {
        return String.join(",", Arrays.asList(
                prov_id,
                prov_name,
                city_id,
                city_name,
                district_id,
                district_name,
                phone_code
        ));
    }
}
