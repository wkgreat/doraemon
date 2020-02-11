/**
 * Created by ke.wang7 on 2020/1/9.
 * 中国行政区域编码
 */
package wk.doraemon.geo.regions;

import wk.doraemon.io.TextReader;

import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

public class AdCodes implements Serializable {

    private List<List<String>> records;
    private enum FIELDS {PROV_ID,PROV_NAME,CITY_ID,CITY_NAME,DISTRICT_ID,DISTRICT_NAME,PHONE_CODE};

    //prov_id,prov_name,city_id,city_name,district_id,district_name,phone_code
    public AdCodes() {
        InputStream is = AdCodes.class.getClassLoader().getResourceAsStream("adcodes.csv");
        TextReader reader = new TextReader(is).init();
        reader.readlines();
        records = reader.getRecords(",");
        reader.close();
    }

    /**
     * 获取每个省所有的城市的编码
     * @return Map：key为中国每个省的中文名称，value为List对应每个省所有城市的编码，其中城市编码在List中是有序的
     * */
    public Map<String,List<String>> getProvinceCodeCityCodes() {

        Map<String, Set<String>> pcMap = new HashMap<>();
        records.forEach(r->{
            String prov = r.get(0).trim();
            Set<String> citys = pcMap.getOrDefault(prov, new HashSet<>());
            citys.add(r.get(2));
            pcMap.put(prov,citys);
        });
        Map<String,List<String>> pcMap2 = new HashMap<>();
        for(Map.Entry<String,Set<String>> entry : pcMap.entrySet()) {
            String k = entry.getKey();
            Set<String> citys = entry.getValue();
            List<String> cityList = new LinkedList<>(citys);
            Collections.sort(cityList);
            pcMap2.put(k,cityList);
        }
        return pcMap2;
    }

    /**
     * 获取每个省所有的城市的编码
     * @return Map：key为中国每个省的编码，value为List对应每个省所有城市的编码，其中城市编码在List中是有序的
     * */
    public Map<String,List<String>> getProvinceNameCityCodes() {

        Map<String, Set<String>> pcMap = new HashMap<>();
        records.forEach(r->{
            String prov = r.get(1);
            Set<String> citys = pcMap.getOrDefault(prov, new HashSet<>());
            citys.add(r.get(2));
            pcMap.put(prov,citys);
        });

        Map<String,List<String>> pcMap2 = new HashMap<>();
        for(Map.Entry<String,Set<String>> entry : pcMap.entrySet()) {
            String k = entry.getKey();
            Set<String> citys = entry.getValue();
            List<String> cityList = new LinkedList<>(citys);
            Collections.sort(cityList);
            pcMap2.put(k,cityList);
        }
        return pcMap2;
    }

    public String provinceNameToCode(String pname) {
        String pcode = "";
        for(List<String> r : records) {
            if(r.get(1).equals(pname)) return r.get(0);
        }
        return pcode;
    }

    public String provinceCodeToName(String pcode) {
        String pname = "";
        for(List<String> r : records) {
            if(r.get(0).equals(pcode)) return r.get(1);
        }
        return pname;
    }

    public List<String> getCityInfoByCode(String cityCode) {
        List<String> record = new ArrayList<>();
        for(List<String> r : records) {
            if(r.get(2).equals(cityCode)) {
                record = r;
                break;
            }
        }
        return record;
    }


    public static void main(String[] args) {
        AdCodes adCodes = new AdCodes();
        List<String> info = adCodes.getCityInfoByCode("341500");
        System.out.println(info);
    }

}
