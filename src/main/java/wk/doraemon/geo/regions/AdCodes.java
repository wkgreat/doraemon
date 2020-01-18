/**
 * Created by ke.wang7 on 2020/1/9.
 * 中国行政区域编码
 */
package wk.doraemon.geo.regions;

import wk.doraemon.io.TextReader;

import java.io.InputStream;
import java.util.*;

public class AdCodes {

    private static List<List<String>> records;

    static {
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
    public static Map<String,List<String>> getProvinceCodeCityCodes() {

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
    public static Map<String,List<String>> getProvinceNameCityCodes() {

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

    public static String provinceNameToCode(String pname) {
        String pcode = "";
        for(List<String> r : records) {
            if(r.get(1).equals(pname)) return r.get(0);
        }
        return pcode;
    }

    public static String provinceCodeToName(String pcode) {
        String pname = "";
        for(List<String> r : records) {
            if(r.get(0).equals(pcode)) return r.get(1);
        }
        return pname;
    }



}
